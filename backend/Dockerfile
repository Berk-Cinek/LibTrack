FROM gradle:jdk26 AS build
WORKDIR /app
COPY . .
RUN --mount=type=cache,target=/home/gradle/.gradle \
        gradle bootJar --no-daemon

FROM eclipse-temurin:26-jre
WORKDIR /app
RUN groupadd --system spring && useradd --system --gid spring spring
USER spring
COPY --chown=spring:spring --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]