FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:21

WORKDIR /app

COPY --from=build /app/target/telegram-bot-category-tree-0.0.1-SNAPSHOT.jar telegram-bot.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/telegram-bot.jar"]