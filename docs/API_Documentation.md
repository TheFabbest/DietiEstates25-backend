
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
  "availableRoles": ["USER", "AGENT"]
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
  "availableRoles": ["USER"]
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

**Response Body:**
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
  "createdAt": "2024-01-15T10:30:00"
}
```

### **GET** [`/thumbnails/{id}`](src/main/java/com/dieti/dietiestatesbackend/controller/PropertiesController.java:84)
Recupera l'immagine thumbnail di una proprietà.

**URL:** `https://api.dietiestates.com/thumbnails/123`

**Response:** Immagine JPEG/PNG (binary content)

### **GET** [`/properties/featured`](src/main/java/com/dieti/dietiestatesbackend/controller/PropertiesController.java:98)
Recupera un elenco di proprietà in evidenza.

**URL:** `https://api.dietiestates.com/properties/featured`

**Response Body:**
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
    "createdAt": "2024-01-10T14:20:00"
  }
]
```

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
  "images": ["/img/property1.jpg", "/img/property2.jpg"],
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
  "images": ["/img/office1.jpg"],
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
  "images": ["/img/garage1.jpg"],
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
  "images": ["/img/land1.jpg"],
  "hasRoadAccess": true
}
```

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

## 👤 **UserController** - Endpoint utenti

### **GET** [`/agent/info/{id}`](src/main/java/com/dieti/dietiestatesbackend/controller/UserController.java:28)
Recupera le informazioni di un agente specifico.

**URL:** `https://api.dietiestates.com/agent/info/789`

**Response Body:**
```json
{
  "fullName": "Luigi Bianchi",
  "email": "luigi.bianchi@agenzia.it"
}
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

### **GET** [`/offers/agent_offers/{id}`](src/main/java/com/dieti/dietiestatesbackend/controller/OfferController.java:24)
Recupera le offerte associat