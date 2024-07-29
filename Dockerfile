# Usa la imagen oficial de Maven con OpenJDK 11
FROM maven:3.8.6-openjdk-11 AS build

# Establece el directorio de trabajo en el contenedor
WORKDIR /app

# Copia el archivo pom.xml y descarga las dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia el resto del código fuente de la aplicación
COPY src ./src

# Empaqueta la aplicación
RUN mvn clean package -DskipTests

# Usa la imagen oficial de OpenJDK para ejecutar la aplicación
FROM openjdk:11-jre-slim

# Establece el directorio de trabajo en el contenedor
WORKDIR /app

# Copia el archivo jar empaquetado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto en el que la aplicación se ejecuta
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
