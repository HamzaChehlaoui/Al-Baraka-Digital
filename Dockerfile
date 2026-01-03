FROM maven:3.9-eclipse-temurin-17-alpine AS build

# Set working directory
WORKDIR /app

COPY pom.xml ./

# Download dependencies (cached layer if pom.xml hasn't changed)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for Docker build)
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Create a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Copy the JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Create directory for uploads and set ownership
RUN mkdir -p /app/uploads && chown -R spring:spring /app

# Change to non-root user
USER spring:spring

# Expose port
EXPOSE 8080

# Environment variables (can be overridden)
ENV JAVA_OPTS="-Xmx512m -Xms256m" \
    JWT_SECRET="" \
    DB_URL="" \
    DB_USER="" \
    DB_PASSWORD=""

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
