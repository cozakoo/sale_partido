# Este dockerfile esta pensado para desarrollo
FROM eclipse-temurin:21-jdk

# Del directorio, solo nos interesan las dependencias
# Y el codigo fuente ingresa al contenedor mediante el bind-mount de docker
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Solo se toman las dependencias
# No se compila ningun .jar
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline