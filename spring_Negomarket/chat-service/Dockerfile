FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY target/chat-service-1.0.jar Chatservice.jar
ENTRYPOINT ["java", "-jar", "Chatservice.jar"]