# ---- Build ----
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY . .
COPY .env .env

RUN apt-get update && apt-get install -y dos2unix

RUN dos2unix ./gradlew

RUN ./gradlew shadowJar

# ---- Run ----
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/libs/backend-1.0-SNAPSHOT.jar app.jar
COPY --from=builder /app/.env .env
COPY --from=builder /app/data_wolfe.json data_wolfe.json

EXPOSE 7000

ENTRYPOINT ["java", "-jar", "app.jar"]