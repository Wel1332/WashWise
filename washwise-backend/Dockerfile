# syntax=docker/dockerfile:1.7

# ---------- Build stage ----------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Resolve dependencies first so they're cached across builds when only sources change
COPY .mvn .mvn
COPY mvnw mvnw.cmd pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -ntp -DskipTests dependency:go-offline

# Build the fat jar
COPY src src
RUN ./mvnw -B -ntp -DskipTests clean package

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the produced jar (uses a wildcard so we don't pin the version filename)
COPY --from=build /app/target/*.jar app.jar

# Render injects PORT; Spring reads it via ${PORT:8080} in application.properties.
# -Xmx450m keeps us under the 512 MB free-tier ceiling with headroom for native overhead.
ENV JAVA_OPTS="-Xmx450m -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
