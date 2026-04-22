FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY settings.gradle .
COPY build.gradle .

COPY booking-service/build.gradle booking-service/
COPY inventory-service/build.gradle inventory-service/
COPY payment-service/build.gradle payment-service/
COPY notification-service/build.gradle notification-service/

RUN ./gradlew :payment-service:dependencies --no-daemon

COPY payment-service/src payment-service/src

RUN ./gradlew :payment-service:bootJar --no-daemon

RUN java -Djarmode=layertools -jar payment-service/build/libs/*.jar extract --destination extracted

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/extracted/dependencies/ ./
COPY --from=builder /app/extracted/spring-boot-loader/ ./
COPY --from=builder /app/extracted/snapshot-dependencies/ ./
COPY --from=builder /app/extracted/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]