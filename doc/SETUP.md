# Setup del Proyecto MyBank API

## 🚀 Inicio Rápido

### Prerrequisitos
- **Java 23**
- **Docker y Docker Compose**
- **Gradle** (opcional, se usa wrapper)

### Instalación y Ejecución

```bash
# Clonar el repositorio
git clone https://github.com/edwinsilva-miso/mybank-api.git
cd mybank-api

# Iniciar servicios dependientes
docker-compose up -d

# Ejecutar migraciones de base de datos
./gradlew flywayMigrate

# Compilar y ejecutar la aplicación
./gradlew build
./gradlew bootRun
```

## 🌐 Acceso a la API
- **Base URL**: http://localhost:8080/api/v1
- **Swagger UI**: http://localhost:8080/api/v1/swagger-ui.html
- **Health Check**: http://localhost:8080/api/v1/health

## 🧪 Validación y Testing

- **Pruebas unitarias:**
  ```bash
  ./gradlew test
  ```
- **Validación funcional completa:**
  ```bash
  ./final-fixed-test.sh
  ./verification-after-adjustments.sh
  ```

## 🔑 Variables de Entorno

Copia y ajusta el archivo ejemplo:
```bash
cp env.example .env
```

## 🛠️ Endpoints Principales

- `POST /api/v1/auth/register` — Registro de usuario
- `POST /api/v1/auth/login` — Login (JWT)
- `POST /api/v1/accounts` — Crear cuenta
- `POST /api/v1/transactions` — Crear transacción
- `POST /api/v1/transactions/{transactionId}/process` — Procesar transacción
- `GET /api/v1/transactions/user/{userId}` — Transacciones de usuario
- `GET /api/v1/transactions/audit/user/{userId}` — Auditoría de usuario
- `GET /api/v1/health` — Health check

## 📝 Ejemplo de uso de scripts de validación

```bash
# Validar todos los flujos principales
yes | ./final-fixed-test.sh

# Verificación rápida post-ajustes
./verification-after-adjustments.sh
```

## 🧩 Documentación
- [README.md](README.md)
- [ARCHITECTURE.md](ARCHITECTURE.md)
- [TRANSACTION_DOMAIN.md](TRANSACTION_DOMAIN.md)
- [TRANSACTION_AUDIT.md](TRANSACTION_AUDIT.md)
- [TESTING.md](TESTING.md)

---

**Última actualización:** Julio 2025 