# Use a minimal OpenJDK base image
FROM openjdk:21-jdk-slim

# Create and set working directory
WORKDIR /app

# Copy the built jar from Maven target directory
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose port (Spring Boot default)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
