# Build stage
FROM --platform=linux/amd64 maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM --platform=linux/amd64 openjdk:17-slim
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends fontconfig libfreetype6 && rm -rf /var/lib/apt/lists/*
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
