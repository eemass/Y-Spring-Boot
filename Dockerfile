# ---------- Build Stage ----------
FROM eclipse-temurin:21-jdk-jammy AS builder

# Set working directory
WORKDIR /app

# Copy everything into the container
COPY . .

# Build the application using Maven
RUN ./mvnw clean package -DskipTests


# ---------- Runtime Stage ----------
FROM eclipse-temurin:21-jre-jammy

# Set working directory for runtime container
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port 8080 (default for Spring Boot)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
