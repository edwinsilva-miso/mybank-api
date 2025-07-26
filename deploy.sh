#!/bin/bash

# Script de conveniencia para desplegar MyBank API
# Este script ejecuta el script de despliegue desde el directorio deployment

echo "🚀 MyBank API - Script de Despliegue"
echo "======================================"

# Verificar que estamos en el directorio correcto
if [ ! -f "build.gradle" ]; then
    echo "❌ Error: Este script debe ejecutarse desde el directorio raíz del proyecto"
    echo "   Directorio actual: $(pwd)"
    echo "   Buscando archivo build.gradle..."
    exit 1
fi

# Verificar que el directorio deployment existe
if [ ! -d "deployment" ]; then
    echo "❌ Error: El directorio 'deployment' no existe"
    echo "   Asegúrate de que todos los archivos de despliegue estén organizados"
    exit 1
fi

# Verificar que el script de despliegue existe
if [ ! -f "deployment/deploy-gcp-fixed.sh" ]; then
    echo "❌ Error: El script de despliegue no existe"
    echo "   Buscando: deployment/deploy-gcp-fixed.sh"
    exit 1
fi

echo "✅ Verificaciones completadas"
echo "📁 Ejecutando script de despliegue desde: deployment/deploy-gcp-fixed.sh"
echo ""

# Ejecutar el script de despliegue
chmod +x deployment/deploy-gcp-fixed.sh
./deployment/deploy-gcp-fixed.sh 