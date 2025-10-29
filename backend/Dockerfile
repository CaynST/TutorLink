# --- Etapa 1: Compilación (Build) ---
# Usamos una imagen de Maven con Java 17 para compilar el .jar
# (Si usas Java 21, cambia 17 por 21)
FROM maven:3.8.5-openjdk-17 AS build

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos el archivo de configuración del proyecto
COPY pom.xml .

# Copiamos todo el código fuente
COPY src ./src

# Ejecutamos el build (skip tests para un despliegue más rápido)
RUN mvn package -DskipTests

# --- Etapa 2: Ejecución (Run) ---
# Usamos una imagen ligera de Java 17 para correr la app
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiamos solo el .jar compilado desde la etapa de 'build'
# (Asegúrate que el nombre de tu .jar en target/ coincida)
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto 8080 (el que usa Spring Boot)
EXPOSE 8080

# Comando para iniciar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
