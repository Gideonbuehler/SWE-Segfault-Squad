# ---- Build ----
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

COPY . .

RUN gradle build

# ---- Run ----
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 7000

ENTRYPOINT ["java", "-jar", "app.jar"]