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
├── final-fixed-test.sh         # Script de validación final
├── verification-after-adjustments.sh # Script de verificación post-ajustes
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