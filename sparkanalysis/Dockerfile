FROM hseeberger/scala-sbt:11.0.6_1.3.10_2.13.1

WORKDIR /app

COPY . .

RUN sbt compile

# Environment variables for Scala application
ENV KEYSPACE=splitwise_analysis
ENV TABLE=simplified_expenses
ENV CASSANDRA_HOST=localhost
ENV CASSANDRA_PORT=9042

CMD ["sbt", "run"]
