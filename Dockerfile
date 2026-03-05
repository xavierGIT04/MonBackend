# Étape 1 : Build (Utilisation de Maven avec Eclipse Temurin 17)
FROM maven:3.8.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Étape 2 : Runtime (Utilisation de l'image JRE légère d'Eclipse Temurin)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Optimisation de la mémoire pour éviter que Render ne coupe l'app (OOM)
ENV JAVA_OPTS="-Xmx300m -Xss512k"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
