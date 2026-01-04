# Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copier le JAR
COPY target/*.jar app.jar

# Exposer le port
EXPOSE 8080

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]