# Colecciones de Postman para MyBank API

Este directorio contiene las colecciones de Postman para probar la API de MyBank, incluyendo el sistema completo de auditoría de transacciones.

## 📁 Archivos Incluidos

- **`MyBank-API.postman_collection.json`** - Colección principal con todos los endpoints
- **`MyBank-API.postman_environment.json`** - Variables de entorno para configuración
- **`README.md`** - Este archivo de documentación

## 🚀 Configuración Inicial

### 1. Importar la Colección
1. Abre Postman
2. Haz clic en "Import"
3. Selecciona el archivo `MyBank-API.postman_collection.json`
4. La colección se importará con todos los endpoints organizados

### 2. Configurar el Entorno
1. En Postman, haz clic en "Environments" en el panel izquierdo
2. Haz clic en "Import"
3. Selecciona el archivo `MyBank-API.postman_environment.json`
4. Selecciona el entorno "MyBank API - Local Environment"

### 3. Variables de Entorno Configuradas

| Variable | Valor por Defecto | Descripción |
|----------|-------------------|-------------|
| `baseUrl` | `http://localhost:8080` | URL base de la API |
| `authToken` | (vacío) | Token JWT de autenticación |
| `userId` | `1` | ID del usuario de prueba |
| `accountId` | `1` | ID de la cuenta de prueba |
| `transactionId` | `1` | ID de la transacción de prueba |
| `username` | `testuser` | Nombre de usuario de prueba |
| `password` | `password123` | Contraseña de prueba |

## 📋 Estructura de la Colección

### 1. Health Check
- **Health Check** - Verificar el estado de la aplicación

### 2. Authentication
- **User Registration** - Registrar un nuevo usuario
- **User Login** - Iniciar sesión y obtener token JWT

### 3. Users
- **Get All Users** - Obtener lista paginada de usuarios
- **Get User by ID** - Obtener usuario por ID
- **Create User** - Crear un nuevo usuario

### 4. Accounts
- **Get All Accounts** - Obtener lista paginada de cuentas
- **Get Account by ID** - Obtener cuenta por ID
- **Get Account by Number** - Obtener cuenta por número
- **Get User Accounts** - Obtener cuentas de un usuario
- **Create Account** - Crear una nueva cuenta

### 5. Transactions
- **Get All Transactions** - Obtener lista paginada de transacciones
- **Get Transaction by ID** - Obtener transacción por ID
- **Get Transaction by Number** - Obtener transacción por número
- **Get User Transactions** - Obtener transacciones de un usuario
- **Get Account Transactions** - Obtener transacciones de una cuenta
- **Create Deposit Transaction** - Crear transacción de depósito
- **Create Withdrawal Transaction** - Crear transacción de retiro
- **Create Transfer Transaction (DISABLED)** - Crear transacción de transferencia (NO IMPLEMENTADO)
- **Process Transaction** - Procesar una transacción pendiente

### 6. Transaction Audit ⭐ (NUEVO)
- **Get Transaction Audit History by ID** - Auditoría por ID de transacción
- **Get Transaction Audit History by Number** - Auditoría por número de transacción
- **Get User Audit History** - Auditoría de un usuario
- **Get User Audit History Paginated** - Auditoría de usuario con paginación
- **Get Account Audit History by ID** - Auditoría de cuenta por ID
- **Get Account Audit History by Number** - Auditoría de cuenta por número
- **Get Audit History by Date Range** - Auditoría por rango de fechas
- **Get Audit History by Date Range Paginated** - Auditoría por fechas con paginación
- **Get Audit History by Event Type** - Auditoría por tipo de evento
- **Get Audit History by Event Type Paginated** - Auditoría por evento con paginación
- **Search Audit History** - Búsqueda en auditoría
- **Get All Audit History Paginated** - Toda la auditoría con paginación
- **Get Audit History by IP Address** - Auditoría por dirección IP
- **Get Audit History by Session ID** - Auditoría por ID de sesión

## 🔄 Flujo de Pruebas Recomendado

### Paso 1: Verificar Salud de la API
1. Ejecuta **Health Check** para verificar que la API esté funcionando

