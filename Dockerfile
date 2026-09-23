# Build the executable Spring Boot JAR with the project's Java 25 toolchain.
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew

# Resolve dependencies before application sources are copied so this layer can be cached.
RUN ./gradlew dependencies --no-daemon

COPY src src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

ARG DB_URL
ARG DB_USERNAME=postgres
ARG DB_PASSWORD=admin
ARG CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173

ENV DB_URL=${DB_URL} \
    DB_USERNAME=${DB_USERNAME} \
    DB_PASSWORD=${DB_PASSWORD} \
    CORS_ALLOWED_ORIGINS=${CORS_ALLOWED_ORIGINS}

RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=build --chown=spring:spring /workspace/build/libs/*.jar app.jar

USER spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
