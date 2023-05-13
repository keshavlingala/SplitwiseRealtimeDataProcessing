name := "SparkAnalysis"

version := "0.1"

scalaVersion := "2.12.14"

libraryDependencies ++= Seq(
  "com.google.guava" % "guava" % "31.1-jre",
  "org.apache.spark" %% "spark-core" % "3.2.0",
  "org.apache.spark" %% "spark-sql" % "3.2.0",
  "org.apache.spark" %% "spark-streaming" % "3.2.0",
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % "3.2.0",
  "com.datastax.spark" %% "spark-cassandra-connector" % "3.2.0",
  "com.datastax.oss" % "java-driver-core" % "4.14.0",
  "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.2.0",
  "joda-time" % "joda-time" % "2.10.12"
)
