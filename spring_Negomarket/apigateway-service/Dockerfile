FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY target/apigateway-service-1.0.jar Apigatewayservice.jar
ENTRYPOINT ["jacdva", "-jar", "Apigatewayservice.jar"]