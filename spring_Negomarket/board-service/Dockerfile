FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY target/board-service-1.0.jar Boardservice.jar
ENTRYPOINT ["java", "-jar", "Boardservice.jar"]