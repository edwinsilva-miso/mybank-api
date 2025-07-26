# Usar la imagen oficial de OpenJDK 21
FROM openjdk:21-jdk-slim

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo JAR de la aplicación
COPY app.jar app.jar

# Exponer el puerto
EXPOSE 8080

# Configurar variables de entorno para producción
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"] 