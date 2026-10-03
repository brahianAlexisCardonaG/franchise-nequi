FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace
COPY gradlew settings.gradle build.gradle lombok.config ./
COPY gradle gradle
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon > /dev/null
COPY src src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S franchise && adduser -S franchise -G franchise
WORKDIR /app
COPY --from=build --chown=franchise:franchise /workspace/build/libs/franchise-0.0.1-SNAPSHOT.jar app.jar
USER franchise
EXPOSE 8085
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget -qO- http://localhost:8085/actuator/health || exit 1
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
