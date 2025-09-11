# Documentazione API DietiEstates25 Backend

## Introduzione

Questa documentazione descrive le API RESTful del backend DietiEstates25 per la gestione delle proprietà immobiliari.

### Autenticazione JWT

Tutte le API richiedono autenticazione tramite JWT (JSON Web Token), tranne gli endpoint pubblici specificati nella configurazione di sicurezza. Il token JWT deve essere incluso nell'header `Authorization` con il formato `Bearer <token>`.

**Endpoint pubblici (non richiedono autenticazione):**
- `/auth/**` - Autenticazione e registrazione
- `/oauth2/**` - OAuth2 integration
- `/api/property-types` - Lista tipi di proprietà
- `/api/categories` - Lista categorie per tipo
- Documentazione Swagger

### Configurazione Credenziali

Il sistema richiede le seguenti configurazioni:

- **JWT Secret Key**: Chiave segreta per la firma dei token JWT
- **Geoapify API Key**: Chiave API per il servizio di geocoding e ricerca luoghi
- **Configurazione nel file `application.properties`**:
```properties
jwt.secret=your-jwt-secret-key
geocoding.provider.geoapify.api-key=your-geoapify-api-key
```

### Geocoding Automatico

Il sistema esegue automaticamente il geocoding degli indirizzi utilizzando il servizio Geoapify. Quando viene fornito un indirizzo tramite `addressRequest`, le coordinate geografiche (latitudine e longitudine) vengono calcolate automaticamente e associate alla proprietà.

## Endpoint API

### 1. Ricerca Proprietà

**Descrizione**: Ricerca proprietà con filtri avanzati e ricerca geografica.

**Metodo**: `POST`

**Percorso**: `/properties/search`

**Autenticazione**: Richiesta (JWT)

**Parametri di richiesta**:
```json
{
  "category": "RESIDENTIAL|COMMERCIAL|LAND|GARAGE",
  "contract": "SALE|RENT",
  "minPrice": 0.01,
  "maxPrice": 1000000.00,
  "minArea": 1,
  "minYearBuilt": 1900,
  "acceptedCondition": ["NEW", "GOOD_CONDITION", "RENOVATED", "TO_BE_RENOVATED", "POOR_CONDITION", "UNDER_CONSTRUCTION"],
  "minEnergyRating": "A|B|C|D|E|F|G",
  "centerLatitude": 41.8902,
  "centerLongitude": 12.4922,
  "radiusInMeters": 5000,
  "minNumberOfFloors": 1,
  "minNumberOfRooms": 1,
  "minNumberOfBathrooms": 1,
  "minParkingSpaces": 1,
  "heating": "string",
  "acceptedGarden": ["NO", "SMALL", "MEDIUM", "LARGE"],
  "mustBeFurnished": true,
  "mustHaveElevator": true,
  "mustHaveWheelchairAccess": true,
  "minNumeroVetrine": 1,
  "mustHaveSurveillance": true,
  "mustBeAccessibleFromStreet": true
}
```

**Campi obbligatori per ricerca geografica**:
- `centerLatitude` (BigDecimal): Latitudine del centro di ricerca
- `centerLongitude` (BigDecimal): Longitudine del centro di ricerca  
- `radiusInMeters` (Double): Raggio di ricerca in metri (≥ 0)

**Esempio richiesta**:
```json
{
  "category": "RESIDENTIAL",
  "contract": "SALE",
  "minPrice": 100000,
  "maxPrice": 500000,
  "minArea": 80,
  "centerLatitude": 41.9028,
  "centerLongitude": 12.4964,
  "radiusInMeters": 3000,
  "minNumberOfRooms": 3,
  "minNumberOfBathrooms": 2
}
```

