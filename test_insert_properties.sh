#!/bin/bash

# Script per inserire proprietà di test con coordinate diverse
BASE_URL="http://localhost:8080"
LOGIN_ENDPOINT="/auth/login"
CREATE_ENDPOINT="/properties"

# Credenziali di test dal populate.sql
EMAIL="success@example.com"
PASSWORD="Password123!"

echo "=== TEST INSERIMENTO PROPRIETÀ CON COORDINATE DIVERSE ==="
echo "Base URL: $BASE_URL"
echo

# 1. Login per ottenere il token
echo "=== 1. AUTENTICAZIONE ==="
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL$LOGIN_ENDPOINT" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"$EMAIL\",
    \"password\": \"$PASSWORD\"
  }")

echo "Risposta login: $LOGIN_RESPONSE"

# Estrai il token dalla risposta (assumendo formato JSON con campo "accessToken")
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo "ERRORE: Impossibile ottenere il token di autenticazione"
    echo "Risposta completa: $LOGIN_RESPONSE"
    exit 1
fi

echo "Token ottenuto: $TOKEN"
echo

# 2. Inserimento proprietà residential (centro Napoli)
echo "=== 2. INSERIMENTO PROPRIETÀ RESIDENTIAL (CENTRO) ==="
curl -X POST "$BASE_URL$CREATE_ENDPOINT" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '@payload/residential.json'
echo -e "\n"

# 3. Inserimento proprietà commercial (entro 500m)
echo "=== 3. INSERIMENTO PROPRIETÀ COMMERCIAL (500m) ==="
curl -X POST "$BASE_URL$CREATE_ENDPOINT" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '@payload/commercial.json'
echo -e "\n"

# 4. Inserimento proprietà garage (entro 1-2km)
echo "=== 4. INSERIMENTO PROPRIETÀ GARAGE (1-2km) ==="
curl -X POST "$BASE_URL$CREATE_ENDPOINT" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '@payload/garage.json'
echo -e "\n"

# 5. Inserimento proprietà land (oltre 10km)
echo "=== 5. INSERIMENTO PROPRIETÀ LAND (OLTRE 10km) ==="
curl -X POST "$BASE_URL$CREATE_ENDPOINT" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '@payload/land.json'
echo -e "\n"

echo "=== INSERIMENTO COMPLETATO ==="
echo "Le proprietà sono state inserite con indirizzi che verranno geocodificati con coordinate diverse"
echo "Piazza del Plebiscito 1 -> Centro Napoli (40.835934, 14.248782)"
echo "Via Toledo 100 -> ~500m dal centro"
echo "Via Caracciolo 75 -> ~1-2km dal centro" 
echo "Via Roma 200, Casoria -> ~10km+ dal centro"