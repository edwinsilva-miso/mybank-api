#!/bin/bash

# Configuración del proyecto
PROJECT_ID="mybank-467102"
REGION="us-central1"
SERVICE_NAME="mybank-api"

echo "🚀 Iniciando despliegue de MyBank API en GCP..."
echo "📋 Proyecto: $PROJECT_ID"
echo "🌍 Región: $REGION"

# Verificar si gcloud está instalado
if ! command -v gcloud &> /dev/null; then
    echo "❌ Google Cloud SDK no está instalado. Por favor instálalo primero."
    echo "📖 Instrucciones: https://cloud.google.com/sdk/docs/install"
    exit 1
fi

# Configurar el proyecto
echo "🔧 Configurando proyecto GCP..."
gcloud config set project $PROJECT_ID

# Habilitar APIs necesarias
echo "🔌 Habilitando APIs de GCP..."
gcloud services enable cloudbuild.googleapis.com
gcloud services enable run.googleapis.com
gcloud services enable sqladmin.googleapis.com
gcloud services enable containerregistry.googleapis.com

# Crear instancia de Cloud SQL (PostgreSQL)
echo "🗄️  Creando instancia de Cloud SQL..."
gcloud sql instances create mybank-postgres \
    --database-version=POSTGRES_15 \
    --tier=db-f1-micro \
    --region=$REGION \
    --storage-type=SSD \
    --storage-size=10GB \
    --backup-start-time=02:00 \
    --maintenance-window-day=SUN \
    --maintenance-window-hour=3 \
    --availability-type=zonal \
    --root-password=MyBank2024! \
    --quiet

# Crear base de datos
echo "📊 Creando base de datos..."
gcloud sql databases create mybank_prod --instance=mybank-postgres

# Crear usuario para la aplicación
echo "👤 Creando usuario de aplicación..."
gcloud sql users create mybank_app \
    --instance=mybank-postgres \
    --password=MyBankApp2024! \
    --host=%

# Obtener la conexión de la instancia
INSTANCE_CONNECTION_NAME=$(gcloud sql instances describe mybank-postgres --format="value(connectionName)")

echo "🔗 Nombre de conexión de la instancia: $INSTANCE_CONNECTION_NAME"

# Configurar variables de entorno para Cloud Run
echo "⚙️  Configurando variables de entorno..."

# Generar JWT secret
JWT_SECRET=$(openssl rand -base64 64)

# Configurar variables de entorno en Cloud Run
gcloud run services update $SERVICE_NAME \
    --region=$REGION \
    --set-env-vars="DB_NAME=mybank_prod" \
    --set-env-vars="DB_USER=mybank_app" \
    --set-env-vars="DB_PASSWORD=MyBankApp2024!" \
    --set-env-vars="DB_INSTANCE_CONNECTION_NAME=$INSTANCE_CONNECTION_NAME" \
    --set-env-vars="JWT_SECRET=$JWT_SECRET" \
    --set-env-vars="SPRING_PROFILES_ACTIVE=prod" \
    --quiet

# Construir y desplegar la aplicación
echo "🔨 Construyendo y desplegando la aplicación..."
gcloud builds submit --config deployment/cloudbuild.yaml .

# Obtener la URL del servicio
SERVICE_URL=$(gcloud run services describe $SERVICE_NAME --region=$REGION --format="value(status.url)")

echo "🎉 ¡Despliegue completado!"
echo "🌐 URL del servicio: $SERVICE_URL"
echo "📚 Swagger UI: $SERVICE_URL/swagger-ui.html"
echo "🏥 Health Check: $SERVICE_URL/health"
echo "📊 API Docs: $SERVICE_URL/api-docs"

echo ""
echo "🔐 Credenciales de base de datos:"
echo "   Host: $INSTANCE_CONNECTION_NAME"
echo "   Database: mybank_prod"
echo "   User: mybank_app"
echo "   Password: MyBankApp2024!"

echo ""
echo "⚠️  IMPORTANTE: Cambia las contraseñas en producción!" 