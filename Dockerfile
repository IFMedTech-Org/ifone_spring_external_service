# syntax=docker/dockerfile:1.4
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Accept JAR name as build arg
ARG JAR_NAME
COPY target/${JAR_NAME} app.jar

EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]
