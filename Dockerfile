# Build
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Cache dependencies
COPY backend/pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of code
COPY backend .
RUN mvn clean package -DskipTests

# Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
