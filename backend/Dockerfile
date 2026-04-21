# ---- Build ----
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY . .

RUN apt-get update && apt-get install -y dos2unix

RUN dos2unix ./gradlew

RUN ./gradlew build

# ---- Run ----
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 7000

ENTRYPOINT ["java", "-jar", "app.jar"]