### Paso 2: Autenticación
1. Ejecuta **User Registration** para crear un usuario de prueba
2. Ejecuta **User Login** para obtener el token JWT
3. Copia el token del response y actualiza la variable `authToken` en el entorno

### Paso 3: Crear Datos de Prueba
1. Ejecuta **Create Account** para crear una cuenta
2. Ejecuta **Create Deposit Transaction** para crear una transacción de depósito
3. Ejecuta **Process Transaction** para procesar la transacción

### Paso 4: Probar Auditoría
1. Ejecuta **Get Transaction Audit History by ID** para ver la auditoría
2. Ejecuta **Get User Audit History** para ver auditoría del usuario
3. Ejecuta **Get Account Audit History by ID** para ver auditoría de la cuenta
4. Prueba diferentes filtros y paginación

## 🔍 Tipos de Eventos de Auditoría

La API de auditoría registra los siguientes tipos de eventos:

- **TRANSACTION_CREATED** - Transacción creada
- **TRANSACTION_PROCESSING** - Transacción en procesamiento
- **TRANSACTION_COMPLETED** - Transacción completada
- **TRANSACTION_FAILED** - Transacción falló
- **TRANSACTION_CANCELLED** - Transacción cancelada
- **TRANSACTION_REVERSED** - Transacción revertida
- **BALANCE_UPDATED** - Balance actualizado
- **VALIDATION_FAILED** - Validación falló
- **AUTHORIZATION_REQUIRED** - Autorización requerida
- **FRAUD_DETECTED** - Fraude detectado
- **COMPLIANCE_CHECK** - Verificación de cumplimiento
- **SYSTEM_ERROR** - Error del sistema

## 📊 Ejemplos de Respuestas

### Respuesta de Auditoría
```json
{
  "success": true,
  "message": "Audit history retrieved successfully",
  "data": [
    {
      "id": 1,
      "transactionId": 1,
      "transactionNumber": "TXN1234567890123456789",
      "previousStatus": "PENDING",
      "newStatus": "PROCESSING",
      "transactionType": "DEPOSIT",
      "amount": 500.00,
      "totalAmount": 500.00,
      "userId": 1,
      "userUsername": "testuser",
      "accountId": 1,
      "accountNumber": "AC1234567890123456789",
      "auditEventType": "TRANSACTION_PROCESSING",
      "eventDescription": "Status changed from PENDING to PROCESSING: Transaction processing started",
      "createdAt": "2024-01-01T10:00:00"
    }
  ]
}
```

## 🛠️ Configuración para Diferentes Ambientes

### Desarrollo Local
```json
{
  "baseUrl": "http://localhost:8080"
}
```

### Desarrollo Remoto
```json
{
  "baseUrl": "http://dev-api.mybank.com"
}
```

### Producción
```json
{
  "baseUrl": "https://api.mybank.com"
}
```

## 🔧 Troubleshooting

### Problema: Error 401 Unauthorized
**Solución**: Verifica que el token JWT sea válido y esté actualizado en la variable `authToken`

### Problema: Error 404 Not Found
**Solución**: Verifica que la URL base sea correcta y que la API esté ejecutándose

### Problema: Error 500 Internal Server Error
**Solución**: Verifica los logs de la aplicación para identificar el problema específico

## 📝 Notas Importantes

1. **Autenticación**: La mayoría de endpoints requieren autenticación JWT
2. **Paginación**: Los endpoints de listado soportan paginación con parámetros `page` y `size`
3. **Auditoría**: Todos los eventos de transacciones se registran automáticamente
4. **Filtros**: La auditoría soporta múltiples filtros: por usuario, cuenta, fecha, tipo de evento, etc.
5. **Búsqueda**: Se puede buscar en las descripciones de eventos de auditoría

## 🎯 Próximos Pasos

1. Importar la colección en Postman
2. Configurar el entorno local
3. Ejecutar las pruebas en el orden recomendado
4. Explorar las funcionalidades de auditoría
5. Personalizar las variables según tus necesidades 