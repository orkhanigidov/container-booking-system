FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY settings.gradle .
COPY booking-service/build.gradle booking-service/
COPY booking-service/src booking-service/src
RUN ./gradlew :booking-service:bootJar --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/booking-service/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]