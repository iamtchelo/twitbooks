FROM openjdk:10-jre-slim

EXPOSE 8080

COPY target/twitbooks-0.0.1-SNAPSHOT.jar ~/app/app.jar

WORKDIR ~/app

CMD ["java", "-jar", "app.jar"]
