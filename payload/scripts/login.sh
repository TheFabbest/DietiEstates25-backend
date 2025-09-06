#!/bin/bash

# Script di login per DietiEstates25-backend

EMAIL="success@example.com"
PASSWORD="Password123!"
URL="http://localhost:8080/auth/login"

curl -X POST -H "Content-Type: application/json" \
    -d "{\"email\": \"$EMAIL\", \"password\": \"$PASSWORD\"}" \
    "$URL"