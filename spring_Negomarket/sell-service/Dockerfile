FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY target/sell-service-1.0.jar Sellservice.jar
ENTRYPOINT ["java", "-jar", "Sellservice.jar"]