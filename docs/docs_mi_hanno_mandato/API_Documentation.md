# Documentazione API DietiEstates

Questa documentazione fornisce una guida completa per l'utilizzo delle API RESTful del backend DietiEstates25. Include esempi pratici e dettagliati per illustrare la struttura dei payload di richiesta e risposta, nonché l'utilizzo corretto degli URL, inclusi i parametri di path e query. L'obiettivo è rendere la documentazione completamente autosufficiente per gli sviluppatori che consumano l'API, minimizzando la necessità di supporto diretto o di spiegazioni aggiuntive sul funzionamento del backend e facilitando un'integrazione rapida ed efficace.

## Introduzione

Le API di DietiEstates consentono l'interazione con il sistema di gestione immobiliare, dalla ricerca di proprietà alla gestione degli utenti e dei contratti. L'autenticazione è gestita tramite JWT (JSON Web Tokens). Alcuni endpoint sono pubblici, mentre altri richiedono un token di accesso valido.

## Configurazione

Per interagire con le API, è necessario configurare le credenziali appropriate:
-   **JWT Secret:** Utilizzato per la firma e la verifica dei token JWT.
-   **Geoapify API Key:** Necessaria per le funzionalità di geocoding e ricerca di luoghi di interesse.

## Geocoding Automatico

Il sistema esegue il geocoding automatico degli indirizzi forniti durante la creazione delle proprietà, convertendo gli indirizzi testuali in coordinate geografiche (latitudine e longitudine).

---

# Esempi Dettagliati per Endpoint API DietiEstates

## 🔐 **AuthController** - Endpoint di autenticazione

### **POST** [`/auth/login`](src/main/java/com/dieti/dietiestatesbackend/controller/AuthController.java:70)
Autentica un utente e restituisce token di accesso e refresh.

**URL:** `https://api.dietiestates.com/auth/login`

**Request Body:**
```json
{
  "email": "mario.rossi@email.com",
  "password": "Password123!"
}
```

**Response Body:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "availableRoles": ["ADMIN", "AGENT"]
}
```

### **POST** [`/auth/signup`](src/main/java/com/dieti/dietiestatesbackend/controller/AuthController.java:86)
Registra un nuovo utente.

**URL:** `https://api.dietiestates.com/auth/signup`

**Request Body:**
```json
{
  "email": "mario.rossi@email.com",
  "username": "mariorossi",
  "password": "Password123!",
  "name": "Mario",
  "surname": "Rossi"
}
```

**Response Body:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "availableRoles": []
}
```

### **POST** [`/auth/google`](src/main/java/com/dieti/dietiestatesbackend/controller/AuthController.java:92)
Autentica un utente tramite Google.

**URL:** `https://api.dietiestates.com/auth/google`

**Request Body:**
```json
{
  "token": "ya29.a0AfH6SMC...",
  "username": "mariorossi",
  "name": "Mario",
  "surname": "Rossi"
}
```

**Response Body:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "availableRoles": ["USER"]
}
```

### **POST** [`/auth/refresh`](src/main/java/com/dieti/dietiestatesbackend/controller/AuthController.java:103)
Aggiorna il token di accesso utilizzando il token di refresh.

**URL:** `https://api.dietiestates.com/auth/refresh`

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response Body:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "availableRoles": ["USER", "AGENT"]
}
```

### **POST** [`/auth/logout`](src/main/java/com/dieti/dietiestatesbackend/controller/AuthController.java:116)
Effettua il logout dell'utente invalidando il token di refresh.

**URL:** `https://api.dietiestates.com/auth/logout`

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:** `204 No Content`

## 🏠 **PropertiesController** - Endpoint di gestione proprietà

### **POST** [`/properties/search`](src/main/java/com/dieti/dietiestatesbackend/controller/PropertiesController.java:66)
Cerca proprietà con filtri avanzati e geografici.

**URL:** `https://api.dietiestates.com/properties/search`

**Request Body:**
```json
{
  "category": "RESIDENTIAL",
  "contract": "SALE",
  "minPrice": 100000,
  "maxPrice": 300000,
  "minArea": 70,
  "minYearBuilt": 2000,
  "acceptedCondition": ["NEW", "GOOD_CONDITION"],
  "minEnergyRating": "B",
  "centerLatitude": 40.8518,
  "centerLongitude": 14.2681,
  "radiusInMeters": 5000,
  "minNumberOfRooms": 2,
  "minNumberOfBathrooms": 1,
  "minParkingSpaces": 1,
  "heating": "Centralized",
  "acceptedGarden": ["PRIVATE", "ABSENT"],
  "mustBeFurnished": false,
  "mustHaveElevator": true
}
```

**Response Body:**
```json
{
  "content": [
    {
      "id": 123,
      "description": "Appartamento luminoso in centro città",
      "price": 250000,
      "area": 85,
      "yearBuilt": 2015,
      "contract": "SALE",
      "propertyCategory": "Apartment",
      "condition": "GOOD_CONDITION",
      "energyRating": "A2",
      "address": {
        "id": 456,
        "country": "Italy",
        "province": "NA",
        "city": "Napoli",
        "street": "Via Toledo",
        "streetNumber": "15",
        "building": "A",
        "latitude": 40.8518,
        "longitude": 14.2681
      },
      "agent": {
        "id": 789,
        "firstName": "Luigi",
        "lastName": "Bianchi",
        "email": "luigi.bianchi@agenzia.it",
        "agencyId": 1,
        "agencyName": "Dieti Immobiliare"
      },
      "createdAt": "2024-01-15T10:30:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": { "sorted": false }
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "size": 20,
  "number": 0,
  "sort": { "sorted": false },
  "first": true,
  "numberOfElements": 1,
  "empty": false
}
```

### **GET** [`/properties/details/{id}`](src/main/java/com/dieti/dietiestatesbackend/controller/PropertiesController.java:75)
Recupera i dettagli completi di una proprietà specifica.

**URL:** `https://api.dietiestates.com/properties/details/123`

