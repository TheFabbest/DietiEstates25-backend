#!/bin/bash
# Script per signup, login e creazione immobile usando residential.json
# Modifica BASE_URL / credenziali se necessario

BASE_URL="http://localhost:8080"
EMAIL="testuser@example.com"
PASSWORD="Password1!"
RES_PAYLOAD="residential.json"

# Optional: signup (decommenta se vuoi creare l'utente via API)
# curl -i -X POST "$BASE_URL/auth/signup" \
#   -H "Content-Type: application/json" \
#   -d '{"email":"'"$EMAIL"'","username":"testuser","password":"'"$PASSWORD"'","name":"Test","surname":"User"}'

# Login
echo "Logging in..."
curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"'"$EMAIL"'","password":"'"$PASSWORD"'"}' \
  -o /tmp/login.json -w "\nHTTP_STATUS:%{http_code}\n"

# Extract token (jq preferred, fallback to python)
if command -v jq >/dev/null 2>&1; then
  ACCESS_TOKEN=$(jq -r '.accessToken // .access_token // empty' /tmp/login.json)
else
  ACCESS_TOKEN=$(python3 - <<PY
import json
try:
  d=json.load(open('/tmp/login.json'))
  print(d.get('accessToken') or d.get('access_token') or '')
except:
  print('')
PY
)
fi

if [ -z "$ACCESS_TOKEN" ]; then
  echo "Access token non ottenuto. Contenuto /tmp/login.json:"
  cat /tmp/login.json || true
  exit 1
fi

echo "Access token ottenuto. Invio property..."
curl -s -X POST "$BASE_URL/properties" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  --data-binary @"$RES_PAYLOAD" -o /tmp/property_resp.json -w "\nHTTP_STATUS:%{http_code}\n"

echo "Property response:"
cat /tmp/property_resp.json || true