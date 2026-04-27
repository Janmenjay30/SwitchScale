FROM maven:3.9.11-eclipse-temurin-25 AS build

WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN mvn -DskipTests clean package && cp target/*.jar /app/app.jar

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/app.jar ./app.jar

EXPOSE 8001

ENTRYPOINT ["java", "-jar", "app.jar"]