FROM maven:3.8.6-eclipse-temurin-17 AS build
COPY . /app
WORKDIR /app
RUN mvn clean dependency:copy-dependencies package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/app.jar /app/app.jar
COPY --from=build /app/target/dependency /app/dependency
ENTRYPOINT ["java", "-cp", "app.jar:dependency/*", "org.example.Main"]
