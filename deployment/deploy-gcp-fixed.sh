#!/bin/bash

# Configuración del proyecto
PROJECT_ID="mybank-467102"
REGION="us-central1"
SERVICE_NAME="mybank-api"

echo "🚀 Iniciando despliegue de MyBank API en GCP..."
echo "📋 Proyecto: $PROJECT_ID"
echo "🌍 Región: $REGION"

# Función para manejar errores
handle_error() {
    echo "❌ Error en el paso: $1"
    echo "🔍 Detalles del error: $2"
    exit 1
}

# Verificar si gcloud está instalado
if ! command -v gcloud &> /dev/null; then
    handle_error "Verificación de gcloud" "Google Cloud SDK no está instalado"
fi

# Configurar el proyecto
echo "🔧 Configurando proyecto GCP..."
gcloud config set project $PROJECT_ID || handle_error "Configuración de proyecto" "No se pudo configurar el proyecto"

# Habilitar APIs necesarias
echo "🔌 Habilitando APIs de GCP..."
gcloud services enable cloudbuild.googleapis.com || handle_error "Habilitar Cloud Build API" "Error al habilitar Cloud Build API"
gcloud services enable run.googleapis.com || handle_error "Habilitar Cloud Run API" "Error al habilitar Cloud Run API"
gcloud services enable sqladmin.googleapis.com || handle_error "Habilitar Cloud SQL API" "Error al habilitar Cloud SQL API"
gcloud services enable containerregistry.googleapis.com || handle_error "Habilitar Container Registry API" "Error al habilitar Container Registry API"

# Verificar si la instancia de Cloud SQL ya existe
echo "🔍 Verificando si la instancia de Cloud SQL existe..."
if gcloud sql instances describe mybank-postgres --quiet 2>/dev/null; then
    echo "✅ La instancia de Cloud SQL ya existe"
else
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
        --quiet || handle_error "Crear instancia Cloud SQL" "Error al crear la instancia de Cloud SQL"
fi

# Esperar a que la instancia esté lista
echo "⏳ Esperando a que la instancia esté lista..."
sleep 30

# Verificar si la base de datos ya existe
echo "🔍 Verificando si la base de datos existe..."
if gcloud sql databases describe mybank_prod --instance=mybank-postgres --quiet 2>/dev/null; then
    echo "✅ La base de datos ya existe"
else
    echo "📊 Creando base de datos..."
    gcloud sql databases create mybank_prod --instance=mybank-postgres || handle_error "Crear base de datos" "Error al crear la base de datos"
fi

# Verificar si el usuario ya existe
echo "🔍 Verificando si el usuario de aplicación existe..."
if gcloud sql users describe mybank_app --instance=mybank-postgres --quiet 2>/dev/null; then
    echo "✅ El usuario de aplicación ya existe"
else
    echo "👤 Creando usuario de aplicación..."
    gcloud sql users create mybank_app \
        --instance=mybank-postgres \
        --password=MyBankApp2024! \
        --host=% || handle_error "Crear usuario" "Error al crear el usuario de aplicación"
fi

# Obtener la conexión de la instancia
echo "🔗 Obteniendo nombre de conexión de la instancia..."
INSTANCE_CONNECTION_NAME=$(gcloud sql instances describe mybank-postgres --format="value(connectionName)") || handle_error "Obtener conexión" "Error al obtener el nombre de conexión"

echo "🔗 Nombre de conexión de la instancia: $INSTANCE_CONNECTION_NAME"

# Generar JWT secret
echo "🔐 Generando JWT secret..."
JWT_SECRET=$(openssl rand -base64 64) || handle_error "Generar JWT secret" "Error al generar el JWT secret"

# Verificar si el servicio de Cloud Run ya existe
echo "🔍 Verificando si el servicio de Cloud Run existe..."
if gcloud run services describe $SERVICE_NAME --region=$REGION --quiet 2>/dev/null; then
    echo "✅ El servicio de Cloud Run ya existe, actualizando variables de entorno..."
    gcloud run services update $SERVICE_NAME \
        --region=$REGION \
        --set-env-vars="DB_NAME=mybank_prod" \
        --set-env-vars="DB_USER=mybank_app" \
        --set-env-vars="DB_PASSWORD=MyBankApp2024!" \
        --set-env-vars="DB_INSTANCE_CONNECTION_NAME=$INSTANCE_CONNECTION_NAME" \
        --set-env-vars="JWT_SECRET=$JWT_SECRET" \
        --set-env-vars="SPRING_PROFILES_ACTIVE=prod" \
        --quiet || handle_error "Actualizar servicio" "Error al actualizar el servicio de Cloud Run"
else
    echo "⚠️  El servicio de Cloud Run no existe. Se creará durante el build."
fi

# Construir y desplegar la aplicación
echo "🔨 Construyendo y desplegando la aplicación..."
gcloud builds submit --config deployment/cloudbuild.yaml . || handle_error "Build y despliegue" "Error en el proceso de build y despliegue"

# Esperar un momento para que el servicio esté disponible
echo "⏳ Esperando a que el servicio esté disponible..."
sleep 30

# Obtener la URL del servicio
echo "🔍 Obteniendo URL del servicio..."
SERVICE_URL=$(gcloud run services describe $SERVICE_NAME --region=$REGION --format="value(status.url)") || handle_error "Obtener URL" "Error al obtener la URL del servicio"

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