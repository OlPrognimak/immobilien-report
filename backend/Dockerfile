FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./pom.xml
COPY backend/pom.xml ./backend/pom.xml
COPY frontend/pom.xml ./frontend/pom.xml
RUN mvn -q -pl backend -am -DskipTests dependency:go-offline
COPY backend/src ./backend/src
RUN mvn -q -pl backend -am verify

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/backend/target/immobilien-report-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
