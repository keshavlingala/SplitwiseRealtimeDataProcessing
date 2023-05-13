# Stage 1: Build the application
FROM openjdk:17-alpine as build
WORKDIR /app
COPY --chown=gradle:gradle . /app
RUN chmod +x ./gradlew && ./gradlew build --no-daemon -x test

# Stage 2: Run the application
FROM openjdk:17-alpine
EXPOSE 8080
RUN mkdir /app
COPY --from=build /app/build/libs/*.jar /app/spring-app.jar
ENTRYPOINT ["java", "-jar", "/app/spring-app.jar"]
