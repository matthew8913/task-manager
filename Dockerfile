FROM openjdk:21-jdk-slim

COPY target/task-manager.jar /app/task-manager.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/task-manager.jar"]