# Sistema de Auditoría de Transacciones

## Descripción General

El sistema de auditoría de transacciones registra y rastrea todos los eventos relevantes de las transacciones bancarias. Proporciona trazabilidad, cumplimiento y capacidades de monitoreo y análisis.

## Arquitectura

### Componentes Principales
- **TransactionAudit** — Entidad de auditoría
- **TransactionAuditRepository** — Repositorio de datos
- **TransactionAuditService** — Servicio de dominio
- **TransactionAuditApplicationService** — Servicio de aplicación
- **TransactionAuditController** — Controlador REST

### Flujo de Auditoría
```
Transacción → TransactionDomainService → TransactionAuditService → Base de Datos
```

## Entidad de Auditoría

La entidad `TransactionAudit` registra:
- Información de la transacción: ID, número, tipo, monto
- Estados: anterior y nuevo
- Usuario: ID y username
- Cuentas involucradas
- Evento: tipo y descripción
- Metadatos: IP, User-Agent, Session ID
- Timestamps

### Tipos de Eventos
- TRANSACTION_CREATED
- TRANSACTION_PROCESSING
- TRANSACTION_COMPLETED
- TRANSACTION_FAILED
- BALANCE_UPDATED
- VALIDATION_FAILED
- SYSTEM_ERROR

## Servicios

- `logEvent()` — Registra eventos generales
- `logStatusChange()` — Cambios de estado
- `logValidationFailure()` — Fallos de validación
- `logSystemError()` — Errores del sistema

## API REST

### Endpoints Principales
- `GET /api/v1/transactions/audit/transaction/{transactionId}`
- `GET /api/v1/transactions/audit/user/{userId}`
- `GET /api/v1/transactions/audit/account/{accountId}`
- `GET /api/v1/transactions/audit/date-range?startDate=...&endDate=...`

## Base de Datos

Tabla principal: `transaction_audit_logs` (ver migraciones en `db/migration/`)

## Integración con Transacciones

La auditoría es **automática** en:
- Creación de transacciones
- Procesamiento (cambios de estado)
- Validaciones fallidas
- Errores del sistema

## Casos de Uso
- Cumplimiento regulatorio y trazabilidad
- Detección de fraude y patrones sospechosos
- Investigación de incidentes
- Monitoreo operacional y métricas

## Seguridad
- Acceso restringido a endpoints de auditoría
- Encriptación/máscara de datos sensibles
- Retención y políticas de cumplimiento

## Testing
- Pruebas unitarias: `TransactionAuditServiceTest`
- Pruebas de integración: endpoints REST y consultas

---

**Última actualización:** Julio 2025 