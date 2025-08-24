#!/usr/bin/env bash
set -euo pipefail
BASE="http://localhost:8080"
OUTDIR="/tmp/idor_registrations"
mkdir -p "$OUTDIR"

echo "Registering test users (only signup). Responses saved in $OUTDIR"

echo "1) manager"
http_code=$(curl -s -o "$OUTDIR/signup_manager.body" -w "%{http_code}" -X POST "$BASE/signup" -H "Content-Type: application/json" -d '{"email":"manager@example.com","username":"manager1","password":"AdminPass1!","name":"Admin","surname":"Test"}' || true)
echo "manager HTTP $http_code"

echo "2) agent"
http_code=$(curl -s -o "$OUTDIR/signup_agent.body" -w "%{http_code}" -X POST "$BASE/signup" -H "Content-Type: application/json" -d '{"email":"agent@example.com","username":"agent1","password":"AgentPass1!","name":"Agent","surname":"Test"}' || true)
echo "agent HTTP $http_code"

echo "3) user"
http_code=$(curl -s -o "$OUTDIR/signup_user.body" -w "%{http_code}" -X POST "$BASE/signup" -H "Content-Type: application/json" -d '{"email":"user@example.com","username":"user1","password":"UserPass1!","name":"User","surname":"Test"}' || true)
echo "user HTTP $http_code"

echo "Done. Files:"
ls -l "$OUTDIR"
echo "When ready, reply 'ok' and I'll proceed with login and tests using these accounts."