**Response Body (Esempio per Proprietà Residenziale):**
```json
{
  "id": 123,
  "description": "Appartamento luminoso in centro città",
  "price": 250000,
  "area": 85,
  "yearBuilt": 2015,
  "contract": "SALE",
  "propertyCategory": "Apartment",
  "condition": "GOOD_CONDITION",
  "energyRating": "A2",
  "address": {
    "id": 456,
    "country": "Italy",
    "province": "NA",
    "city": "Napoli",
    "street": "Via Toledo",
    "streetNumber": "15",
    "building": "A",
    "latitude": 40.8518,
    "longitude": 14.2681
  },
  "agent": {
    "id": 789,
    "firstName": "Luigi",
    "lastName": "Bianchi",
    "email": "luigi.bianchi@agenzia.it",
    "agencyId": 1,
    "agencyName": "Dieti Immobiliare"
  },
  "createdAt": "2024-01-15T10:30:00",
  "firstImageUrl": "https://dietiestatesstorage.blob.core.windows.net/properties/01H8X.../0.webp",
  "numberOfImages": 3,
  "numberOfRooms": 3,
  "numberOfBathrooms": 2,
  "parkingSpaces": 1,
  "heating": "Autonomous",
  "garden": "ABSENT",
  "floor": 2,
  "numberOfFloors": 4,
  "hasElevator": true,
  "furnished": false
}
```

**Response Body (Esempio per Proprietà Commerciale):**
```json
{
  "id": 456,
  "description": "Ufficio in zona centrale",
  "price": 350000,
  "area": 120,
  "yearBuilt": 2015,
  "contract": "SALE",
  "propertyCategory": "Office",
  "condition": "RENOVATED",
  "energyRating": "B",
  "address": {
    "id": 789,
    "country": "Italy",
    "province": "NA",
    "city": "Napoli",
    "street": "Via Roma",
    "streetNumber": "100",
    "building": null,
    "latitude": 40.8518,
    "longitude": 14.2681
  },
  "agent": {
    "id": 123,
    "firstName": "Mario",
    "lastName": "Rossi",
    "email": "mario.rossi@agenzia.it",
    "agencyId": 1,
    "agencyName": "Dieti Immobiliare"
  },
  "createdAt": "2024-01-20T09:15:00",
  "firstImageUrl": "https://dietiestatesstorage.blob.core.windows.net/properties/01H8Y.../0.webp",
  "numberOfImages": 2,
  "numberOfRooms": 6,
  "floor": 3,
  "numberOfBathrooms": 2,
  "hasDisabledAccess": true,
  "shopWindowCount": 2,
  "numberOfFloors": 5
}
```

**Response Body (Esempio per Garage):**
```json
{
  "id": 789,
  "description": "Box auto con sorveglianza",
  "price": 30000,
  "area": 18,
  "yearBuilt": 2010,
  "contract": "SALE",
  "propertyCategory": "Garage Box",
  "condition": "GOOD_CONDITION",
  "energyRating": "NOT_APPLIABLE",
  "address": {
    "id": 101,
    "country": "Italy",
    "province": "NA",
    "city": "Napoli",
    "street": "Viale Augusto",
    "streetNumber": "75",
    "building": null,
    "latitude": 40.8518,
    "longitude": 14.2681
  },
  "agent": {
    "id": 123,
    "firstName": "Mario",
    "lastName": "Rossi",
    "email": "mario.rossi@agenzia.it",
    "agencyId": 1,
    "agencyName": "Dieti Immobiliare"
  },
  "createdAt": "2024-01-20T09:15:00",
  "firstImageUrl": "https://dietiestatesstorage.blob.core.windows.net/properties/01H8Z.../0.webp",
  "numberOfImages": 1,
  "hasSurveillance": true,
  "numberOfFloors": 1
}
```

**Response Body (Esempio per Terreno):**
```json
{
  "id": 101,
  "description": "Terreno edificabile con vista",
  "price": 180000,
  "area": 5000,
  "yearBuilt": 2024,
  "contract": "SALE",
  "propertyCategory": "Buildable Land",
  "condition": "NEW",
  "energyRating": "NOT_APPLIABLE",
  "address": {
    "id": 123,
    "country": "Italy",
    "province": "NA",
    "city": "Pozzuoli",
    "street": "Via Campi Flegrei",
    "streetNumber": "200",
    "building": null,
    "latitude": 40.8518,
    "longitude": 14.2681
  },
  "agent": {
    "id": 123,
    "firstName": "Mario",
    "lastName": "Rossi",
    "email": "mario.rossi@agenzia.it",
    "agencyId": 1,
    "agencyName": "Dieti Immobiliare"
  },
  "createdAt": "2024-01-20T09:15:00",
  "firstImageUrl": "https://dietiestatesstorage.blob.core.windows.net/properties/01H9A.../0.webp",
  "numberOfImages": 0,
  "hasRoadAccess": true
}
```

### **GET** [`/properties/featured`](src/main/java/com/dieti/dietiestatesbackend/controller/PropertiesController.java:98)
Recupera un elenco di proprietà in evidenza.

**URL:** `https://api.dietiestates.com/properties/featured`

**Response Body (Esempio):**
```json
[
  {
    "id": 123,
    "description": "Appartamento di lusso con vista mare",
    "price": 450000,
    "area": 120,
    "yearBuilt": 2020,
    "contract": "SALE",
    "propertyCategory": "Luxury Apartment",
    "condition": "NEW",
    "energyRating": "A1",
    "address": {
      "id": 456,
      "country": "Italy",
      "province": "NA",
      "city": "Posillipo",
      "street": "Via Posillipo",
      "streetNumber": "25",
      "building": null,
      "latitude": 40.8128,
      "longitude": 14.2121
    },
    "agent": {
      "id": 789,
      "firstName": "Giulia",
      "lastName": "Verdi",
      "email": "giulia.verdi@agenzia.it",
      "agencyId": 1,
      "agencyName": "Dieti Immobiliare"
    },
    "createdAt": [2025,10,29,12,4,3,788641000],
    "updatedAt": [2025,10,29,12,4,3,788641000],
    "firstImageUrl": "https://dietiestatesstorage.blob.core.windows.net/properties/01H8X.../0.webp",
    "numberOfImages": 3,
    "numberOfRooms": 5,
    "numberOfBathrooms": 3,
    "parkingSpaces": 2,
    "heating": "Autonomous",
    "garden": "PRIVATE",
    "floor": 2,
    "numberOfFloors": 5,
    "hasElevator": true,
    "furnished": true
  }
]
```
**Nota:** I campi `agent.agencyId` e `agent.agencyName` possono essere `null` nella risposta.

