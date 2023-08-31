FROM openjdk:11-jre-slim
VOLUME /app/data
COPY target/apigateway-service-1.0.jar /app/Apigatewayservice.jar
ENTRYPOINT ["java", "-jar", "/app/Apigatewayservice.jar"]
