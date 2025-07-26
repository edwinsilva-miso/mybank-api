#!/bin/bash

echo "🔧 Configuración de Permisos para GCP"
echo "====================================="

# Configuración del proyecto
PROJECT_ID="mybank-467102"
SERVICE_ACCOUNT_EMAIL="mybank-cicd@mybank-467102.iam.gserviceaccount.com"

echo "📋 Proyecto: $PROJECT_ID"
echo "👤 Cuenta de servicio: $SERVICE_ACCOUNT_EMAIL"
echo ""

# Verificar si gcloud está instalado
if ! command -v gcloud &> /dev/null; then
    echo "❌ Error: Google Cloud SDK no está instalado"
    echo "📥 Instala desde: https://cloud.google.com/sdk/docs/install"
    exit 1
fi

# Configurar el proyecto
echo "🔧 Configurando proyecto..."
gcloud config set project $PROJECT_ID

# Crear cuenta de servicio si no existe
echo "👤 Verificando cuenta de servicio..."
if ! gcloud iam service-accounts describe $SERVICE_ACCOUNT_EMAIL --quiet 2>/dev/null; then
    echo "📝 Creando cuenta de servicio..."
    gcloud iam service-accounts create mybank-cicd \
        --display-name="MyBank CI/CD Service Account" \
        --description="Service account for MyBank API CI/CD pipeline"
else
    echo "✅ La cuenta de servicio ya existe"
fi

# Asignar roles necesarios
echo "🔐 Asignando roles necesarios..."

ROLES=(
    "roles/run.admin"
    "roles/storage.admin"
    "roles/iam.serviceAccountUser"
    "roles/cloudbuild.builds.builder"
    "roles/artifactregistry.writer"
)

for ROLE in "${ROLES[@]}"; do
    echo "   📋 Asignando $ROLE..."
    gcloud projects add-iam-policy-binding $PROJECT_ID \
        --member="serviceAccount:$SERVICE_ACCOUNT_EMAIL" \
        --role="$ROLE" \
        --quiet
done

# Crear clave de servicio
echo "🔑 Creando clave de servicio..."
gcloud iam service-accounts keys create gcp-sa-key.json \
    --iam-account=$SERVICE_ACCOUNT_EMAIL

echo ""
echo "🎉 Configuración completada!"
echo ""
echo "📋 Próximos pasos:"
echo "1. Copia el contenido del archivo gcp-sa-key.json"
echo "2. Ve a GitHub → Settings → Secrets and variables → Actions"
echo "3. Crea un nuevo secret llamado GCP_SA_KEY"
echo "4. Pega el contenido del archivo JSON"
echo ""
echo "🔐 Archivo de clave creado: gcp-sa-key.json"
echo "⚠️  IMPORTANTE: Mantén este archivo seguro y no lo subas al repositorio!"
echo ""
echo "🚀 Después de configurar el secret, el CI/CD funcionará correctamente" 