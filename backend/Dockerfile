# Este dockerfile esta pensado para desarrollo
FROM eclipse-temurin:21-jdk

# Del directorio, solo nos interesan las dependencias
# Y el codigo fuente ingresa al contenedor mediante el bind-mount de docker
WORKDIR /app
COPY . .
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Ejecutar para desarrollo
# No se compila ningun .jar
RUN ./mvnw dependency:go-offline
CMD ["./mvnw", "spring-boot:run"]