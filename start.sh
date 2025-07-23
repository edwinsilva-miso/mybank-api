#!/bin/bash

echo "🚀 Iniciando MyBank API..."

# Verificar si Java 23 está configurado
if [ -z "$JAVA_HOME" ]; then
    echo "⚠️  JAVA_HOME no está configurado. Configurando Java 23..."
    export JAVA_HOME=/opt/homebrew/opt/openjdk@23/libexec/openjdk.jdk/Contents/Home
fi

echo "✅ Java version: $(java -version 2>&1 | head -n 1)"

# Verificar si Docker está corriendo
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker no está corriendo. Por favor inicia Docker Desktop."
    exit 1
fi

echo "✅ Docker está corriendo"

# Iniciar servicios dependientes
echo "🐳 Iniciando servicios dependientes..."
docker-compose up -d

# Esperar a que PostgreSQL esté listo
echo "⏳ Esperando a que PostgreSQL esté listo..."
sleep 10

# Ejecutar migraciones
echo "🔄 Ejecutando migraciones de base de datos..."
./gradlew flywayMigrate

# Compilar y ejecutar la aplicación
echo "🔨 Compilando y ejecutando la aplicación..."
./gradlew bootRun

echo "🎉 MyBank API está corriendo en http://localhost:8080/api/v1"
echo "📚 Swagger UI: http://localhost:8080/api/v1/swagger-ui.html"
echo "🏥 Health Check: http://localhost:8080/api/v1/health" 