# 🧪 Testing Strategy - MyBank API

## 📋 Overview

La estrategia de testing de MyBank API se basa en pruebas unitarias, de integración y validación funcional automatizada mediante scripts bash.

## 🎯 Objetivos
- Cobertura de código ≥ 80%
- Validación de todos los flujos críticos de la API
- Documentación viva del comportamiento real

## 🏗️ Estructura de Testing

- **Unitarios:**
  - Ubicación: `src/test/java/com/mybank/`
  - Por dominio: `user`, `account`, `transaction`, `shared`
- **Integración:**
  - Ubicación: `src/test/java/com/mybank/`
  - Incluye pruebas de controladores y servicios
- **Validación funcional:**
  - Scripts: `final-fixed-test.sh`, `verification-after-adjustments.sh`

## 🧪 Ejecución de Pruebas

- **Unitarias:**
  ```bash
  ./gradlew test
  ```
- **Validación funcional completa:**
  ```bash
  ./final-fixed-test.sh
  ./verification-after-adjustments.sh
  ```

## 🗂️ Estructura de carpetas de tests

```
src/test/java/com/mybank/
├── domains/
│   ├── user/domain/service/UserDomainServiceTest.java
│   ├── account/domain/service/AccountDomainServiceTest.java
│   └── transaction/domain/service/TransactionDomainServiceTest.java
│   └── transaction/domain/service/TransactionAuditServiceTest.java
│   └── transaction/domain/valueobject/MoneyTest.java
└── shared/
    └── security/JwtServiceTest.java
```

## 📝 Scripts de validación

- `final-fixed-test.sh`: Valida todos los flujos principales, procesamiento, errores y auditoría.
- `verification-after-adjustments.sh`: Verificación rápida tras cambios o limpieza.

## 🛠️ Comandos útiles

```bash
# Ejecutar todos los tests
./gradlew test

# Validar todos los flujos principales
yes | ./final-fixed-test.sh

# Verificación rápida post-ajustes
./verification-after-adjustments.sh
```

## 📈 Cobertura y Reportes

- Cobertura con Jacoco:
  ```bash
  ./gradlew test jacocoTestReport
  open build/reports/jacoco/test/html/index.html
  ```
- Reporte de tests:
  ```bash
  open build/reports/tests/test/index.html
  ```

## 🧩 Documentación
- [README.md](README.md)
- [ARCHITECTURE.md](ARCHITECTURE.md)
- [TRANSACTION_DOMAIN.md](TRANSACTION_DOMAIN.md)
- [TRANSACTION_AUDIT.md](TRANSACTION_AUDIT.md)

---

**Última actualización:** Julio 2025 