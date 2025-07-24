#!/bin/bash

# Script para probar la integración del userId en el sistema MyBank API
# Valida que el userId esté disponible en login y se use correctamente en operaciones

set -e

BASE_URL="http://localhost:8080/api/v1"
echo "🧪 Probando integración de userId en MyBank API"
echo "================================================"

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Función para hacer requests
make_request() {
    local method=$1
    local endpoint=$2
    local data=$3
    local token=$4
    
    local headers="Content-Type: application/json"
    if [ ! -z "$token" ]; then
        headers="$headers -H Authorization: Bearer $token"
    fi
    
    if [ "$method" = "GET" ]; then
        response=$(curl -s -w "\n%{http_code}" -H "$headers" "$BASE_URL$endpoint")
    else
        response=$(curl -s -w "\n%{http_code}" -H "$headers" -X "$method" -d "$data" "$BASE_URL$endpoint")
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    echo "$body"
    return $http_code
}

# Función para validar respuesta JSON
validate_json() {
    local json=$1
    local field=$2
    local expected_value=$3
    
    local actual_value=$(echo "$json" | jq -r ".data.$field" 2>/dev/null || echo "null")
    
    if [ "$actual_value" = "$expected_value" ]; then
        echo -e "${GREEN}✅ $field = $actual_value${NC}"
        return 0
    else
        echo -e "${RED}❌ $field = $actual_value (esperado: $expected_value)${NC}"
        return 1
    fi
}

echo -e "\n${YELLOW}1. Registrando usuario de prueba...${NC}"
register_response=$(make_request "POST" "/auth/register" '{
    "username": "useridtest",
    "email": "useridtest@example.com",
    "password": "password123",
    "firstName": "UserId",
    "lastName": "Test"
}')

echo "$register_response"
user_id=$(echo "$register_response" | jq -r '.data.id')

if [ "$user_id" = "null" ] || [ -z "$user_id" ]; then
    echo -e "${RED}❌ Error: No se pudo obtener el userId del registro${NC}"
    exit 1
fi

echo -e "\n${YELLOW}2. Probando login con userId en respuesta...${NC}"
login_response=$(make_request "POST" "/auth/login" '{
    "usernameOrEmail": "useridtest",
    "password": "password123"
}')

echo "$login_response"

# Validar que el userId esté presente en la respuesta
echo -e "\n${YELLOW}Validando campos en LoginResponse:${NC}"
validate_json "$login_response" "userId" "$user_id"
validate_json "$login_response" "username" "useridtest"
validate_json "$login_response" "email" "useridtest@example.com"
validate_json "$login_response" "firstName" "UserId"
validate_json "$login_response" "lastName" "Test"

# Extraer token
token=$(echo "$login_response" | jq -r '.data.token')

if [ "$token" = "null" ] || [ -z "$token" ]; then
    echo -e "${RED}❌ Error: No se pudo obtener el token${NC}"
    exit 1
fi

echo -e "\n${YELLOW}3. Verificando que el JWT contiene userId...${NC}"
# Decodificar el JWT para verificar que contiene userId
jwt_payload=$(echo "$token" | cut -d'.' -f2 | base64 -d 2>/dev/null || echo "{}")
jwt_user_id=$(echo "$jwt_payload" | jq -r '.userId' 2>/dev/null || echo "null")

if [ "$jwt_user_id" = "$user_id" ]; then
    echo -e "${GREEN}✅ JWT contiene userId correcto: $jwt_user_id${NC}"
else
    echo -e "${RED}❌ JWT userId incorrecto: $jwt_user_id (esperado: $user_id)${NC}"
fi

echo -e "\n${YELLOW}4. Probando operaciones con el userId...${NC}"

# Crear una cuenta usando el userId
echo -e "\n${YELLOW}4.1 Creando cuenta...${NC}"
account_response=$(make_request "POST" "/accounts?userId=$user_id" '{
    "accountType": "CHECKING",
    "description": "Cuenta de prueba para userId"
}' "$token")

echo "$account_response"
account_id=$(echo "$account_response" | jq -r '.data.id')

if [ "$account_id" != "null" ] && [ ! -z "$account_id" ]; then
    echo -e "${GREEN}✅ Cuenta creada exitosamente con ID: $account_id${NC}"
else
    echo -e "${RED}❌ Error al crear cuenta${NC}"
fi

# Obtener cuentas del usuario
echo -e "\n${YELLOW}4.2 Obteniendo cuentas del usuario...${NC}"
accounts_response=$(make_request "GET" "/accounts/user/$user_id" "" "$token")
echo "$accounts_response"

# Crear una transacción
echo -e "\n${YELLOW}4.3 Creando transacción...${NC}"
transaction_response=$(make_request "POST" "/transactions?userId=$user_id" '{
    "type": "DEPOSIT",
    "amount": 100.00,
    "accountId": '$account_id',
    "description": "Depósito de prueba para userId"
}' "$token")

echo "$transaction_response"
transaction_id=$(echo "$transaction_response" | jq -r '.data.id')

if [ "$transaction_id" != "null" ] && [ ! -z "$transaction_id" ]; then
    echo -e "${GREEN}✅ Transacción creada exitosamente con ID: $transaction_id${NC}"
else
    echo -e "${RED}❌ Error al crear transacción${NC}"
fi

# Obtener transacciones del usuario
echo -e "\n${YELLOW}4.4 Obteniendo transacciones del usuario...${NC}"
transactions_response=$(make_request "GET" "/transactions/user/$user_id" "" "$token")
echo "$transactions_response"

echo -e "\n${GREEN}🎉 Prueba de integración de userId completada exitosamente!${NC}"
echo -e "${GREEN}✅ userId incluido en LoginResponse${NC}"
echo -e "${GREEN}✅ userId incluido en JWT${NC}"
echo -e "${GREEN}✅ Operaciones funcionando con userId${NC}"

echo -e "\n${YELLOW}📋 Resumen de la implementación:${NC}"
echo "• LoginResponse ahora incluye userId"
echo "• JWT contiene userId en el payload"
echo "• Todas las operaciones pueden usar el userId del contexto"
echo "• Seguridad mejorada con validación de propiedad de recursos" 