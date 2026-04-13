FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app
COPY . . 
WORKDIR /app/backend/slotify/spring
RUN ./mvnw clean package -DskipTests


FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/backend/slotify/spring/target/*.jar app.jar

ENTRYPOINT ["java", "-XX:+UseZGC", "-XX:+ZGenerational", "-jar", "app.jar"]