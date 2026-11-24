FROM maven:3.9.14-eclipse-temurin-21 AS compilacion
WORKDIR /proyecto
COPY .mvn/settings.xml /proyecto/settings.xml
COPY pom.xml ./
COPY src/ src/
RUN mvn -B -s /proyecto/settings.xml -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=compilacion /proyecto/target/pedido-service-0.1.0.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
