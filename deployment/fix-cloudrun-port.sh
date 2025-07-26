#!/bin/bash

echo "🔧 Corrigiendo problema de puerto en Cloud Run"
echo "=============================================="

# Variables
PROJECT_ID="mybank-467102"
REGION="us-central1"
SERVICE_NAME="mybank-api"
IMAGE_NAME="gcr.io/$PROJECT_ID/mybank-api"

echo "📋 Configuración:"
echo "   Proyecto: $PROJECT_ID"
echo "   Región: $REGION"
echo "   Servicio: $SERVICE_NAME"
echo ""

# Verificar estado actual
echo "🔍 Verificando estado actual del servicio..."
CURRENT_STATUS=$(gcloud run services describe $SERVICE_NAME --region=$REGION --format="value(status.conditions[0].status)" --project=$PROJECT_ID 2>/dev/null)

if [ "$CURRENT_STATUS" = "False" ]; then
    echo "❌ Servicio con problemas detectado"
    echo "🔄 Iniciando corrección..."
    
    # Construir nueva imagen
    echo "🏗️ Construyendo nueva imagen Docker..."
    docker build -t $IMAGE_NAME:latest -f deployment/Dockerfile .
    
    # Push a GCR
    echo "📤 Subiendo imagen a GCR..."
    docker push $IMAGE_NAME:latest
    
    # Actualizar servicio
    echo "🚀 Actualizando servicio en Cloud Run..."
    gcloud run deploy $SERVICE_NAME \
        --image $IMAGE_NAME:latest \
        --region $REGION \
        --platform managed \
        --allow-unauthenticated \
        --port 8080 \
        --memory 1Gi \
        --cpu 1 \
        --max-instances 10 \
        --set-env-vars="SPRING_PROFILES_ACTIVE=prod" \
        --set-env-vars="JWT_SECRET=$(openssl rand -base64 64)" \
        --set-env-vars="DB_NAME=mybank_db" \
        --set-env-vars="DB_USER=mybank_app" \
        --set-env-vars="DB_PASSWORD=MyBank2024!" \
        --set-env-vars="DB_INSTANCE_CONNECTION_NAME=$PROJECT_ID:$REGION:mybank-postgres" \
        --add-cloudsql-instances="$PROJECT_ID:$REGION:mybank-postgres" \
        --project=$PROJECT_ID
    
    echo "✅ Corrección completada"
else
    echo "✅ Servicio funcionando correctamente"
fi

# Obtener URL del servicio
SERVICE_URL=$(gcloud run services describe $SERVICE_NAME --region=$REGION --format="value(status.url)" --project=$PROJECT_ID)
echo ""
echo "🌐 URL del servicio: $SERVICE_URL"
echo "🏥 Health check: $SERVICE_URL/health"
echo "📚 Swagger UI: $SERVICE_URL/swagger-ui.html" 