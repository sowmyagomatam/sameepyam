# --- Build stage: compile and package the jar (no Maven wrapper in the repo, so use a Maven image) ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Resolve dependencies first so this layer is cached unless pom.xml changes.
COPY pom.xml .
RUN mvn -q -e -B dependency:go-offline
COPY src ./src
# Skip tests in the image: the eval test needs OPENAI_API_KEY and would call the live API.
RUN mvn -q -e -B clean package -DskipTests

# --- Runtime stage: small JRE, just the jar ---
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# Document the default; the host overrides via $PORT (see application.yml server.port).
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]