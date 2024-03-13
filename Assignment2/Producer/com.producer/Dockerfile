FROM openjdk:19-jdk-oracle
ARG JAR-FILE=target/*.jar
COPY ./target/producer-0.0.1-SNAPSHOT.jar.original app.jar
COPY ./sensor.csv sensor.csv
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]

