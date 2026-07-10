FROM gradle:jdk26 AS build
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon

From eclipse-temurin:26-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]