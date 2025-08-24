# Test locali per autenticazione e claims JWT

1) Ottenere access token (login)
curl -s -X POST http://localhost:8080/api/auth/login \\
  -H "Content-Type: application/json" \\
  -d '{ "username": "tuo_username", "password": "tua_password" }' | jq -r '.accessToken'

2) Verificare i claim direttamente dal token (bash + jq)
TOKEN=ey... # sostituisci con il token ottenuto
echo $TOKEN | cut -d'.' -f2 | base64 --decode | jq

Cerca i campi: \"id\", \"isManager\", \"roles\".

3) Chiamare endpoint protetto usando il token
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/protected/endpoint

Sostituisci l'URL con un endpoint reale protetto dell'app, ad es. /api/properties/1 o altri.

4) Casi di test consigliati
- Token valido con ruolo ROLE_AGENT: verifica accesso alle risorse dell'agente.
- Token valido con isManager=true: verifica accesso globale.
- Token senza claim id/isManager: dovrebbe comportarsi come utente non autorizzato (dipende dal ruolo).
