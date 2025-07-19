# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN ./mvnw --quiet clean package -Dspring.profiles.active=ci -Pci
# -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar order-service.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "order-service.jar", "--spring.profiles.active=pre"]
