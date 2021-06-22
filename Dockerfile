# syntax=docker/dockerfile:1
FROM openjdk:8

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw clean
RUN ./mvnw compile
CMD ["./mvnw", "exec:java"]