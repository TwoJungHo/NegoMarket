FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY target/imgfile-service-1.0.jar Imgfileservice.jar
ENTRYPOINT ["java", "-jar", "Imgfileservice.jar"]