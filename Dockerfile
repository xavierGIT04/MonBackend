# Étape 1 : Build
FROM maven:3.8.4-openjdk-17-slim AS build
COPY . .
RUN mvn clean package -DskipTests

# Étape 2 : Run
FROM openjdk:17-jdk-slim
COPY --from=build /target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]