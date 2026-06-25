FROM openjdk:17
WORKDIR /app
COPY ./target/store.jar /app
EXPOSE 8080
CMD ["java", "-jar", "store.jar"]