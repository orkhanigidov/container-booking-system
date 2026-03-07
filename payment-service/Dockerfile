FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY settings.gradle .
COPY payment-service/build.gradle payment-service/
COPY payment-service/src payment-service/src
RUN ./gradlew :payment-service:bootJar --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/payment-service/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]