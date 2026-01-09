# Stage 1: Build app
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run app
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# copy jar từ stage 1
COPY --from=builder /app/target/*.jar app.jar

# copy file .env vào container
# COPY --from=builder /app/.env .env

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
