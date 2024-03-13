FROM openjdk:19-jdk-oracle
ARG JAR-FILE=target/*.jar
COPY ./target/consumer-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app.jar"]
