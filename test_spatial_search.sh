#!/bin/bash

# Script per testare la ricerca spaziale con diversi raggi
BASE_URL="http://localhost:8080"
LOGIN_ENDPOINT="/auth/login"
SEARCH_ENDPOINT="/properties/search/test"

# Credenziali di test
EMAIL="success@example.com"
PASSWORD="Password123!"

echo "=== TEST RICERCA SPAZIALE CON DIVERSI RAGGI ==="
echo "Base URL: $BASE_URL"
echo

# 1. Login per ottenere il token
echo "=== AUTENTICAZIONE ==="
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL$LOGIN_ENDPOINT" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"$EMAIL\",
    \"password\": \"$PASSWORD\"
  }")

echo "Risposta login: $LOGIN_RESPONSE"

# Estrai il token dalla risposta
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo "ERRORE: Impossibile ottenere il token di autenticazione"
    echo "Risposta completa: $LOGIN_RESPONSE"
    exit 1
fi

echo "Token ottenuto: $TOKEN"
echo

# Coordinate centro Napoli (Piazza del Plebiscito)
CENTER_LAT="40.835934"
CENTER_LON="14.248782"

# Test 1: Raggio molto piccolo (100m) - dovrebbe trovare solo la proprietà centrale
echo "=== TEST 1: RAGGIO 100m (SOLO CENTRO) ==="
curl -X POST "$BASE_URL$SEARCH_ENDPOINT" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"centerLatitude\": $CENTER_LAT,
    \"centerLongitude\": $CENTER_LON,
    \"radiusInMeters\": 100
  }"
echo -e "\n"

# Test 2: Raggio 500m - dovrebbe trovare proprietà centrale e commercial
echo "=== TEST 2: RAGGIO 500m (CENTRO + COMMERCIAL) ==="
curl -X POST "$BASE_URL$SEARCH_ENDPOINT" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"centerLatitude\": $CENTER_LAT,
    \"centerLongitude\": $CENTER_LON,
    \"radiusInMeters\": 500
  }"
echo -e "\n"

# Test 3: Raggio 2000m (2km) - dovrebbe trovare centro, commercial e garage
echo "=== TEST 3: RAGGIO 2000m (2km) ==="
curl -X POST "$BASE_URL$SEARCH_ENDPOINT" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"centerLatitude\": $CENTER_LAT,
    \"centerLongitude\": $CENTER_LON,
    \"radiusInMeters\": 2000
  }"
echo -e "\n"

# Test 4: Raggio 5000m (5km) - dovrebbe trovare tutte tranne Casoria
echo "=== TEST 4: RAGGIO 5000m (5km) ==="
curl -X POST "$BASE_URL$SEARCH_ENDPOINT" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"centerLatitude\": $CENTER_LAT,
    \"centerLongitude\": $CENTER_LON,
    \"radiusInMeters\": 5000
  }"
echo -e "\n"

# Test 5: Raggio 15000m (15km) - dovrebbe trovare tutte le proprietà
echo "=== TEST 5: RAGGIO 15000m (15km) ==="
curl -X POST "$BASE_URL$SEARCH_ENDPOINT" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"centerLatitude\": $CENTER_LAT,
    \"centerLongitude\": $CENTER_LON,
    \"radiusInMeters\": 15000
  }"
echo -e "\n"

# Test 6: Ricerca senza filtri geografici (compatibilità)
echo "=== TEST 6: RICERCA SENZA FILTRI GEOGRAFICI ==="
curl -X POST "$BASE_URL$SEARCH_ENDPOINT" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{}"
echo -e "\n"

echo "=== TEST COMPLETATI ==="
echo "Riepilogo proprietà inserite con coordinate:"
echo "- ID 39: Piazza del Plebiscito 1 (40.83684755, 14.25099199) - ~100m dal centro"
echo "- ID 40: Via Toledo 100 (40.84526, 14.2491901) - ~500m dal centro" 
echo "- ID 41: Via Caracciolo 75 (40.90047001, 14.20986936) - ~1-2km dal centro"
echo "- ID 42: Via Roma 200, Casoria (40.907569, 14.291872) - ~10km+ dal centro"