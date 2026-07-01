# ETAPA 1: Construccion (Build)
# Usamos una imagen que ya tiene Maven y Java instalados para compilar el codigo.
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
# Copiamos los archivos de configuración y el código fuente
COPY pom.xml .
COPY src ./src
# Compilamos el proyecto generando el archivo .jar
RUN mvn clean package -DskipTests

# ETAPA 2: Ejecucion (Run)
# Usamos una imagen mucho mas ligera solo con el entorno de ejecucion de Java (JRE).
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copiamos el .jar generado en la ETAPA 1 hacia esta nueva imagen limpia
COPY --from=builder /app/target/*.jar app.jar
# Exponemos el puerto de Spring Boot
EXPOSE 8080
# Comando para arrancar la aplicacion
ENTRYPOINT ["java", "-jar", "app.jar"]