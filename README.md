# MyBank API

## Descripción
API REST para un sistema bancario moderno desarrollado con Spring Boot 3, siguiendo arquitectura hexagonal y principios DDD.

## Características
- **Arquitectura**: Hexagonal (Puertos y Adaptadores) con DDD
- **Framework**: Spring Boot 3.3.0
- **Base de datos**: PostgreSQL 15
- **Autenticación**: JWT
- **Documentación**: OpenAPI/Swagger
- **Migraciones**: Flyway
- **Testing**: JUnit 5 + Mockito
- **Despliegue**: Google Cloud Platform

## Estructura del Proyecto
```
src/
├── main/java/com/mybank/
│   ├── domains/
│   │   ├── account/          # Dominio de cuentas
│   │   ├── transaction/      # Dominio de transacciones
│   │   └── user/            # Dominio de usuarios
│   ├── shared/              # Componentes compartidos
│   └── MyBankApplication.java
├── main/resources/
│   ├── application.yml      # Configuración principal
│   ├── application-dev.yml  # Configuración desarrollo
│   └── db/migration/        # Migraciones Flyway
└── test/                    # Tests unitarios e integración
```

## Tecnologías
- **Backend**: Spring Boot 3.3.0, Java 21
- **Base de datos**: PostgreSQL 15
- **ORM**: Spring Data JPA
- **Autenticación**: JWT (JSON Web Tokens)
- **Documentación**: OpenAPI 3.0
- **Migraciones**: Flyway
- **Testing**: JUnit 5, Mockito, TestContainers
- **Build**: Gradle
- **Despliegue**: Google Cloud Run
- **Despliegue**: Google Cloud Run
- **Container**: Docker

## Configuración Local

### Prerrequisitos
- Java 21
- Docker y Docker Compose
- Gradle (opcional, se incluye wrapper)

### Instalación
1. **Clonar el repositorio**
   ```bash
   git clone <repository-url>
   cd mybank-api
   ```

2. **Configurar variables de entorno**
   ```bash
   cp env.example .env
   # Editar .env con tus configuraciones
   ```

3. **Iniciar servicios con Docker Compose**
   ```bash
   docker-compose up -d
   ```

4. **Ejecutar la aplicación**
   ```bash
   # Con Gradle Wrapper
   ./gradlew bootRun
   
   # O con Java directamente
   java -jar build/libs/mybank-api-0.1.0.jar
   ```

### Variables de Entorno
```bash
# Base de datos
DB_HOST=localhost
DB_PORT=5432
DB_NAME=mybank_db
DB_USER=mybank_app
DB_PASSWORD=MyBank2024!

# JWT
JWT_SECRET=tu_jwt_secret_super_seguro

# Perfil de Spring
SPRING_PROFILES_ACTIVE=dev
```

## API Endpoints

### Autenticación
- `POST /api/v1/auth/register` - Registro de usuario
- `POST /api/v1/auth/login` - Inicio de sesión

### Usuarios
- `GET /api/v1/users/{id}` - Obtener usuario por ID
- `PUT /api/v1/users/{id}` - Actualizar usuario
- `DELETE /api/v1/users/{id}` - Eliminar usuario

### Cuentas
- `POST /api/v1/accounts` - Crear cuenta
- `GET /api/v1/accounts/{id}` - Obtener cuenta por ID
- `PUT /api/v1/accounts/{id}` - Actualizar cuenta
- `DELETE /api/v1/accounts/{id}` - Eliminar cuenta

### Transacciones
- `POST /api/v1/transactions?userId={userId}` - Crear transacción
- `GET /api/v1/transactions/{id}` - Obtener transacción por ID
- `GET /api/v1/transactions/user/{userId}` - Obtener transacciones por usuario
- `GET /api/v1/transactions/account/{accountId}` - Obtener transacciones por cuenta

### Auditoría
- `GET /api/v1/transactions/audit/{transactionId}` - Obtener auditoría de transacción

### Health Check
- `GET /health` - Estado del servicio
- `GET /actuator/health` - Health check detallado
- `GET /actuator/info` - Información de la aplicación
- `GET /actuator/metrics` - Métricas de la aplicación

