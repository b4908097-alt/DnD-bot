FROM maven:3.8.6-eclipse-temurin-17 AS build
COPY . /app
WORKDIR /app
RUN mvn clean compile assembly:single -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*-jar-with-dependencies.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
