# Postman Environments - MyBank API

Este directorio contiene los environments de Postman para testing de la API MyBank.

## 📁 Environments Disponibles

### 1. Local Environment
- **Archivo**: `MyBank-API.postman_environment.json`
- **URL Base**: `http://localhost:8080`
- **Uso**: Desarrollo local con Docker Compose

### 2. Production Environment
- **Archivo**: `MyBank-API-Production.postman_environment.json`
- **URL Base**: `https://mybank-api-282065076144.us-central1.run.app`
- **Uso**: Testing en ambiente de producción

## 🚀 Configuración

### Importar Environments
1. Abre Postman
2. Ve a **Environments** en el panel izquierdo
3. Haz clic en **Import**
4. Selecciona el archivo del environment que desees usar

### Variables Disponibles

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `baseUrl` | URL base de la API | `https://mybank-api-282065076144.us-central1.run.app` |
| `authToken` | Token JWT de autenticación | (se llena automáticamente) |
| `userId` | ID del usuario de prueba | `1` |
| `accountId` | ID de la cuenta de prueba | `1` |
| `transactionId` | ID de la transacción de prueba | `1` |
| `username` | Usuario de prueba | `testuser` |
| `password` | Contraseña de prueba | `password123` |
| `environment` | Ambiente actual | `production` |
| `apiVersion` | Versión de la API | `v1` |
| `fullApiUrl` | URL completa de la API | `https://mybank-api-282065076144.us-central1.run.app/api/v1` |

## 🔄 Flujo de Testing

### 1. Configurar Environment
- Selecciona el environment apropiado (Local o Production)
- Verifica que la URL base sea correcta

### 2. Autenticación
1. Ejecuta **User Registration** para crear un usuario
2. Ejecuta **User Login** para obtener el token JWT
3. El token se guardará automáticamente en `authToken`

### 3. Testing de Endpoints
- Usa la colección `MyBank-API.postman_collection.json`
- Todos los endpoints están configurados para usar las variables del environment

## 🔗 URLs Importantes

### Production
- **API Base**: https://mybank-api-282065076144.us-central1.run.app
- **Swagger UI**: https://mybank-api-282065076144.us-central1.run.app/swagger-ui.html
- **Health Check**: https://mybank-api-282065076144.us-central1.run.app/health
- **API Docs**: https://mybank-api-282065076144.us-central1.run.app/api-docs

### Local
- **API Base**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/health

## ⚠️ Notas Importantes

1. **Seguridad**: Las contraseñas en los environments son solo para testing
2. **Tokens**: Los tokens JWT se generan automáticamente al hacer login
3. **Datos**: Los IDs (userId, accountId, etc.) son ejemplos y pueden cambiar
4. **Ambiente**: Asegúrate de usar el environment correcto para cada ambiente

## 🛠️ Troubleshooting

### Error de Conexión
- Verifica que la URL base sea correcta
- Confirma que el servicio esté ejecutándose
- Revisa los logs del servicio

### Error de Autenticación
- Ejecuta el login nuevamente
- Verifica que el token se haya guardado correctamente
- Revisa que el usuario exista en la base de datos

### Error de Variables
- Verifica que el environment esté seleccionado
- Confirma que las variables estén habilitadas
- Revisa la sintaxis de las variables en las requests 