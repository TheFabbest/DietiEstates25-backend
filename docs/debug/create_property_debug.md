Debug: creazione immobile — istruzioni e payload di test

Scopo: testare POST /properties su servizio locale avviato con [`runLocally.sh`](runLocally.sh:1).

Prerequisiti:
- Servizio in esecuzione (vedi Terminal 5).
- accessToken ottenuto via POST /login; conserva il token per Authorization: Bearer <ACCESS_TOKEN>.
- L'utente indicato in agentUsername deve avere ruolo AGENT (vedi login response availableRoles).

Note tecniche:
- DTO base: [`CreatePropertyRequest`](src/main/java/com/dieti/dietiestatesbackend/dto/request/CreatePropertyRequest.java:31).
- Logica server: [`PropertyService`](src/main/java/com/dieti/dietiestatesbackend/service/PropertyService.java:111).
- propertyType deve essere uno di: RESIDENTIAL, COMMERCIAL, LAND, GARAGE.
- Indirizzo: inviare addressId (se esistente) oppure addressRequest con campi richiesti.
- agentUsername è obbligatorio.

Esempio: ottieni token (se non lo hai)
```bash
curl -sS -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test.user@example.com","password":"Password123!"}'
```

Payload di esempio — RESIDENTIAL
```json
{
  "propertyType":"RESIDENTIAL",
  "agentUsername":"testuser",
  "description":"Appartamento di prova",
  "price":120000,
  "area":80,
  "yearBuilt":1998,
  "contractType":"SALE",
  "propertyCategoryName":"Appartamento",
  "addressRequest":{
    "country":"IT",
    "province":"RM",
    "city":"Rome",
    "street":"Via Test",
    "streetNumber":"1"
  },
  "images":["/img/1.jpg","/img/2.jpg"],
  "numeroLocali":3,
  "numeroBagni":2,
  "postiAuto":1,
  "heatingType":"GAS",
  "giardino":"NO",
  "numeroPianiTotali":3,
  "haAscensore":false
}
```

curl per inviare il RESIDENTIAL
```bash
curl -i -X POST http://localhost:8080/properties \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -d '@residential.json'
```

Payload di esempio — COMMERCIAL
```json
{
  "propertyType":"COMMERCIAL",
  "agentUsername":"testuser",
  "description":"Locale commerciale prova",
  "price":250000,
  "area":120,
  "contractType":"RENT",
  "addressRequest":{
    "country":"IT",
    "province":"MI",
    "city":"Milan",
    "street":"Corso Prova",
    "streetNumber":"10"
  },
  "numeroLocali":4,
  "piano":1,
  "numeroBagni":2,
  "numeroPianiTotali":1,
  "haAccessoDisabili":true,
  "numeroVetrine":2
}
```

Payload di esempio — LAND
```json
{
  "propertyType":"LAND",
  "agentUsername":"testuser",
  "description":"Terreno edificabile",
  "price":80000,
  "area":1000,
  "addressRequest":{
    "country":"IT",
    "province":"FI",
    "city":"Florence",
    "street":"Via Campi",
    "streetNumber":"0"
  }
}
```

Payload di esempio — GARAGE
```json
{
  "propertyType":"GARAGE",
  "agentUsername":"testuser",
  "description":"Box auto",
  "price":20000,
  "area":20,
  "addressRequest":{
    "country":"IT",
    "province":"RM",
    "city":"Rome",
    "street":"Via Garage",
    "streetNumber":"5"
  }
}
```

Consigli di debug
- Se ricevi 400: controlla body della risposta e i log del server nel terminale (Terminal 5).
- Se ottieni EntityNotFoundException: verifica contractType, propertyCategoryName o addressId.
- Controlla i log in tempo reale in VSCode Terminal 5 (servizio avviato via [`runLocally.sh`](runLocally.sh:1)).

Dove salvare i payload
- Salva i JSON di test in file locali, es. `residential.json`, `commercial.json` e invia con curl -d '@file.json'.

Dopo aver eseguito, incolla qui status code e body della risposta; io analizzerò i log e apporterò correzioni se necessario.