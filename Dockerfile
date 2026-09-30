# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jdk AS development
WORKDIR /workspace
COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew
EXPOSE 8080
CMD ["./gradlew", "bootRun", "--no-daemon"]

FROM development AS production-build
COPY src ./src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre AS production
WORKDIR /app
RUN addgroup --system spring && adduser --system --ingroup spring spring
COPY --from=production-build /workspace/build/libs/*.jar app.jar
USER spring:spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
