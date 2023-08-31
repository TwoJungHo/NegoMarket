FROM openjdk:11-jre-slim
VOLUME /app/data
COPY target/user-service-1.0.jar /app/Userservice.jar
ENTRYPOINT ["java", "-jar", "/app/Userservice.jar"]
