# Use an official OpenJDK image to build and run the app
FROM eclipse-temurin:21-jdk AS build

# Set working directory
WORKDIR /app

# Copy your project files
COPY . .

# Build the project (skip tests for speed)
RUN ./mvnw clean package -DskipTests

# ---- Run Stage ----
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 to the outside world
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