**Esempio risposta** (paginated):
```json
{
  "content": [
    {
      "id": 123,
      "description": "Appartamento in centro",
      "price": 350000.00,
      "area": 95,
      "yearBuilt": 2010,
      "contract": "SALE",
      "propertyCategory": "Appartamento",
      "condition": "GOOD_CONDITION",
      "energyRating": "B",
      "address": {
        "country": "IT",
        "province": "RM",
        "city": "Roma",
        "street": "Via Appia",
        "civic": "123",
        "building": "A",
        "latitude": 41.9028,
        "longitude": 12.4964
      },
      "agent": {
        "id": 456,
        "name": "Mario Rossi",
        "email": "mario.rossi@agency.com",
        "phone": "+39 1234567890"
      },
      "createdAt": "2024-01-15T10:30:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": false,
      "unsorted": true,
      "empty": true
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "size": 20,
  "number": 0,
  "sort": {
    "sorted": false,
    "unsorted": true,
    "empty": true
  },
  "numberOfElements": 1,
  "first": true,
  "empty": false
}
```

### 2. Dettaglio Proprietà

**Descrizione**: Ottiene i dettagli completi di una specifica proprietà.

**Metodo**: `GET`

**Percorso**: `/properties/details/{id}`

**Autenticazione**: Richiesta (JWT)

**Parametri**:
- `id` (path variable): ID della proprietà (long)

**Esempio risposta**:
```json
{
  "id": 123,
  "description": "Appartamento luminoso in zona centrale",
  "price": 350000.00,
  "area": 95,
  "yearBuilt": 2010,
  "contract": "SALE",
  "propertyCategory": "Appartamento",
  "condition": "GOOD_CONDITION",
  "energyRating": "B",
  "address": {
    "country": "IT",
    "province": "RM",
    "city": "Roma",
    "street": "Via Appia",
    "civic": "123",
    "building": "A",
    "latitude": 41.9028,
    "longitude": 12.4964
  },
  "agent": {
    "id": 456,
    "name": "Mario Rossi",
    "email": "mario.rossi@agency.com",
    "phone": "+39 1234567890",
    "agencyId": 789,
    "agencyName": "Premium Real Estate"
  },
  "createdAt": "2024-01-15T10:30:00",
  "images": ["property_123_1.jpg", "property_123_2.jpg"]
}
```

### 3. Creazione Proprietà

**Descrizione**: Crea una nuova proprietà con validazione automatica e geocoding.

**Metodo**: `POST`

**Percorso**: `/properties`

**Autenticazione**: Richiesta (JWT) - Solo agenti immobiliari o manager

**Parametri di richiesta**:

Il payload utilizza deserializzazione polimorfica basata sul campo `propertyType`. La struttura base include:

**Campi comuni a tutte le proprietà**:
```json
{
  "propertyType": "RESIDENTIAL|COMMERCIAL|LAND|GARAGE",
  "description": "string (obbligatorio)",
  "price": 0.01 (obbligatorio, BigDecimal),
  "area": 1 (obbligatorio, Integer),
  "yearBuilt": 1900 (Integer, opzionale, può essere null),
  "contractType": "SALE|RENT",
  "propertyCategoryName": "string",
  "condition": "NEW|GOOD_CONDITION|RENOVATED|TO_BE_RENOVATED|POOR_CONDITION|UNDER_CONSTRUCTION",
  "energyRating": "A|B|C|D|E|F|G",
  "addressRequest": {
    "country": "IT",
    "province": "RM",
    "city": "Roma",
    "street": "Via Roma",
    "civic": "1",
    "building": "A",
    "latitude": 41.8902,
    "longitude": 12.4922
  },
  "images": ["path1.jpg", "path2.jpg"]
}
```

**Campi specifici per tipologia**:

**RESIDENTIAL**:
```json
{
  "propertyType": "RESIDENTIAL",
  "numeroLocali": 3,
  "numeroBagni": 2,
  "giardino": "NO|SMALL|MEDIUM|LARGE",
  "numeroPianiTotali": 2,
  "isArredato": false,
  "piani": ["1", "2"]
}
```

**COMMERCIAL**:
```json
{
  "propertyType": "COMMERCIAL", 
  "numeroLocali": 5,
  "piano": 1,
  "numeroBagni": 2,
  "numeroPianiTotali": 3,
  "mustHaveWheelchairAccess": true,
  "minNumeroVetrine": 2
}
```

**GARAGE**:
```json
{
  "propertyType": "GARAGE",
  "mustHaveSurveillance": true
}
```

**LAND**:
```json
{
  "propertyType": "LAND", 
  "mustBeAccessibleFromStreet": true
}
```