### **POST** [`/properties`](src/main/java/com/dieti/dietiestatesbackend/controller/PropertiesController.java:110)
Crea una nuova proprietà. Supporta la creazione polimorfica di diversi tipi di proprietà.

**URL:** `https://api.dietiestates.com/properties`

#### Sottotipo: [`CreateResidentialPropertyRequest`](src/main/java/com/dieti/dietiestatesbackend/dto/request/CreateResidentialPropertyRequest.java)
Crea una nuova proprietà residenziale.

**Request Body:**
```json
{
  "propertyType": "RESIDENTIAL",
  "agentUsername": "mariorossi",
  "description": "Appartamento moderno con terrazza",
  "price": 280000,
  "area": 90,
  "yearBuilt": 2018,
  "contractType": "SALE",
  "propertyCategoryName": "Apartment",
  "condition": "GOOD_CONDITION",
  "energyRating": "A2",
  "addressRequest": {
    "country": "Italy",
    "province": "NA",
    "city": "Napoli",
    "street": "Corso Umberto I",
    "streetNumber": "45",
    "building": "B"
  },

  "numberOfRooms": 3,
  "numberOfBathrooms": 2,
  "parkingSpaces": 1,
  "heatingType": "Autonomous",
  "garden": "ABSENT",
  "floor": 2,
  "numberOfFloors": 4,
  "hasElevator": true,
  "isFurnished": false
}
```

#### Sottotipo: [`CreateCommercialPropertyRequest`](src/main/java/com/dieti/dietiestatesbackend/dto/request/CreateCommercialPropertyRequest.java)
Crea una nuova proprietà commerciale.

**Request Body:**
```json
{
  "propertyType": "COMMERCIAL",
  "agentUsername": "mariorossi",
  "description": "Ufficio in zona centrale",
  "price": 350000,
  "area": 120,
  "yearBuilt": 2015,
  "contractType": "SALE",
  "propertyCategoryName": "Office",
  "condition": "RENOVATED",
  "energyRating": "B",
  "addressRequest": {
    "country": "Italy",
    "province": "NA",
    "city": "Napoli",
    "street": "Via Roma",
    "streetNumber": "100",
    "building": null
  },

  "numberOfRooms": 6,
  "floor": 3,
  "numberOfBathrooms": 2,
  "hasDisabledAccess": true,
  "shopWindowCount": 2,
  "numberOfFloors": 5
}
```

#### Sottotipo: [`CreateGaragePropertyRequest`](src/main/java/com/dieti/dietiestatesbackend/dto/request/CreateGaragePropertyRequest.java)
Crea una nuova proprietà di tipo garage.

**Request Body:**
```json
{
  "propertyType": "GARAGE",
  "agentUsername": "mariorossi",
  "description": "Box auto con sorveglianza",
  "price": 30000,
  "area": 18,
  "yearBuilt": 2010,
  "contractType": "SALE",
  "propertyCategoryName": "Garage Box",
  "condition": "GOOD_CONDITION",
  "energyRating": "NOT_APPLIABLE",
  "addressRequest": {
    "country": "Italy",
    "province": "NA",
    "city": "Napoli",
    "street": "Viale Augusto",
    "streetNumber": "75",
    "building": null
  },

  "hasSurveillance": true,
  "numberOfFloors": 1
}
```

#### Sottotipo: [`CreateLandPropertyRequest`](src/main/java/com/dieti/dietiestatesbackend/dto/request/CreateLandPropertyRequest.java)
Crea una nuova proprietà di tipo terreno.

**Request Body:**
```json
{
  "propertyType": "LAND",
  "agentUsername": "mariorossi",
  "description": "Terreno edificabile con vista",
  "price": 180000,
  "area": 5000,
  "yearBuilt": 2024,
  "contractType": "SALE",
  "propertyCategoryName": "Buildable Land",
  "condition": "NEW",
  "energyRating": "NOT_APPLIABLE",
  "addressRequest": {
    "country": "Italy",
    "province": "NA",
    "city": "Pozzuoli",
    "street": "Via Campi Flegrei",
    "streetNumber": "200",
    "building": null
  },

  "hasRoadAccess": true
}
```

#### **Caricamento delle Immagini (multipart/form-data)**

Le immagini per le proprietà devono essere caricate utilizzando un **`multipart/form-data`** separato dalla richiesta JSON principale. Ogni file immagine deve essere in formato **WebP**.

**Dettagli del Form-Data:**
-   **Tipo di Contenuto:** `multipart/form-data`
-   **Nome del Campo:** `images`
-   **Formato File:** Solo file `.webp` sono accettati

**Esempio di cURL:**
```bash
curl -X POST "https://api.dietiestates.com/properties" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d @property_data.json \
  -F "images=@/path/to/image1.webp" \
  -F "images=@/path/to/image2.webp" \
  -F "images=@/path/to/image3.webp"
```

**Note:**
-   È possibile caricare più immagini specificando più campi `images`.
-   I campi `images` nel form-data sono indipendenti dal payload JSON della proprietà.
-   Il server eseguirà automaticamente il geocoding dell'indirizzo fornito nel payload JSON.

