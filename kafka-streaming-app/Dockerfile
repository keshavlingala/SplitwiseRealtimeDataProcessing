FROM hseeberger/scala-sbt:11.0.6_1.3.10_2.13.1

WORKDIR /app

COPY . .

RUN sbt compile


ENV KEYSPACE=splitwise_analysis
ENV CASSANDRA_HOST=localhost
ENV KAFKA_SERVERS=localhost:9092
ENV KAFKA_TOPIC=expenses

CMD ["sbt", "run"]
