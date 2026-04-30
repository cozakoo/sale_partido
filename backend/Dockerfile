FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY . .

RUN ./mvnw clean package -DskipTests && \
    mv target/*.jar target/app.jar

CMD ["java", "-jar", "target/app.jar"]