**Response Body per tutti i sottotipi:**
```json
{
  "id": 456,
  "description": "Appartamento moderno con terrazza",
  "price": 280000,
  "area": 90,
  "yearBuilt": 2018,
  "contract": "SALE",
  "propertyCategory": "Apartment",
  "condition": "GOOD_CONDITION",
  "energyRating": "A2",
  "address": {
    "id": 789,
    "country": "Italy",
    "province": "NA",
    "city": "Napoli",
    "street": "Corso Umberto I",
    "streetNumber": "45",
    "building": "B",
    "latitude": 40.8518,
    "longitude": 14.2681
  },
  "agent": {
    "id": 123,
    "firstName": "Mario",
    "lastName": "Rossi",
    "email": "mario.rossi@agenzia.it",
    "agencyId": 1,
    "agencyName": "Dieti Immobiliare"
  },
  "createdAt": "2024-01-20T09:15:00"
}
```

### **GET** [`/api/property-types`](src/main/java/com/dieti/dietiestatesbackend/controller/PropertiesController.java:125)
Recupera un elenco di tutti i tipi di proprietà disponibili.

**URL:** `https://api.dietiestates.com/api/property-types`

**Response Body:**
```json
[
  "RESIDENTIAL",
  "COMMERCIAL",
  "GARAGE",
  "LAND"
]
```

### **GET** [`/api/categories`](src/main/java/com/dieti/dietiestatesbackend/controller/PropertiesController.java:138)
Recupera un elenco di tutte le categorie di proprietà disponibili.

**URL:** `https://api.dietiestates.com/api/categories`

**Response Body:**
```json
[
  "Apartment",
  "Villa",
  "Office",
  "Shop",
  "Garage Box",
  "Buildable Land",
  "Agricultural Land"
]
```

### **GET** [`/api/properties/{id}/places`](src/main/java/com/dieti/dietiestatesbackend/controller/PropertiesController.java:158)
Recupera i luoghi di interesse vicini a una proprietà specifica.

**URL:** `https://api.dietiestates.com/api/properties/123/places`

**Response Body:**
```json
[
  {
    "name": "Supermercato Conad",
    "category": "supermarket",
    "distance": 250,
    "latitude": 40.8520,
    "longitude": 14.2685
  },
  {
    "name": "Scuola Elementare",
    "category": "education",
    "distance": 500,
    "latitude": 40.8510,
    "longitude": 14.2675
  },
  {
    "name": "Fermata Metro Toledo",
    "category": "transport",
    "distance": 300,
    "latitude": 40.8515,
    "longitude": 14.2680
  }
]
```
### **GET** [`/api/properties/agent_properties/{agentID}`](src/main/java/com/dieti/dietiestatesbackend/controller/PropertiesController.java:155)
Recupera le proprietà associate a un agente specifico.

**URL:** `https://api.dietiestates.com/api/properties/agent_properties/789`

**Response Body:**
```json
{
  "content": [
    {
      "id": 123,
      "description": "Appartamento luminoso in centro città",
      "price": 250000,
      "area": 85,
      "yearBuilt": 2015,
      "contract": "SALE",
      "propertyCategory": "Apartment",
      "condition": "GOOD_CONDITION",
      "energyRating": "A2",
      "address": {
        "id": 456,
        "country": "Italy",
        "province": "NA",
        "city": "Napoli",
        "street": "Via Toledo",
        "streetNumber": "15",
        "building": "A",
        "latitude": 40.8518,
        "longitude": 14.2681
      },
      "agent": {
        "id": 789,
        "firstName": "Luigi",
        "lastName": "Bianchi",
        "email": "luigi.bianchi@agenzia.it",
        "agencyId": 1,
        "agencyName": "Dieti Immobiliare"
      },
      "createdAt": "2024-01-15T10:30:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": { "sorted": false }
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "size": 20,
  "number": 0,
  "sort": { "sorted": false },
  "first": true,
  "numberOfElements": 1,
  "empty": false
}
```

### **DELETE** [`/properties/{id}`](src/main/java/com/dieti/dietiestatesbackend/controller/PropertiesController.java:163)
Elimina una proprietà specifica. Richiede autorizzazioni di manager o agente proprietario della proprietà.

**URL:** `https://api.dietiestates.com/properties/123`

**Response:** `204 No Content`

**Response Body (Errore):**
*   `404 Not Found`: se la proprietà non esiste.
*   `403 Forbidden`: se l'utente non ha le autorizzazioni per eliminare la proprietà.

### **POST** [`/api/properties/history`](src/main/java/com/dieti/dietiestatesbackend/controller/PropertiesController.java:181)
Recupera i dettagli completi per una lista di immobili, identificati dai loro ID.

**URL:** `https://api.dietiestates.com/api/properties/history`

**Request Body:**
```json
{
  "propertyIds": ["id1", "id2", "id3"] // @Size(max=100)
}
```

