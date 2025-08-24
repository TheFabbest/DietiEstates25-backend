# Build stage
FROM --platform=linux/amd64 maven:3.8-openjdk-17 AS build
WORKDIR /app

# Copy only pom.xml first to leverage Docker cache for dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Now copy the source code
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM --platform=linux/amd64 openjdk:17-jdk-slim
WORKDIR /app

# Install dependencies in a single layer to reduce image size
# RUN apt-get update && \
#     apt-get install -y --no-install-recommends fontconfig libfreetype6 && \
#     rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
