#!/bin/bash

echo "🔧 Configuración de Secrets para GitHub Actions"
echo "=============================================="

# Verificar si gh CLI está instalado
if ! command -v gh &> /dev/null; then
    echo "❌ GitHub CLI no está instalado."
    echo "📥 Instala GitHub CLI desde: https://cli.github.com/"
    exit 1
fi

# Verificar si está autenticado
if ! gh auth status &> /dev/null; then
    echo "❌ No estás autenticado con GitHub CLI."
    echo "🔐 Ejecuta: gh auth login"
    exit 1
fi

echo "✅ GitHub CLI configurado correctamente"
echo ""

# Obtener el nombre del repositorio
REPO=$(gh repo view --json nameWithOwner -q .nameWithOwner)
echo "📦 Repositorio: $REPO"
echo ""

echo "🔑 Configurando secrets..."
echo ""

# Secret para la clave de servicio de GCP
echo "1️⃣ Configurando GCP_SA_KEY..."
echo "   📝 Necesitas crear una clave de servicio en Google Cloud:"
echo "   🔗 https://console.cloud.google.com/iam-admin/serviceaccounts"
echo "   📋 Selecciona la cuenta de servicio y crea una nueva clave JSON"
echo "   🔐 La cuenta de servicio debe tener estos roles:"
echo "      - Cloud Run Admin"
echo "      - Storage Admin"
echo "      - Service Account User"
echo "      - Cloud Build Service Account"
echo ""

read -p "   📄 Ruta al archivo JSON de la clave de servicio: " GCP_KEY_FILE

if [ -f "$GCP_KEY_FILE" ]; then
    gh secret set GCP_SA_KEY --repo "$REPO" < "$GCP_KEY_FILE"
    echo "   ✅ GCP_SA_KEY configurado correctamente"
else
    echo "   ❌ Archivo no encontrado: $GCP_KEY_FILE"
    echo "   🔄 Puedes configurarlo manualmente más tarde"
fi

echo ""

# Secret para la contraseña de la base de datos
echo "2️⃣ Configurando DB_PASSWORD..."
echo "   📝 Contraseña de la base de datos mybank_db"

read -s -p "   🔒 Contraseña de la base de datos: " DB_PASSWORD
echo ""

if [ -n "$DB_PASSWORD" ]; then
    echo "$DB_PASSWORD" | gh secret set DB_PASSWORD --repo "$REPO"
    echo "   ✅ DB_PASSWORD configurado correctamente"
else
    echo "   ❌ Contraseña no proporcionada"
    echo "   🔄 Puedes configurarlo manualmente más tarde"
fi

echo ""
echo "🎉 Configuración completada!"
echo ""
echo "📋 Secrets configurados:"
echo "   ✅ GCP_SA_KEY: Clave de servicio de Google Cloud"
echo "   ✅ DB_PASSWORD: Contraseña de la base de datos"
echo ""
echo "🔗 Para verificar los secrets:"
echo "   gh secret list --repo $REPO"
echo ""
echo "🚀 El CI/CD se ejecutará automáticamente en el próximo push a main" 