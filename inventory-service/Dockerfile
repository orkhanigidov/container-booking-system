FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY settings.gradle .

COPY booking-service/build.gradle booking-service/
COPY inventory-service/build.gradle inventory-service/
COPY payment-service/build.gradle payment-service/
COPY notification-service/build.gradle notification-service/

COPY inventory-service/src inventory-service/src
RUN ./gradlew :inventory-service:bootJar --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/inventory-service/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]