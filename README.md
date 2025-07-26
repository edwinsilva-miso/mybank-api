# MyBank API - Proyecto de Modernización

## 📋 Descripción del Proyecto

Este proyecto representa la **migración y modernización** del sistema bancario legacy [Legacy_MyBank](https://github.com/edwinsilva-miso/Legacy_MyBank) hacia una arquitectura moderna basada en dominios y APIs REST.

### 🎯 Estado Actual
- **API funcional y validada**
- **Pruebas unitarias y de integración exitosas**
- **Scripts de validación final incluidos**: `final-fixed-test.sh`, `verification-after-adjustments.sh`
- **Integración de userId completada**: `test-userid-integration.sh`
- **Estructura limpia y optimizada**

## 🏗️ Arquitectura del Sistema

- **Backend**: Spring Boot 3.4.0, Java 23
- **Base de Datos**: PostgreSQL 15 (Flyway para migraciones)
- **Seguridad**: JWT, roles, cifrado
- **Testing**: JUnit, Mockito, scripts bash de validación

## 📂 Estructura del Proyecto (actual)

```
mybank-api/
├── src/
│   ├── main/
│   │   ├── java/com/mybank/
│   │   │   ├── domains/         # Dominios: user, account, transaction
│   │   │   └── shared/         # Config, seguridad, utilidades
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/
│   └── test/                   # Pruebas unitarias e integración
├── deployment/                 # 🚀 Archivos de despliegue en GCP
│   ├── cloudbuild.yaml        # Configuración de Cloud Build
│   ├── Dockerfile             # Configuración de Docker
│   ├── env-vars.yaml          # Variables de entorno
│   ├── deploy-gcp-fixed.sh    # Script de despliegue
│   ├── MyBank-API.postman_collection.json # Colección de Postman
│   ├── MyBank-API.postman_environment.json # Variables para desarrollo local
│   ├── MyBank-API-Production.postman_environment.json # Variables para producción
│   ├── POSTMAN_ENVIRONMENTS.md # Documentación de environments
│   └── README.md              # Documentación de despliegue
├── final-fixed-test.sh         # Script de validación final
├── test-userid-integration.sh  # Script de testing específico
├── deploy.sh                   # Script de conveniencia para despliegue
├── setup-github-secrets.sh     # Configuración de secrets para CI/CD
├── .github/workflows/          # 🚀 Pipeline de CI/CD
│   └── ci-cd.yml              # Workflow principal
├── README.md
├── ARCHITECTURE.md
├── SETUP.md
├── TESTING.md
├── TRANSACTION_AUDIT.md
├── TRANSACTION_DOMAIN.md
├── build.gradle
├── docker-compose.yml
└── ...
```

## 🚀 Endpoints Principales

- **Autenticación**
  - `POST /api/v1/auth/register` - Registro
  - `POST /api/v1/auth/login` - Login (JWT) - Incluye userId en respuesta
- **Cuentas**
  - `POST /api/v1/accounts` - Crear cuenta
  - `GET /api/v1/accounts/{accountId}` - Obtener cuenta
  - `GET /api/v1/accounts/user/{userId}` - Cuentas de usuario
- **Transacciones**
  - `POST /api/v1/transactions` - Crear transacción (DEPOSIT, WITHDRAWAL, PAYMENT)
  - `POST /api/v1/transactions/{transactionId}/process` - Procesar transacción
  - `GET /api/v1/transactions/{transactionId}` - Obtener transacción
  - `GET /api/v1/transactions/user/{userId}` - Transacciones de usuario
- **Auditoría**
  - `GET /api/v1/transactions/audit/transaction/{transactionId}`
  - `GET /api/v1/transactions/audit/user/{userId}`
- **Health**
  - `GET /api/v1/health`

## 🧪 Validación y Testing

- Ejecuta los scripts:
  - `./final-fixed-test.sh` — Valida todos los flujos principales y casos de error
  - `./verification-after-adjustments.sh` — Verificación rápida post-ajustes
  - `./test-userid-integration.sh` — Valida integración de userId en login y operaciones
- Pruebas unitarias: `./gradlew test`
- Pruebas de integración: incluidas en los scripts y tests automáticos

## 🚀 Despliegue en Producción

### Despliegue en Google Cloud Platform

El proyecto incluye configuración completa para despliegue en GCP Cloud Run con Cloud SQL.

#### Archivos de Despliegue
Todos los archivos de despliegue están organizados en el directorio `deployment/`:
- **`deployment/cloudbuild.yaml`** — Configuración de Google Cloud Build
- **`deployment/Dockerfile`** — Configuración de Docker
- **`deployment/env-vars.yaml`** — Variables de entorno para producción
- **`deployment/deploy-gcp-fixed.sh`** — Script de despliegue automatizado
- **`deployment/MyBank-API.postman_collection.json`** — Colección de Postman para testing
- **`deployment/MyBank-API.postman_environment.json`** — Variables para desarrollo local
- **`deployment/MyBank-API-Production.postman_environment.json`** — Variables para producción

#### Despliegue Rápido
```bash
# Desde el directorio raíz del proyecto
./deploy.sh
```

#### Despliegue Manual
```bash
# Usar el script de despliegue directamente
./deployment/deploy-gcp-fixed.sh

# O usar Cloud Build manualmente
gcloud builds submit --config deployment/cloudbuild.yaml .
```

#### Configuración Actual
- **Proyecto GCP**: `mybank-467102`
- **Región**: `us-central1`
- **Servicio**: `mybank-api`
- **URL**: https://mybank-api-7mxungdvxq-uc.a.run.app

Para más detalles, consulta [deployment/README.md](deployment/README.md).

#### Testing con Postman
```bash
# Importar la colección y environments de Postman
# 1. Abre Postman
# 2. Importa: deployment/MyBank-API.postman_collection.json
# 3. Importa: deployment/MyBank-API.postman_environment.json (desarrollo local)
# 4. Importa: deployment/MyBank-API-Production.postman_environment.json (producción)
# 5. Selecciona el environment apropiado según el ambiente a probar
```

Para más detalles sobre los environments, consulta [deployment/POSTMAN_ENVIRONMENTS.md](deployment/POSTMAN_ENVIRONMENTS.md).

## 🔄 CI/CD Pipeline

### Despliegue Automático
El proyecto incluye un pipeline de CI/CD con GitHub Actions que se ejecuta automáticamente:

- **Push a `main`**: Build → Test → Deploy automático
- **Pull Request**: Build → Test (sin deploy)

### Configuración
```bash
# 1. Configurar permisos en GCP
./setup-gcp-permissions.sh

# 2. Configurar secrets de GitHub
./setup-github-secrets.sh
```

### Secrets Requeridos
- `GCP_SA_KEY`: Clave de servicio de Google Cloud
- `DB_PASSWORD`: Contraseña de la base de datos

Para más detalles, consulta [.github/README.md](.github/README.md).

## 📚 Documentación

- [SETUP.md](doc/SETUP.md) — Guía de configuración y ejecución
- [ARCHITECTURE.md](doc/ARCHITECTURE.md) — Arquitectura y dominios
- [TRANSACTION_DOMAIN.md](doc/TRANSACTION_DOMAIN.md) — Lógica de transacciones
- [TRANSACTION_AUDIT.md](doc/TRANSACTION_AUDIT.md) — Auditoría de transacciones
- [TESTING.md](doc/TESTING.md) — Estrategia y guía de testing

## 🔍 API Documentation

La documentación interactiva de la API está disponible en:
- **Swagger UI**: http://localhost:8080/api/v1/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/v1/api-docs

## 🛠️ Contribución

1. Fork del proyecto
2. Crea una rama para tu feature
3. Commit y push
4. Pull Request

## 🛡️ Licencia

GPL-3.0 — ver [LICENSE](LICENSE)

## 👤 Equipo
- **Desarrollador Principal**: Edwin Silva

---

**Última actualización:** Julio 2025 