#!/bin/bash
# Create property using residential.json
BASE_URL="http://localhost:8080"
RES_PAYLOAD="residential.json"

# Try to obtain ACCESS_TOKEN from env or /tmp/login.json
if [ -z "$ACCESS_TOKEN" ]; then
  if [ -f /tmp/login.json ]; then
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
  fi
fi

if [ -z "$ACCESS_TOKEN" ]; then
  echo "ERROR: set ACCESS_TOKEN env var or ensure /tmp/login.json exists"
  exit 1
fi

curl -i -X POST "$BASE_URL/properties" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  --data-binary @"$RES_PAYLOAD"