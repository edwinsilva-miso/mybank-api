# MyBank API - Deployment

Este directorio contiene todos los archivos necesarios para desplegar la aplicación MyBank API en Google Cloud Platform.

## Archivos Incluidos

### Configuración de Cloud Build
- **`cloudbuild.yaml`**: Configuración de Google Cloud Build para automatizar el proceso de build y despliegue
- **`Dockerfile`**: Configuración de Docker para containerizar la aplicación
- **`.dockerignore`**: Archivos y directorios a excluir del contexto de Docker
- **`.gcloudignore`**: Archivos y directorios a excluir del contexto de Cloud Build

### Variables de Entorno
- **`env-vars.txt`**: Variables de entorno en formato simple (key=value)
- **`env-vars.yaml`**: Variables de entorno en formato YAML para Cloud Run

### Scripts de Despliegue
- **`deploy-gcp.sh`**: Script original de despliegue
- **`deploy-gcp-fixed.sh`**: Script corregido de despliegue con mejoras

### Testing y Validación
- **`MyBank-API.postman_collection.json`**: Colección de Postman con todos los endpoints
- **`MyBank-API.postman_environment.json`**: Variables de entorno para desarrollo local
- **`MyBank-API-Production.postman_environment.json`**: Variables de entorno para producción
- **`POSTMAN_ENVIRONMENTS.md`**: Documentación de environments de Postman

## Uso

### Despliegue Manual
```bash
# Desde el directorio raíz del proyecto
./deployment/deploy-gcp-fixed.sh
```

### Despliegue con Cloud Build
```bash
# Desde el directorio raíz del proyecto
gcloud builds submit --config deployment/cloudbuild.yaml .
```

### Actualizar Variables de Entorno
```bash
# Actualizar variables en Cloud Run
gcloud run services update mybank-api --env-vars-file=deployment/env-vars.yaml --region=us-central1
```

### Testing con Postman
```bash
# Importar la colección y environments de Postman
# 1. Abre Postman
# 2. Importa: deployment/MyBank-API.postman_collection.json
# 3. Importa: deployment/MyBank-API.postman_environment.json (desarrollo local)
# 4. Importa: deployment/MyBank-API-Production.postman_environment.json (producción)
# 5. Selecciona el environment apropiado según el ambiente a probar
```

Para más detalles sobre los environments, consulta [POSTMAN_ENVIRONMENTS.md](POSTMAN_ENVIRONMENTS.md).

## Configuración Actual

### Proyecto GCP
- **Proyecto**: `mybank-467102`
- **Región**: `us-central1`
- **Servicio**: `mybank-api`

### Base de Datos
- **Instancia**: `mybank-postgres`
- **Base de datos**: `mybank_db`
- **Usuario**: `mybank_app`

### URL del Servicio
- **Producción**: https://mybank-api-7mxungdvxq-uc.a.run.app

## Notas Importantes

1. **JWT_SECRET**: Debe estar codificado en Base64 para evitar errores de decodificación
2. **Variables de Entorno**: Usar `env-vars.yaml` para Cloud Run, no `env-vars.txt`
3. **Permisos**: Asegurarse de tener los permisos necesarios en GCP antes del despliegue
4. **Base de Datos**: La instancia de Cloud SQL debe estar configurada y accesible

## Troubleshooting

### Error de JWT_SECRET
Si aparece el error "Illegal base64 character", regenerar el JWT_SECRET:
```bash
echo "your-secret-key" | base64
```

### Error de Conexión a Base de Datos
Verificar que la instancia de Cloud SQL esté configurada correctamente y que el usuario tenga los permisos necesarios.

### Error de Build
Verificar que el archivo `.gcloudignore` no esté excluyendo archivos necesarios para el build. 