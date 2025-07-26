# CI/CD Pipeline - MyBank API

## 🚀 Flujo de CI/CD

### Trigger
- **Push a `main`**: Ejecuta build, test y deploy automático
- **Pull Request a `main`**: Ejecuta solo build y test

### Jobs

#### 1. Build and Test
- ✅ Checkout del código
- ✅ Setup Java 21
- ✅ Cache de dependencias Gradle
- ✅ Ejecutar tests unitarios
- ✅ Build de la aplicación

#### 2. Deploy (solo en main)
- ✅ Build de la aplicación
- ✅ Setup Google Cloud CLI
- ✅ Build y push de imagen Docker
- ✅ Deploy a Cloud Run
- ✅ Notificación de éxito

## 🔧 Configuración Requerida

### Secrets de GitHub
- `GCP_SA_KEY`: Clave de cuenta de servicio de Google Cloud
- `DB_PASSWORD`: Contraseña de la base de datos

### Variables de Entorno
- `PROJECT_ID`: mybank-467102
- `REGION`: us-central1
- `SERVICE_NAME`: mybank-api

## 📋 Comandos Locales

```bash
# Ejecutar tests
./gradlew test

# Build de la aplicación
./gradlew build

# Deploy manual
./deploy.sh
```

## 🎯 Resultado

Después de un push exitoso a `main`:
- ✅ Tests pasan
- ✅ Aplicación se despliega automáticamente
- ✅ URL del servicio disponible en los logs 