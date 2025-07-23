FROM eclipse-temurin:24-jdk AS build
WORKDIR /app
COPY . .
# Ensure mvnw is executable
RUN chmod +x mvnw
# Verify maven-wrapper.jar exists
RUN ls -l .mvn/wrapper/
# Build the JAR
RUN ./mvnw clean package -DskipTests
# Debug: List contents of target/
RUN ls -l /app/target/

FROM eclipse-temurin:24-jre
WORKDIR /app
# Copy the specific JAR
COPY --from=build /app/target/Y-0.0.1-SNAPSHOT.jar app.jar
# Debug: Verify app.jar exists
RUN ls -l /app/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]