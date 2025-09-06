# Build stage
FROM maven:3.9.11-eclipse-temurin AS build
WORKDIR /app

# Copy pom first to leverage cache
COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline

# Copy sources and build
COPY src ./src
RUN mvn -B clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jdk-jammy AS runtime
WORKDIR /app

# Copy application artifact
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Run as non-root if available (remove if causes permission issues on macOS Docker)
USER 8080:8080

ENTRYPOINT ["java", "-jar", "app.jar"]
