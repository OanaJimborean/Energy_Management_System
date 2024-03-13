FROM openjdk:19-jdk-oracle
ARG JAR-FILE=target/*.jar
COPY ./target/com.chat.application-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "/app.jar"]

