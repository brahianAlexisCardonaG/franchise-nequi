FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace
COPY gradlew settings.gradle build.gradle main.gradle gradle.properties lombok.config ./
COPY gradle gradle
COPY applications/app-service/build.gradle applications/app-service/
COPY domain/model/build.gradle domain/model/
COPY domain/usecase/build.gradle domain/usecase/
COPY infrastructure/driven-adapters/r2dbc-postgresql/build.gradle infrastructure/driven-adapters/r2dbc-postgresql/
COPY infrastructure/entry-points/reactive-web/build.gradle infrastructure/entry-points/reactive-web/
RUN chmod +x gradlew && ./gradlew :app-service:dependencies --no-daemon > /dev/null
COPY applications applications
COPY domain domain
COPY infrastructure infrastructure
RUN ./gradlew :app-service:bootJar --no-daemon

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S franchise && adduser -S franchise -G franchise
WORKDIR /app
COPY --from=build --chown=franchise:franchise /workspace/applications/app-service/build/libs/franchise.jar app.jar
USER franchise
EXPOSE 8085
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget -qO- http://localhost:8085/actuator/health || exit 1
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