**Response Body (Successo):**
`200 OK` con un array di oggetti [`PropertyResponse`](src/main/java/com/dieti/dietiestatesbackend/dto/response/PropertyResponse.java).
```json
[
  {
    "id": 123,
    "description": "Appartamento luminoso in centro città",
    "price": 250000,
    "area": 85,
    "yearBuilt": 2015,
    "contract": "SALE",
    "propertyCategory": "Apartment",
    "condition": "GOOD_CONDITION",
    "energyRating": "A2",
    "address": {
      "id": 456,
      "country": "Italy",
      "province": "NA",
      "city": "Napoli",
      "street": "Via Toledo",
      "streetNumber": "15",
      "building": "A",
      "latitude": 40.8518,
      "longitude": 14.2681
    },
    "agent": {
      "id": 789,
      "firstName": "Luigi",
      "lastName": "Bianchi",
      "email": "luigi.bianchi@agenzia.it",
      "agencyId": 1,
      "agencyName": "Dieti Immobiliare"
    },
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

**Response Body (Errore):**
*   `400 Bad Request`: per richieste non valide (es. `propertyIds` mancante, non è un array, o supera la dimensione massima di 100 elementi).
*   `400 Bad Request`: se uno o più `propertyIds` non sono numeri validi (`NumberFormatException`).
*   `500 Internal Server Error`: per altri errori non gestiti.


## 🖼️ Gestione delle Foto nelle Risposte delle Proprietà

Le risposte degli endpoint che restituiscono i dettagli delle proprietà (come [`GET /properties/details/{id}`](src/main/java/com/dieti/dietiestatesbackend/controller/PropertiesController.java:75), [`POST /properties/search`](src/main/java/com/dieti/dietiestatesbackend/controller/PropertiesController.java:66) e [`POST /api/properties/history`](src/main/java/com/dieti/dietiestatesbackend/controller/PropertiesController.java:202)) includono informazioni sulle immagini associate alla proprietà, ma non un array completo di URL.

**Struttura JSON delle Immagini:**

Nel DTO di risposta [`PropertyResponse`](src/main/java/com/dieti/dietiestatesbackend/dto/response/PropertyResponse.java), sono presenti i seguenti campi relativi alle immagini:

*   `firstImageUrl`: Una stringa che rappresenta l'URL diretto della prima immagine della proprietà. Questo URL contiene l'ULID (Identificatore Univoco Lessicograficamente Ordinabile) della proprietà, che funge da prefisso per tutte le immagini della proprietà.
*   `numberOfImages`: Un intero che indica il numero totale di immagini disponibili per la proprietà.

Esempio di risposta con i campi immagine:

```json
{
  "id": 123,
  "description": "Appartamento luminoso in centro città",
  // ... altri campi della proprietà ...
  "firstImageUrl": "https://dietiestatesstorage.blob.core.windows.net/properties/01H8X.../0.webp",
  "numberOfImages": 3
}
```

*   **URL Completo:** Il `firstImageUrl` è un URL diretto e completo alla prima immagine. Le altre immagini non sono esplicitamente elencate, ma possono essere costruite dal client.
*   **Limiti:** Non ci sono limiti specifici sul numero di foto o sulla loro dimensione imposti dall'API nel JSON di risposta, sebbene limiti possano essere applicati a livello di storage o di caricamento iniziale.

**Gestione delle Immagini:**

Le immagini sono gestite tramite un servizio di storage esterno, nello specifico **Azure Blob Storage**. Gli URL forniti (`firstImageUrl`) sono URL diretti e pubblici per accedere alle immagini. Ciò significa che il client può utilizzare questi URL direttamente per visualizzare le immagini senza necessità di autenticazione aggiuntiva o di pass-through tramite il backend di DietiEstates.

---

## 💻 Indicazioni per l'Iterazione e Visualizzazione dei Dettagli delle Proprietà Lato Client

Questa sezione fornisce linee guida per i client (frontend) su come iterare e visualizzare i dettagli delle proprietà, con un focus particolare sulla gestione delle immagini e dei diversi tipi di proprietà.

### Visualizzazione delle Foto

Dato che l'API fornisce `firstImageUrl` e `numberOfImages`, il client deve costruire gli URL per le immagini aggiuntive. Il formato degli URL per le immagini è `[base_url]/[ULID_della_proprietà]/[indice].webp`.
L'ULID della proprietà può essere estratto dal `firstImageUrl`.

Esempio di costruzione degli URL delle immagini:

Se `firstImageUrl` è `https://dietiestatesstorage.blob.core.windows.net/properties/01H8X.../0.webp` e `numberOfImages` è 3:
1.  Estrarre l'ULID: `01H8X...` (questa è la parte dell'URL che identifica univocamente la proprietà all'interno del blob storage).
2.  Costruire gli URL:
    *   `https://dietiestatesstorage.blob.core.windows.net/properties/01H8X.../0.webp` (già fornito)
    *   `https://dietiestatesstorage.blob.core.windows.net/properties/01H8X.../1.webp`
    *   `https://dietiestatesstorage.blob.core.windows.net/properties/01H8X.../2.webp`

Per una visualizzazione efficiente e performante delle immagini lato client, si raccomandano le seguenti best practice:

