# Stage 1: Build the application
FROM maven:3.8.6-openjdk-17 AS builder

# Set working directory
WORKDIR /app

# Copy Maven configuration and download dependencies (cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:17-jre-slim

# Set working directory
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/Approbation-0.0.1-SNAPSHOT.jar app.jar

# Install dependencies for JasperReports (e.g., fonts)
RUN apt-get update && apt-get install -y --no-install-recommends \
    fontconfig \
    fonts-dejavu \
    && rm -rf /var/lib/apt/lists/*

# Run as non-root user for security
RUN useradd -m springuser
USER springuser

# Expose the default Spring Boot port
EXPOSE 8080

# Define health check (optional, for Kubernetes)
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application with optimized JVM flags
ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]