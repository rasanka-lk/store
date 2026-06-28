# Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY ./build/libs/store-1.0.0-SNAPSHOT.jar /app/store-app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "store-app.jar"]