import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.functions.date_format
import org.apache.spark.sql.{SparkSession, functions}

object SparkAnalysis {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession
      .builder()
      .appName("Spark Analysis")
      .master("local[*]")
      .getOrCreate()

    Logger.getRootLogger.setLevel(Level.ERROR)
    val keyspace = sys.env.getOrElse("KEYSPACE", "splitwise_analysis")
    val table = sys.env.getOrElse("TABLE", "simplified_expenses")
    val cassandraHost = sys.env.getOrElse("CASSANDRA_HOST", "localhost")
    val cassandraPort = sys.env.getOrElse("CASSANDRA_PORT", "9042")
    //    Reading Data from Cassandra
    val df = spark.read
      .format("org.apache.spark.sql.cassandra")
      .option("keyspace", keyspace)
      .option("table", table)
      .option("spark.cassandra.connection.host", cassandraHost)
      .option("spark.cassandra.connection.port", cassandraPort)
      .load()
    
    
    // All expenses in Detail
    df
      .write
      .format("csv")
      .option("header", "true")
      .mode("overwrite")
      .save("output/detailed_expenses")
    // Group wise expenses
    df.where("payment = false")
      .groupBy("user_name", "group_id")
      .agg(functions.sum("cost").as("SumOfCosts"))
      .orderBy("SumOfCosts")
      .write
      .format("csv")
      .option("header", "true")
      .mode("overwrite")
      .save("output/group_wise_expenses")

    // Expenses vs Payments
    df.withColumn("month", date_format(df("date"), "MMMM"))
      .withColumn("year", date_format(df("date"), "yyyy"))
      .groupBy("month", "year", "user_name", "payment")
      .sum("cost").orderBy("year", "month")
      .write
      .format("csv")
      .option("header", "true")
      .mode("overwrite")
      .save("output/expenses_vs_payments")
    // Monthly Expenses by Category per User

    df.withColumn("month", date_format(df("date"), "MMMM"))
      .withColumn("year", date_format(df("date"), "yyyy"))
      .where("payment = false")
      .groupBy("month", "year", "category", "user_name")
      .agg(functions.sum("cost").as("Amount Spent"))
      .orderBy("year", "month")
      .write.format("csv")
      .option("header", "true")
      .mode("overwrite")
      .save("output/monthly_expenses")

  }
}
