FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY target/discovery-service-1.0.jar Discovery-service.jar
ENTRYPOINT ["java", "-jar", "Discovery-service.jar"]