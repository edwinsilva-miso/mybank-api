# Arquitectura por Dominios - Clean Architecture

## 🎯 Visión General

MyBank API está diseñado siguiendo los principios de **Clean Architecture** y organizado por **dominios de negocio**, permitiendo:
- Escalabilidad
- Mantenibilidad
- Testabilidad
- Independencia de dominios

## 🏗️ Estructura de Dominios (actual)

```
src/main/java/com/mybank/
├── shared/                  # Configuración, seguridad, utilidades, excepciones
├── domains/
│   ├── user/                # Dominio de Usuarios
│   ├── account/             # Dominio de Cuentas
│   └── transaction/         # Dominio de Transacciones
```

Cada dominio contiene:
- `application/` — Casos de uso y servicios de aplicación
- `domain/` — Entidades, servicios y repositorios de dominio
- `infrastructure/` — Implementaciones técnicas (solo si aplica)
- `presentation/` — Controladores REST

## 🧩 Capas de Clean Architecture

1. **Presentation Layer**: Controladores REST, entrada/salida
2. **Application Layer**: Orquestación de casos de uso
3. **Domain Layer**: Lógica de negocio, entidades, reglas
4. **Infrastructure Layer**: Persistencia, integración técnica

## 🔄 Flujo de Datos

```
HTTP Request → Controller → Application Service → Domain Service → Repository
```

## 📦 Dominios Implementados

- **User Domain** ✅
- **Account Domain** ✅
- **Transaction Domain** ✅
- **Transaction Audit Domain** ✅

## 🏆 Beneficios
- Escalabilidad y mantenibilidad
- Testabilidad por capas
- Flexibilidad para agregar nuevos dominios

## 🛠️ Configuración y Validaciones por Dominio
- Cada dominio puede tener validaciones y configuraciones propias
- Manejo de errores y documentación independiente

## 🗺️ Roadmap de Implementación
1. User Domain ✅
2. Account Domain ✅
3. Transaction Domain ✅
4. Transaction Audit Domain ✅

---

**Última actualización:** Julio 2025 