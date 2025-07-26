# 🔧 Corrección del Problema de Cloud Run

## 📋 Problema Identificado
```
Revision 'mybank-api-00012-nwm' is not ready and cannot serve traffic. 
The user-provided container failed to start and listen on the port defined 
provided by the PORT=8080 environment variable within the allocated timeout.
```

## 🔍 Causa Raíz
1. **Configuración de puerto incorrecta**: La aplicación no estaba escuchando en el puerto correcto
2. **Context path problemático**: El `context-path` estaba configurado como `/api/v1` lo que causaba problemas con Cloud Run
3. **Variable de entorno incorrecta**: Se usaba `SERVER_PORT` en lugar de `PORT`

## ✅ Correcciones Aplicadas

### 1. Configuración de Aplicación (`application-prod.yml`)
```yaml
server:
  port: ${PORT:8080}  # ✅ Usar variable PORT de Cloud Run
  servlet:
    context-path: /   # ✅ Cambiar de /api/v1 a /
```

### 2. Dockerfile (`deployment/Dockerfile`)
```dockerfile
# Configurar variables de entorno para producción
ENV SPRING_PROFILES_ACTIVE=prod
ENV PORT=8080  # ✅ Usar PORT en lugar de SERVER_PORT
```

### 3. Scripts de Corrección Creados
- **`deployment/fix-cloudrun-port.sh`**: Script para corregir automáticamente el problema
- **`deployment/monitor-service.sh`**: Script para monitorear el estado del servicio

## 🚀 Comandos de Corrección

### Corrección Automática
```bash
./deployment/fix-cloudrun-port.sh
```

### Monitoreo del Servicio
```bash
./deployment/monitor-service.sh
```

### Verificación Manual
```bash
# Verificar estado del servicio
gcloud run services describe mybank-api --region=us-central1 --project=mybank-467102

# Ver logs del servicio
gcloud logging read "resource.type=cloud_run_revision AND resource.labels.service_name=mybank-api" --limit=20 --project=mybank-467102
```

## 📊 Estado Esperado Después de la Corrección

### Endpoints Funcionando
- ✅ `/health` - Health check básico
- ✅ `/api/v1/health` - Health check de la API
- ✅ `/swagger-ui.html` - Documentación Swagger
- ✅ `/api/v1/auth/*` - Endpoints de autenticación
- ✅ `/api/v1/accounts/*` - Endpoints de cuentas
- ✅ `/api/v1/transactions/*` - Endpoints de transacciones

### Variables de Entorno Correctas
- `PORT=8080` (proporcionada por Cloud Run)
- `SPRING_PROFILES_ACTIVE=prod`
- `JWT_SECRET=*` (generado automáticamente)
- `DB_*` (configuración de base de datos)

## 🔄 Despliegue Manual
Los cambios se han committeado y pusheado a `main`. Para desplegar manualmente, usar los scripts de despliegue.

## 📝 Notas Importantes
1. **Context Path**: Cambiado de `/api/v1` a `/` para compatibilidad con Cloud Run
2. **Puerto**: La aplicación ahora usa la variable `PORT` proporcionada por Cloud Run
3. **Health Checks**: Los endpoints de health check están disponibles en `/health` y `/api/v1/health`
4. **Logs**: Los logs detallados están disponibles en Google Cloud Console

## 🆘 Troubleshooting
Si el problema persiste:
1. Verificar logs: `gcloud logging read "resource.type=cloud_run_revision AND resource.labels.service_name=mybank-api"`
2. Ejecutar script de corrección: `./deployment/fix-cloudrun-port.sh`
3. Monitorear estado: `./deployment/monitor-service.sh`
4. Verificar configuración de despliegue manual

---
**Fecha**: Julio 2024  
**Estado**: Corregido  
**Versión**: 0.1.0 