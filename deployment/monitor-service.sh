#!/bin/bash

echo "📊 Monitoreo del Servicio MyBank API"
echo "===================================="

# Variables
PROJECT_ID="mybank-467102"
REGION="us-central1"
SERVICE_NAME="mybank-api"

echo "🔍 Verificando estado del servicio..."

# Obtener información del servicio
SERVICE_URL=$(gcloud run services describe $SERVICE_NAME --region=$REGION --format="value(status.url)" --project=$PROJECT_ID 2>/dev/null)
SERVICE_STATUS=$(gcloud run services describe $SERVICE_NAME --region=$REGION --format="value(status.conditions[0].status)" --project=$PROJECT_ID 2>/dev/null)
SERVICE_MESSAGE=$(gcloud run services describe $SERVICE_NAME --region=$REGION --format="value(status.conditions[0].message)" --project=$PROJECT_ID 2>/dev/null)

echo "📋 Información del Servicio:"
echo "   URL: $SERVICE_URL"
echo "   Estado: $SERVICE_STATUS"
echo "   Mensaje: $SERVICE_MESSAGE"
echo ""

if [ "$SERVICE_STATUS" = "True" ]; then
    echo "✅ Servicio funcionando correctamente"
    echo ""
    echo "🧪 Probando endpoints..."
    
    # Probar health check
    echo "🏥 Health Check:"
    HEALTH_RESPONSE=$(curl -s -w "%{http_code}" "$SERVICE_URL/health" -o /tmp/health_response)
    if [ "$HEALTH_RESPONSE" = "200" ]; then
        echo "   ✅ /health - OK"
        cat /tmp/health_response | head -3
    else
        echo "   ❌ /health - Error ($HEALTH_RESPONSE)"
    fi
    
    # Probar API health
    echo "🏥 API Health:"
    API_HEALTH_RESPONSE=$(curl -s -w "%{http_code}" "$SERVICE_URL/api/v1/health" -o /tmp/api_health_response)
    if [ "$API_HEALTH_RESPONSE" = "200" ]; then
        echo "   ✅ /api/v1/health - OK"
        cat /tmp/api_health_response | head -3
    else
        echo "   ❌ /api/v1/health - Error ($API_HEALTH_RESPONSE)"
    fi
    
    # Probar Swagger
    echo "📚 Swagger UI:"
    SWAGGER_RESPONSE=$(curl -s -w "%{http_code}" "$SERVICE_URL/swagger-ui.html" -o /dev/null)
    if [ "$SWAGGER_RESPONSE" = "200" ]; then
        echo "   ✅ /swagger-ui.html - OK"
    else
        echo "   ❌ /swagger-ui.html - Error ($SWAGGER_RESPONSE)"
    fi
    
    echo ""
    echo "🌐 Enlaces útiles:"
    echo "   - Servicio: $SERVICE_URL"
    echo "   - Health: $SERVICE_URL/health"
    echo "   - API Health: $SERVICE_URL/api/v1/health"
    echo "   - Swagger: $SERVICE_URL/swagger-ui.html"
    
else
    echo "❌ Servicio con problemas"
    echo "🔧 Ejecuta: ./deployment/fix-cloudrun-port.sh"
fi

# Limpiar archivos temporales
rm -f /tmp/health_response /tmp/api_health_response 2>/dev/null 