# Use a slim JDK
FROM eclipse-temurin:21-jdk-jammy as build
WORKDIR /app

# Copy and build your Spring Boot JAR
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# Runtime image
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"] 