**Esempio richiesta completa (RESIDENTIAL)**:
```json
{
  "propertyType": "RESIDENTIAL",
  "description": "Appartamento moderno in zona residenziale",
  "price": 280000.00,
  "area": 85,
  "yearBuilt": 2015,
  "contractType": "SALE",
  "propertyCategoryName": "Appartamento",
  "condition": "GOOD_CONDITION",
  "energyRating": "A",
  "addressRequest": {
    "country": "IT",
    "province": "MI",
    "city": "Milano",
    "street": "Corso Buenos Aires",
    "civic": "45",
    "building": "B",
    "latitude": 45.4773,
    "longitude": 9.1815
  },
  "images": ["img1.jpg", "img2.jpg"],
  "numeroLocali": 3,
  "numeroBagni": 1,
  "giardino": "NO",
  "numeroPianiTotali": 5,
  "isArredato": false,
  "piani": ["3"]
}
```

**Esempio risposta**:
```json
{
  "id": 124,
  "description": "Appartamento moderno in zona residenziale",
  "price": 280000.00,
  "area": 85,
  "yearBuilt": 2015,
  "contract": "SALE",
  "propertyCategory": "Appartamento",
  "condition": "GOOD_CONDITION",
  "energyRating": "A",
  "address": {
    "country": "IT",
    "province": "MI",
    "city": "Milano",
    "street": "Corso Buenos Aires",
    "civic": "45",
    "building": "B",
    "latitude": 45.4773,
    "longitude": 9.1815
  },
  "agent": {
    "id": 457,
    "name": "Laura Bianchi",
    "email": "laura.bianchi@agency.com",
    "phone": "+39 0987654321",
    "agencyId": 790,
    "agencyName": "City Homes"
  },
  "createdAt": "2024-01-16T14:20:00"
}
```

## Gestione Campi Specifici

### `agencyId` e `agencyName`
- **Gestione valori null**: I campi `agencyId` e `agencyName` possono essere `null` quando l'agente non è associato a un'agenzia
- Nei DTO di risposta, questi campi sono opzionali e possono essere omessi o avere valore esplicito `null`

### `createdAt`
- **Inclusione**: Sempre incluso nella risposta
- **Formato**: `LocalDateTime` in formato ISO 8601 (es. "2024-01-15T10:30:00")

### `yearBuilt`
- **Gestione valori null**: Campo opzionale, può essere `null` quando l'anno di costruzione non è disponibile
- **Tipo**: Integer

### `numberOfFloors` (`numeroPianiTotali`)
- **Tipo**: Numero intero (Integer)
- **Obbligatorio**: Per proprietà residential e commercial e garage

### `condition` (ex `status`)
- **Descrizione**: Rappresenta lo stato fisico della proprietà
- **Valori possibili**:
  - `NEW`: Nuova costruzione
  - `GOOD_CONDITION`: Buone condizioni
  - `RENOVATED`: Ristrutturato di recente  
  - `TO_BE_RENOVATED`: Da ristrutturare
  - `POOR_CONDITION`: Cattive condizioni
  - `UNDER_CONSTRUCTION`: In costruzione

## Convenzioni di Naming

- **JSON**: Utilizzare camelCase per tutti i nomi dei campi
- **Enum**: Utilizzare MAIUSCOLO per i valori degli enumerativi
- **Validazione**: Seguire le annotazioni Jakarta Bean Validation (@NotNull, @Min, etc.)

## Errori comuni

**HTTP 400 - Bad Request**:
- Campi obbligatori mancanti
- Tipi di dati non validi
- Valori fuori range

**HTTP 401 - Unauthorized**:
- Token JWT mancante o scaduto
- Token non valido

**HTTP 403 - Forbidden**:
- Permessi insufficienti (solo agenti/manager possono creare proprietà)

**HTTP 404 - Not Found**:
- Proprietà non trovata
- Risorsa non esistente

## Note Tecniche

- Tutte le date sono in formato ISO 8601
- I prezzi sono in BigDecimal con precisione di 2 decimali
- Le coordinate geografiche utilizzano il sistema WGS84
- Le immagini sono gestite come array di path
- La paginazione segue lo standard Spring Data

---

*Ultimo aggiornamento: 11 Settembre 2024*