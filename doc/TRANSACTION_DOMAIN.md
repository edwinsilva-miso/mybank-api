# Dominio de Transacciones - MyBank API

## �� Visión General

El dominio de transacciones es el núcleo del sistema bancario, responsable de manejar todas las operaciones financieras de los usuarios. Implementa la lógica de negocio para depósitos, retiros, transferencias y pagos, siguiendo Clean Architecture.

## 🏗️ Arquitectura del Dominio (actual)

```
domains/transaction/
├── application/           # Casos de uso y servicios
├── domain/               # Entidades, servicios, repositorios
├── infrastructure/       # Implementaciones técnicas (si aplica)
└── presentation/         # Controladores REST
```

## 💸 Tipos de Transacciones
- **DEPOSIT**: Ingreso de dinero
- **WITHDRAWAL**: Retiro de dinero
- **TRANSFER**: Movimiento entre cuentas (NO IMPLEMENTADO - DESACTIVADO)
- **PAYMENT**: Pago de servicios

## 🔄 Estados de Transacción
- **PENDING**: Pendiente
- **PROCESSING**: Procesando
- **COMPLETED**: Completada
- **FAILED**: Fallida

## 🧩 Componentes Principales
- **Transaction**: Entidad principal
- **Money**: Objeto de valor para montos
- **TransactionDomainService**: Lógica de negocio
- **TransactionService**: Orquestación y casos de uso

## 🚀 Endpoints Principales
- `POST /api/v1/transactions?userId={userId}` — Crear transacción
- `POST /api/v1/transactions/{transactionId}/process` — Procesar transacción
- `GET /api/v1/transactions/{transactionId}` — Obtener transacción
- `GET /api/v1/transactions/user/{userId}` — Transacciones de usuario
- `GET /api/v1/transactions/account/{accountId}` — Transacciones por cuenta
- `GET /api/v1/transactions/pending` — Transacciones pendientes

## 🛡️ Reglas de Negocio
- Monto debe ser mayor a cero
- Fondos suficientes para retiros/pagos (transferencias desactivadas)
- Cuentas deben existir y estar activas
- Solo transacciones PENDING pueden ser procesadas

## 🔗 Integración con Auditoría
- Todas las transacciones y cambios de estado son auditados automáticamente
- Eventos de auditoría: creación, procesamiento, validaciones fallidas, errores

## 🧪 Testing
- Pruebas unitarias: servicios de dominio y aplicación
- Pruebas de integración: controladores y repositorios
- Validación funcional: `final-fixed-test.sh`, `verification-after-adjustments.sh`

## 📈 Métricas y Monitoreo
- Número de transacciones por tipo
- Tasa de éxito/fallo
- Volumen y tiempos de procesamiento

---

**Última actualización:** Julio 2025 