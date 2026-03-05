FROM maven:3.9.9-eclipse-temurin-21
COPY build/libs/market-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]