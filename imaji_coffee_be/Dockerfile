FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -Dmaven.test.skip=true package

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV JAVA_TOOL_OPTIONS="-Xms64m -Xmx256m -Xss256k -XX:MaxMetaspaceSize=128m -XX:MaxDirectMemorySize=32m -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError"

EXPOSE 10000

ENTRYPOINT ["java", "-jar", "app.jar"]