*   **Lazy Loading:** Implementare il lazy loading per le immagini. Questo assicura che le immagini vengano caricate solo quando entrano nel viewport dell'utente, migliorando i tempi di caricamento iniziali e riducendo il consumo di banda.
*   **Thumbnail/Preview:** Se la UI lo richiede, considerare la generazione di thumbnail o immagini di preview lato client (se non disponibili direttamente dall'API) per le gallerie o le liste di proprietà. Questo può essere fatto ridimensionando le immagini originali o utilizzando tecniche CSS/JS per visualizzare una versione a bassa risoluzione prima del caricamento completo.
*   **Gestione degli Errori:** Implementare una gestione robusta degli errori per le immagini. Se un URL dell'immagine non è valido o l'immagine non può essere caricata, mostrare un'immagine placeholder o un messaggio di errore significativo all'utente.
*   **Ottimizzazione della Larghezza di Banda:** Se la larghezza di banda è una preoccupazione, considerare l'utilizzo di attributi `srcset` o elementi `<picture>` in HTML per servire immagini di diverse risoluzioni a seconda delle dimensioni dello schermo del dispositivo.

### Gestione dei Tipi di Proprietà e Campi Specifici

Le proprietà possono essere di diversi tipi (residenziale, commerciale, garage, terreno), ognuno con campi specifici nel JSON di risposta. Il client deve essere in grado di interpretare la struttura della risposta in base al tipo di proprietà per visualizzare correttamente tutti i dettagli pertinenti.

*   **Identificazione del Tipo:** Il campo `propertyCategory` (o un campo simile che indica il tipo generale, es. `propertyType` in fase di creazione) nella risposta JSON può essere utilizzato per determinare il tipo di proprietà. Ad esempio, una `propertyCategory` di "Apartment" o "Villa" indicherà una proprietà residenziale, mentre "Office" o "Shop" indicherà una commerciale.

*   **Rendering Condizionale:** Il frontend dovrebbe implementare un rendering condizionale dell'interfaccia utente basato sul tipo di proprietà. Ogni tipo di proprietà estende una `PropertyResponse` di base e aggiunge campi specifici.

*   **Struttura dei Dati per Tipo di Proprietà:**

    *   **[`ResidentialPropertyResponse`](src/main/java/com/dieti/dietiestatesbackend/dto/response/ResidentialPropertyResponse.java):**
        Estende i campi di base di una proprietà e include:
        -   `numberOfRooms`: Numero di stanze.
        -   `numberOfBathrooms`: Numero di bagni.
        -   `parkingSpaces`: Numero di posti auto.
        -   `heating`: Tipo di riscaldamento (es. "Autonomous", "Centralized").
        -   `garden`: Presenza e tipo di giardino (es. "PRIVATE", "COMMUNAL", "ABSENT").
        -   `floor`: Piano dell'immobile.
        -   `numberOfFloors`: Numero totale di piani dell'edificio.
        -   `hasElevator`: Booleano, indica se l'edificio ha un ascensore.
        -   `furnished`: Booleano, indica se l'immobile è arredato.

    *   **[`CommercialPropertyResponse`](src/main/java/com/dieti/dietiestatesbackend/dto/response/CommercialPropertyResponse.java):**
        Estende i campi di base di una proprietà e include:
        -   `numberOfRooms`: Numero di stanze/ambienti.
        -   `numberOfBathrooms`: Numero di bagni.
        -   `hasDisabledAccess`: Booleano, indica se ha accesso per disabili.
        -   `shopWindowCount`: Numero di vetrine (se applicabile).
        -   `floor`: Piano dell'immobile.
        -   `numberOfFloors`: Numero totale di piani dell'edificio.

    *   **[`GarageResponse`](src/main/java/com/dieti/dietiestatesbackend/dto/response/GarageResponse.java):**
        Estende i campi di base di una proprietà e include:
        -   `hasSurveillance`: Booleano, indica se il garage ha sorveglianza.
        -   `numberOfFloors`: Numero totale di piani del garage.

    *   **[`LandResponse`](src/main/java/com/dieti/dietiestatesbackend/dto/response/LandResponse.java):**
        Estende i campi di base di una proprietà e include:
        -   `hasRoadAccess`: Booleano, indica se il terreno ha accesso stradale.

Assicurarsi di leggere attentamente la struttura JSON per ogni tipo di proprietà per visualizzare correttamente tutti i dettagli pertinenti.

---

## 👤 **UserController** - Endpoint utenti

### **GET** [`/agent/info/{id}`](src/main/java/com/dieti/dietiestatesbackend/controller/UserController.java:41)
Recupera le informazioni di un agente specifico.

**URL:** `https://api.dietiestates.com/agent/info/789`

**Response Body:**
```json
{
  "fullName": "Luigi Bianchi",
  "email": "luigi.bianchi@agenzia.it"
}
```

### **POST** [`/agent/create`](src/main/java/com/dieti/dietiestatesbackend/controller/UserController.java:58)
Crea un nuovo agente. Solo i manager possono eseguire questa operazione. Se l'utente esiste già, gli viene assegnato il ruolo di agente.

**URL:** `https://api.dietiestates.com/agent/create`

**Request Body:**
```json
{
  "email": "nuovo.agente@email.com",
  "username": "nuovoagente",
  "password": "Password123!",
  "name": "Nome",
  "surname": "Cognome"
}
```

**Response Body (Nuovo agente creato):**
`201 Created` con un messaggio di successo.
```
Agent created
```

**Response Body (Ruolo agente aggiunto a utente esistente):**
`200 OK` con un messaggio di successo.
```
Agent role added to existing user
```

**Response Body (Errore):**
`500 Internal Server Error` in caso di errore durante la creazione o l'assegnazione del ruolo.
```
Errore durante la creazione dell'agente: Messaggio di errore
```

### **POST** [`/manager/create`](src/main/java/com/dieti/dietiestatesbackend/controller/UserController.java:83)
Crea un nuovo manager. Solo i manager possono eseguire questa operazione. Se l'utente esiste già, gli viene assegnato il ruolo di manager.

**URL:** `https://api.dietiestates.com/manager/create`

**Request Body:**
```json
{
  "email": "nuovo.manager@email.com",
  "username": "nuovomanager",
  "password": "Password123!",
  "name": "Nome",
  "surname": "Cognome"
}
```

**Response Body (Nuovo manager creato):**
`201 Created` con un messaggio di successo.
```
Manager created
```

**Response Body (Ruolo manager aggiunto a utente esistente):**
`200 OK` con un messaggio di successo.
```
Manager role added to existing user
```

**Response Body (Errore):**
`500 Internal Server Error` in caso di errore durante la creazione o l'assegnazione del ruolo.
```
Errore durante la creazione del manager: Messaggio di errore
```

### **POST** [`/manager/change_password`](src/main/java/com/dieti/dietiestatesbackend/controller/UserController.java:108)
Cambia la password di un utente. Solo i manager possono eseguire questa operazione per altri utenti.

**URL:** `https://api.dietiestates.com/manager/change_password`

**Request Body:**
```json
{
  "email": "utente.da.modificare@email.com",
  "oldPassword": "VecchiaPassword123!",
  "newPassword": "NuovaPassword456!"
}
```

**Response Body (Successo):**
`200 OK` con un messaggio di successo.
```
Password cambiata con successo
```

**Response Body (Errore):**
*   `404 Not Found`: se l'utente non viene trovato.
    ```
    Utente non trovato
    ```
*   `403 Forbidden`: se l'utente che tenta di cambiare la password non è un manager.
    ```
    Solo i manager possono cambiare la password
    ```
*   `500 Internal Server Error`: per altri errori durante il cambio password.
    ```
    Errore durante il cambio della password
    ```

## 📍 **AddressController** - Endpoint indirizzi

### **GET** [`/address/{id}`](src/main/java/com/dieti/dietiestatesbackend/controller/AddressController.java:25)
Recupera i dettagli di un indirizzo specifico.

**URL:** `https://api.dietiestates.com/address/456`

**Response Body:**
```json
{
  "id": 456,
  "country": "Italy",
  "province": "NA",
  "city": "Napoli",
  "street": "Via Toledo",
  "streetNumber": "15",
  "building": "A",
  "latitude": 40.8518,
  "longitude": 14.2681,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

## 📋 **ContractController** - Endpoint contratti

### **GET** [`/contracts`](src/main/java/com/dieti/dietiestatesbackend/controller/ContractController.java:20)
Recupera un elenco di tutti i contratti disponibili.

**URL:** `https://api.dietiestates.com/contracts`

**Response Body:**
```json
[
  {
    "id": 1,
    "name": "SALE",
    "isActive": true,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  },
  {
    "id": 2,
    "name": "RENT",
    "isActive": true,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
]
```

## 💼 **OfferController** - Endpoint offerte
 
### **GET** [`/offers/agent_offers/{id}`](src/main/java/com/dieti/dietiestatesbackend/controller/OfferController.java:29)
 Recupera le offerte associate a un agente specifico.
 
 **Autorizzazione:** L'utente autenticato (principal) deve avere le autorizzazioni adeguate per visualizzare le entità correlate all'agente specificato da `{agentID}`.
 
 **URL:** `https://api.dietiestates.com/offers/agent_offers/789`
 
 **Response Body:**
 ```json
 {
   "content": [
     {
       "id": 1,
       "offerAmount": 100000.0,
       "offerDate": "2024-01-01T10:00:00Z",
       "status": "PENDING",
       "propertyId": 123,
       "propertyName": "Appartamento in centro",
       "offerorId": 456,
       "offerorName": "Mario Rossi"
     }
   ],
   "pageable": {
     "pageNumber": 0,
     "pageSize": 20,
     "sort": { "sorted": false }
   },
   "totalElements": 1,
   "totalPages": 1,
   "last": true,
   "size": 20,
   "number": 0,
   "sort": { "sorted": false },
   "first": true,
   "numberOfElements": 1,
   "empty": false
 }
 ```
 
 ### **POST** [`/offers/create`](src/main/java/com/dieti/dietiestatesbackend/controller/OfferController.java:39)
Crea una nuova offerta per una proprietà.

**URL:** `https://api.dietiestates.com/offers/create`

**Request Body:**
```json
{
  "propertyId": 123,
  "offerAmount": 150000.0
}
```

**Response Body:**
```json
{
  "id": 2,
  "offerAmount": 150000.0,
  "offerDate": "2024-01-02T11:00:00Z",
  "status": "PENDING",
  "propertyId": 123,
  "propertyName": "Appartamento in centro",
  "offerorId": 789,
  "offerorName": "Giuseppe Verdi"
}
```

### **POST** [`/offers/withdraw/{propertyID}`](src/main/java/com/dieti/dietiestatesbackend/controller/OfferController.java:46)
Ritira un'offerta precedentemente fatta su una proprietà.

**URL:** `https://api.dietiestates.com/offers/withdraw/123`

**Response Body:**
```json
{
  "id": 2,
  "offerAmount": 150000.0,
  "offerDate": "2024-01-02T11:00:00Z",
  "status": "WITHDRAWN",
  "propertyId": 123,
  "propertyName": "Appartamento in centro",
  "offerorId": 789,
  "offerorName": "Giuseppe Verdi"
}
```

### **POST** [`/offers/accept/{offerID}`](src/main/java/com/dieti/dietiestatesbackend/controller/OfferController.java:53)
Accetta un'offerta specifica. Solo l'agente proprietario della proprietà può accettare l'offerta.

**URL:** `https://api.dietiestates.com/offers/accept/2`

**Response Body:**
```json
{
  "id": 2,
  "offerAmount": 150000.0,
  "offerDate": "2024-01-02T11:00:00Z",
  "status": "ACCEPTED",
  "propertyId": 123,
  "propertyName": "Appartamento in centro",
  "offerorId": 789,
  "offerorName": "Giuseppe Verdi"
}
```

### **POST** [`/offers/reject/{offerID}`](src/main/java/com/dieti/dietiestatesbackend/controller/OfferController.java:60)
Rifiuta un'offerta specifica. Solo l'agente proprietario della proprietà può rifiutare l'offerta.

**URL:** `https://api.dietiestates.com/offers/reject/2`

**Response Body:**
```json
{
  "id": 2,
  "offerAmount": 150000.0,
  "offerDate": "2024-01-02T11:00:00Z",
  "status": "REJECTED",
  "propertyId": 123,
  "propertyName": "Appartamento in centro",
  "offerorId": 789,
  "offerorName": "Giuseppe Verdi"
}
```

### **POST** [`/offers/counter/{offerID}`](src/main/java/com/dieti/dietiestatesbackend/controller/OfferController.java:67)
Effettua una controfferta su un'offerta esistente. Solo l'agente proprietario della proprietà può fare una controfferta.

**URL:** `https://api.dietiestates.com/offers/counter/2`

**Request Body:**
```json
160000.0
```

**Response Body:**
```json
{
  "id": 2,
  "offerAmount": 160000.0,
  "offerDate": "2024-01-02T11:00:00Z",
  "status": "COUNTERED",
  "propertyId": 123,
  "propertyName": "Appartamento in centro",
  "offerorId": 789,
  "offerorName": "Giuseppe Verdi"
}
```

## 🗓️ **AgentAvailabilityController** - Endpoint di gestione disponibilità agenti

### **POST** [`/agent-availabilities`](src/main/java/com/dieti/dietiestatesbackend/controller/AgentAvailabilityController.java:50)
Crea un nuovo slot di disponibilità per un agente.

**URL:** `https://api.dietiestates.com/agent-availabilities`

**Request Body:**
```json
{
  "agentId": 1,
  "startTime": "2025-01-01T09:00:00Z",
  "endTime": "2025-01-01T10:00:00Z"
}
```

**Response Body:**
```json
{
  "id": 1,
  "agentId": 1,
  "startTime": "2025-01-01T09:00:00Z",
  "endTime": "2025-01-01T10:00:00Z"
}
```

### **GET** [`/agents/{agentId}/availabilities`](src/main/java/com/dieti/dietiestatesbackend/controller/AgentAvailabilityController.java:62)
Recupera tutti gli slot di disponibilità per un agente specifico.

**URL:** `https://api.dietiestates.com/agents/1/availabilities`

**Response Body:**
```json
[
  {
    "id": 1,
    "agentId": 1,
    "startTime": "2025-01-01T09:00:00Z",
    "endTime": "2025-01-01T10:00:00Z"
  },
  {
    "id": 2,
    "agentId": 1,
    "startTime": "2025-01-01T10:00:00Z",
    "endTime": "2025-01-01T11:00:00Z"
  }
]
```

### **GET** [`/agent-availabilities/{id}`](src/main/java/com/dieti/dietiestatesbackend/controller/AgentAvailabilityController.java:74)
Recupera uno slot di disponibilità specifico tramite ID.

**URL:** `https://api.dietiestates.com/agent-availabilities/1`

**Response Body:**
```json
{
  "id": 1,
  "agentId": 1,
  "startTime": "2025-01-01T09:00:00Z",
  "endTime": "2025-01-01T10:00:00Z"
}
```

### **DELETE** [`/agent-availabilities/{id}`](src/main/java/com/dieti/dietiestatesbackend/controller/AgentAvailabilityController.java:87)
Elimina uno slot di disponibilità specifico tramite ID.

**URL:** `https://api.dietiestates.com/agent-availabilities/1`

**Response:** `204 No Content`

## 📅 **VisitController** - Endpoint di gestione visite

### **GET** [`/visits/agent/{agentID}`](src/main/java/com/dieti/dietiestatesbackend/controller/VisitController.java:33)
Recupera tutte le visite pianificate per un agente specifico.

**URL:** `https://api.dietiestates.com/visits/agent/1`

**Response Body:**
```json
{
  "content": [
    {
      "id": 1,
      "propertyId": 101,
      "propertyName": "Appartamento in Via Roma",
      "agentId": 1,
      "agentName": "Mario Rossi",
      "clientId": 2,
      "clientName": "Luigi Bianchi",
      "visitTime": "2025-01-01T10:00:00Z",
      "status": "SCHEDULED"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": { "sorted": false }
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "size": 20,
  "number": 0,
  "sort": { "sorted": false },
  "first": true,
  "numberOfElements": 1,
  "empty": false
}
```

### **GET** [`/visits/me/`](src/main/java/com/dieti/dietiestatesbackend/controller/VisitController.java:41)
Recupera tutte le visite pianificate per l'utente autenticato.

**URL:** `https://api.dietiestates.com/visits/me/`

**Response Body:**
```json
{
  "content": [
    {
      "id": 1,
      "propertyId": 101,
      "propertyName": "Appartamento in Via Roma",
      "agentId": 1,
      "agentName": "Mario Rossi",
      "clientId": 2,
      "clientName": "Luigi Bianchi",
      "visitTime": "2025-01-01T10:00:00Z",
      "status": "SCHEDULED"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": { "sorted": false }
  },
  "totalElements": 1,
  "totalPages": 1,
    "last": true,
  "size": 20,
  "number": 0,
  "sort": { "sorted": false },
  "first": true,
  "numberOfElements": 1,
  "empty": false
}
```

### **GET** [`/properties/{propertyId}/visits`](src/main/java/com/dieti/dietiestatesbackend/controller/VisitController.java:47)
Recupera tutte le visite pianificate per una proprietà specifica.

**URL:** `https://api.dietiestates.com/properties/101/visits`

**Response Body:**
```json
{
  "content": [
    {
      "id": 1,
      "propertyId": 101,
      "propertyName": "Appartamento in Via Roma",
      "agentId": 1,
      "agentName": "Mario Rossi",
      "clientId": 2,
      "clientName": "Luigi Bianchi",
      "visitTime": "2025-01-01T10:00:00Z",
      "status": "SCHEDULED"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": { "sorted": false }
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "size": 20,
  "number": 0,
  "sort": { "sorted": false },
  "first": true,
  "numberOfElements": 1,
  "empty": false
}
```

### **POST** [`/visits`](src/main/java/com/dieti/dietiestatesbackend/controller/VisitController.java:54)
Crea una nuova visita.

**URL:** `https://api.dietiestates.com/visits`

**Request Body:**
```json
{
  "propertyId": 101,
  "agentId": 1,
  "visitTime": "2025-01-01T14:00:00Z"
}
```

**Response Body:**
```json
{
  "id": 2,
  "propertyId": 101,
  "propertyName": "Appartamento in Via Roma",
  "agentId": 1,
  "agentName": "Mario Rossi",
  "clientId": 2,
  "clientName": "Luigi Bianchi",
  "visitTime": "2025-01-01T14:00:00Z",
  "status": "SCHEDULED"
}
```

### **PUT** [`/visits/{visitId}/status`](src/main/java/com/dieti/dietiestatesbackend/controller/VisitController.java:62)
Aggiorna lo stato di una visita specifica.

**URL:** `https://api.dietiestates.com/visits/2/status`

**Request Body:**
```json
{
  "status": "COMPLETED"
}
```

**Response Body:**
```json
{
  "id": 2,
  "propertyId": 101,
  "propertyName": "Appartamento in Via Roma",
  "agentId": 1,
  "agentName": "Mario Rossi",
  "clientId": 2,
  "clientName": "Luigi Bianchi",
  "visitTime": "2025-01-01T14:00:00Z",
  "status": "COMPLETED"
}
```

### **DELETE** [`/visits/{visitId}`](src/main/java/com/dieti/dietiestatesbackend/controller/VisitController.java:69)
Cancella una visita specifica.

**URL:** `https://api.dietiestates.com/visits/2`

**Response Body:**
```json
{
  "id": 2,
  "propertyId": 101,
  "propertyName": "Appartamento in Via Roma",
  "agentId": 1,
  "agentName": "Mario Rossi",
  "clientId": 2,
  "clientName": "Luigi Bianchi",
  "visitTime": "2025-01-01T14:00:00Z",
  "status": "CANCELLED"
}