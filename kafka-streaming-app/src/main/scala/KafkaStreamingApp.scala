
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.cassandra.DataFrameWriterWrapper
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{StringType, StructType}
import org.apache.spark.sql.{DataFrame, SparkSession}

object KafkaStreamingApp {
  val spark: SparkSession = SparkSession.builder()
    .appName("KafkaStreamingApp")
    .master("local[*]")
    .getOrCreate()

  val cassandraConfig = Map(
    "keyspace" -> sys.env.getOrElse("KEYSPACE", "splitwise_analysis"),
    "spark.cassandra.connection.host" -> sys.env.getOrElse("CASSANDRA_HOST", "localhost")
  )

  def main(args: Array[String]): Unit = {

     Logger.getRootLogger.setLevel(Level.WARN)

    val kafkaServers = sys.env.getOrElse("KAFKA_SERVERS", "localhost:9092")
    val topicName = sys.env.getOrElse("KAFKA_TOPIC", "expenses")

    spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaServers)
      .option("subscribe", topicName)
      .load()
      .selectExpr("CAST(value AS STRING)")
      .select(
        get_json_object(col("value"), "$.id").as("expense_id"),
        get_json_object(col("value"), "$.user_id").as("user_id"),
        get_json_object(col("value"), "$.user_name").as("user_name"),
        get_json_object(col("value"), "$.category.name").as("category"),
        get_json_object(col("value"), "$.cost").as("cost"),
        coalesce(get_json_object(col("value"), "$.group_id"), lit("0")).as("group_id"),
        //        get_json_object(col("value"), "$.group_id").as("group_id"),
        get_json_object(col("value"), "$.description").as("description"),
        get_json_object(col("value"), "$.payment").as("payment"),
        get_json_object(col("value"), "$.date").as("date"),
        get_json_object(col("value"), "$.created_by.id").as("created_by"),
        col("value").as("payload"),
      )
      .withColumn("created_at", current_timestamp())
      .writeStream
      .foreachBatch { (batchDF: DataFrame, batchId: Long) =>
        batchDF
          .show(false)
        batchDF
          .write
          .cassandraFormat("simplified_expenses", "splitwise_analysis")
          .mode("append")
          .options(cassandraConfig)
          .save()
      }
      .start()
      .awaitTermination()
  }

  def processPingDF(): Unit = {

    val pingDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "ping")
      .load()
    val schema = new StructType()
      .add("message", StringType)

    val jsonDf = pingDF
      .select(from_json(col("value").cast(StringType), schema).as("data"))
      .select("data.message")

    jsonDf
      .writeStream
      .foreachBatch { (batchDF: DataFrame, batchId: Long) =>
        batchDF
          .selectExpr("uuid() as id", "to_json(struct(*)) as message")
          .write
          .cassandraFormat("ping_table", "splitwise_analysis")
          .mode("append")
          .options(cassandraConfig)
          .save()
      }
      .start()
      .awaitTermination()
  }
}