### Documentación
- `GET /swagger-ui.html` - Interfaz Swagger UI
- `GET /v3/api-docs` - Especificación OpenAPI

## Testing

### Ejecutar Tests
```bash
# Todos los tests
./gradlew test

# Tests específicos
./gradlew test --tests "*TransactionDomainServiceTest*"

# Con cobertura
./gradlew test jacocoTestReport
```

### Cobertura de Código
Los reportes de cobertura se generan en:
- `build/reports/jacoco/test/html/index.html` (HTML)
- `build/reports/jacoco/test/jacocoTestReport.xml` (XML)

## Despliegue

### Despliegue Manual
```bash
# Script de despliegue
./deploy.sh
```

### Despliegue Automático
El proyecto incluye scripts de despliegue automatizado:
1. **Build y Test**: Compila y ejecuta tests
2. **Deploy**: Despliegue a Google Cloud Run

**Comandos**:
- `./deploy.sh` - Despliegue manual
- `./deployment/fix-cloudrun-port.sh` - Corrección de problemas
- `./deployment/monitor-service.sh` - Monitoreo del servicio

## Estructura de Dominios

### Dominio de Usuario
- **Entidad**: `User`
- **Servicios**: `UserService`, `UserDomainService`
- **Repositorios**: `UserRepository`

### Dominio de Cuenta
- **Entidad**: `Account`
- **Servicios**: `AccountService`, `AccountDomainService`
- **Repositorios**: `AccountRepository`

### Dominio de Transacción
- **Entidad**: `Transaction`, `TransactionAudit`
- **Servicios**: `TransactionService`, `TransactionDomainService`, `TransactionAuditService`
- **Repositorios**: `TransactionRepository`, `TransactionAuditRepository`
- **Value Objects**: `Money`

## Arquitectura

### Principios DDD
- **Entidades**: Objetos con identidad única
- **Value Objects**: Objetos inmutables sin identidad
- **Servicios de Dominio**: Lógica de negocio compleja
- **Repositorios**: Abstracción de persistencia

### Arquitectura Hexagonal
- **Puertos de Entrada**: Controllers REST
- **Puertos de Salida**: Repositorios
- **Adaptadores**: Implementaciones concretas
- **Dominio**: Lógica de negocio pura

## Monitoreo y Observabilidad

### Health Checks
- `/health` - Estado básico del servicio
- `/actuator/health` - Health check detallado con dependencias

### Métricas
- `/actuator/metrics` - Métricas de la aplicación
- `/actuator/env` - Variables de entorno

### Logging
- Logs estructurados en formato JSON
- Niveles configurables por perfil

## Seguridad

### Autenticación JWT
- Tokens con expiración configurable
- Refresh tokens (futuro)
- Blacklisting de tokens (futuro)

### Validación de Entrada
- Validación con Bean Validation
- Sanitización de datos
- Rate limiting (futuro)

## Base de Datos

### Migraciones
Las migraciones se ejecutan automáticamente con Flyway:
- `V1__Create_initial_tables.sql` - Tablas iniciales
- `V3__Create_transactions_table.sql` - Tabla de transacciones
- `V4__Create_transaction_audit_logs_table.sql` - Tabla de auditoría

### Esquema
- **Usuarios**: Información de usuarios del sistema
- **Cuentas**: Cuentas bancarias asociadas a usuarios
- **Transacciones**: Movimientos financieros
- **Auditoría**: Log de cambios en transacciones

## Contribución

### Flujo de Trabajo
1. Crear feature branch desde `main`
2. Desarrollar funcionalidad
3. Ejecutar tests y validaciones
4. Crear Pull Request
5. Revisión de código
6. Merge a `main`

### Estándares de Código
- Java 21 features
- Spring Boot best practices
- Clean Code principles
- Test coverage > 80%

## Licencia
Este proyecto es parte del curso de Modernización de Software.

---

**Última actualización**: Julio 2024
**Versión**: 0.1.0
**Estado**: En desarrollo activo

<!-- Deployment Ready --> 