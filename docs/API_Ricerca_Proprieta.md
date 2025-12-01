# Documentazione API: Ricerca Proprietà

Questo documento fornisce al team di frontend una guida chiara e concisa sulla funzionalità di ricerca delle proprietà esposta dal backend.

**Controller e Servizio Responsabili:**
*   **Controller:** [`PropertiesController.java`](src/main/java/com/dieti/dietiestatesbackend/controller/PropertiesController.java)
*   **Servizio:** [`PropertyService.java`](src/main/java/com/dieti/dietiestatesbackend/service/PropertyService.java)

Il metodo principale per la ricerca è `POST /properties/search` nel `PropertiesController`, che delega la logica di business a `searchPropertiesWithFilters` nel `PropertyService`.

## Endpoint di Ricerca

**Endpoint:** `POST /properties/search`
Questo endpoint consente di effettuare ricerche filtrate di proprietà con supporto per la paginazione.

## Parametri Accettati

L'endpoint accetta un oggetto [`FilterRequest`](src/main/java/com/dieti/dietiestatesbackend/dto/request/FilterRequest.java) nel corpo della richiesta (`@RequestBody`) e un oggetto `Pageable` per la paginazione.

### DTO `FilterRequest`

Il DTO [`FilterRequest`](src/main/java/com/dieti/dietiestatesbackend/dto/request/FilterRequest.java) contiene i seguenti campi:

| Nome Parametro             | Tipo di Dato           | Obbligatorio/Facoltativo | Valori Accettati / Note                                              | Descrizione                                                          |
| :------------------------- | :--------------------- | :----------------------- | :------------------------------------------------------------------- | :------------------------------------------------------------------- |
| `category`                 | String                 | Facoltativo              | Vedere la sezione "Enum `PropertyCategory`"                          | Categoria della proprietà.                                           |
| `propertySubcategoryName`  | String                 | Facoltativo              | (es. Apartment, Villa, Office)                                       | Sottocategoria specifica della proprietà. Utilizzabile da solo o in combinazione con `category`. |
| `contract`                 | String                 | Facoltativo              | "RENT", "SALE"                                                       | Tipo di contratto.                                                   |
| `minPrice`                 | BigDecimal             | Facoltativo              | Valori numerici positivi.                                            | Prezzo minimo della proprietà.                                       |
| `maxPrice`                 | BigDecimal             | Facoltativo              | Valori numerici positivi.                                            | Prezzo massimo della proprietà.                                      |
| `minArea`                  | Integer                | Facoltativo              | Valori numerici positivi.                                            | Area minima in metri quadrati.                                       |
| `minYearBuilt`             | Integer                | Facoltativo              | Anno valido (es. 1900-2025).                                         | Anno minimo di costruzione.                                          |
| `acceptedCondition`        | List<PropertyCondition> | Facoltativo              | Vedere la sezione "Enum `PropertyCondition`"                         | Condizioni accettate della proprietà.                                |
| `minEnergyRating`          | EnergyRating           | Facoltativo              | Vedere la sezione "Enum `EnergyRating`"                              | Classificazione energetica minima.                                   |
| `centerLatitude`           | BigDecimal             | **Obbligatorio**         | Valori numerici validi per la latitudine (-90.0 a +90.0).            | Latitudine del centro della ricerca geografica.                      |
| `centerLongitude`          | BigDecimal             | **Obbligatorio**         | Valori numerici validi per la longitudine (-180.0 a +180.0).         | Longitudine del centro della ricerca geografica.                     |
| `radiusInMeters`           | Double                 | **Obbligatorio**         | Valori numerici positivi.                                            | Raggio di ricerca in metri dal centro specificato.                   |
| `minNumberOfFloors`        | Integer                | Facoltativo              | Valori numerici positivi.                                            | Numero minimo di piani (per proprietà residenziali/commerciali).     |
| `minNumberOfRooms`         | Integer                | Facoltativo              | Valori numerici positivi.                                            | Numero minimo di stanze (per proprietà residenziali).                |
| `minNumberOfBathrooms`     | Integer                | Facoltativo              | Valori numerici positivi.                                            | Numero minimo di bagni (per proprietà residenziali).                 |
| `minParkingSpaces`         | Integer                | Facoltativo              | Valori numerici positivi.                                            | Numero minimo di posti auto (per garage o proprietà con garage).     |
| `heating`                  | String                 | Facoltativo              | "Centralized", "Autonomous", "Absent"                                | Tipo di riscaldamento.                                               |
| `acceptedGarden`           | List<Garden>           | Facoltativo              | Vedere la sezione "Enum `Garden`"                                    | Tipi di giardino accettati.                                          |
| `mustBeFurnished`          | Boolean                | Facoltativo              | `true` o `false`                                                     | `true` se la proprietà deve essere arredata.                         |
| `mustHaveElevator`         | Boolean                | Facoltativo              | `true` o `false`                                                     | `true` se la proprietà deve avere un ascensore.                      |
| `mustHaveWheelchairAccess` | Boolean                | Facoltativo              | `true` o `false`                                                     | `true` se la proprietà deve essere accessibile ai disabili.          |
| `mustHaveSurveillance`     | Boolean                | Facoltativo              | `true` o `false`                                                     | `true` se la proprietà deve avere sorveglianza.                      |
| `mustBeAccessibleFromStreet` | Boolean              | Facoltativo              | `true` o `false`                                                     | `true` se il terreno deve essere accessibile dalla strada.           |

**Nota Importante:** I parametri geografici (`centerLatitude`, `centerLongitude`, `radiusInMeters`) sono **obbligatori** per tutte le query di ricerca. Questi parametri definiscono l'area geografica entro cui effettuare la ricerca. Se non specificati, la ricerca non potrà essere eseguita.

### Oggetto `Pageable`

Il parametro `pageable` è un oggetto `Pageable` di Spring Data e gestisce la paginazione e l'ordinamento dei risultati. Può includere i seguenti parametri come query string o parte del body (a seconda dell'implementazione del client):
*   `page`: Numero della pagina (base 0).
*   `size`: Numero di elementi per pagina.
*   `sort`: Criteri di ordinamento (es. `price,desc` per ordinare per prezzo decrescente).

## Formato della Risposta Atteso

La risposta dell'endpoint `POST /properties/search` è una pagina (`org.springframework.data.domain.Page`) di oggetti [`PropertyResponse`](src/main/java/com/dieti/dietiestatesbackend/dto/response/PropertyResponse.java).

Ogni oggetto `PropertyResponse` include i seguenti campi principali:

| Nome Campo        | Tipo di Dato       | Descrizione                                          |
| :---------------- | :----------------- | :--------------------------------------------------- |
| `id`              | Long               | ID univoco della proprietà.                          |
| `description`     | String             | Descrizione dettagliata della proprietà.             |
| `price`           | BigDecimal         | Prezzo della proprietà.                              |
| `area`            | Integer            | Area della proprietà in metri quadrati.              |
| `yearBuilt`       | Integer            | Anno di costruzione della proprietà.                  |
| `contract`        | String             | Tipo di contratto (es. "SALE", "RENT").              |
| `propertyCategory`| String             | Categoria della proprietà (es. "RESIDENTIAL", "COMMERCIAL"). |
| `condition`       | String             | Condizione della proprietà.                          |
| `energyRating`    | String             | Classificazione energetica.                          |
| `address`         | AddressResponseDTO | Dettagli dell'indirizzo della proprietà.             |
| `agent`           | AgentResponseDTO   | Dettagli dell'agente associato alla proprietà.       |
| `createdAt`       | LocalDateTime      | Data e ora di creazione della proprietà.             |
| `updatedAt`       | LocalDateTime      | Data e ora dell'ultimo aggiornamento della proprietà. |
| `firstImageUrl`   | String             | URL della prima immagine della proprietà.            |
| `numberOfImages`  | int                | Numero totale di immagini disponibili per la proprietà. |

## Dettaglio degli Enum e Valori Accettati

Per garantire la coerenza e facilitare lo sviluppo del frontend, di seguito sono elencati i valori accettati per gli enum utilizzati nei filtri.

### Enum `PropertyCategory`
I valori per la categoria della proprietà sono stringhe che rappresentano i tipi principali di proprietà:
*   `"RESIDENTIAL"`
*   `"COMMERCIAL"`
*   `"GARAGE"`
*   `"LAND"`

### Enum `PropertyCondition`
I valori per la condizione della proprietà sono:
*   `"NEW"`
*   `"GOOD_CONDITION"`
*   `"RENOVATED"`
*   `"TO_BE_RENOVATED"`
*   `"POOR_CONDITION"`
*   `"UNDER_CONSTRUCTION"`

### Enum `EnergyRating`
I valori per la classificazione energetica sono:
*   `"A4"`
*   `"A3"`
*   `"A2"`
*   `"A1"`
*   `"B"`
*   `"C"`
*   `"D"`
*   `"E"`
*   `"F"`
*   `"G"`
*   `"NOT_APPLIABLE"`

### Enum `Garden`
I valori per il tipo di giardino sono:
*   `"PRIVATE"`
*   `"SHARED"`
*   `"ABSENT"`

### Enum `Heating`
I valori per il tipo di riscaldamento (come da contesto):
*   `"Centralized"`
*   `"Autonomous"`
*   `"Absent"`

### Enum `Contract`
I valori per il tipo di contratto (come da contesto):
*   `"RENT"`
*   `"SALE"`

## Applicabilità dei Filtri per Categoria di Proprietà

I filtri disponibili nell'oggetto `FilterRequest` possono essere categorizzati in base alla loro applicabilità ai diversi tipi di proprietà. È fondamentale comprendere quali filtri sono validi per ciascuna `PropertyCategory` per costruire query di ricerca efficaci.

### Filtri Comuni (applicabili a tutte le proprietà):
*   [`category`](#) (per selezionare il tipo generale di proprietà: `RESIDENTIAL`, `COMMERCIAL`, `GARAGE`, `LAND`)
*   [`propertySubcategoryName`](#) (applicabile se la categoria ha sottocategorie, es. `RESIDENTIAL` -> `Apartment`)
*   [`contract`](#)
*   [`minPrice`](#)
*   [`maxPrice`](#)
*   [`minArea`](#)
*   [`minYearBuilt`](#)
*   [`acceptedCondition`](#)
*   [`minEnergyRating`](#)
*   [`centerLatitude`](#) (obbligatorio)
*   [`centerLongitude`](#) (obbligatorio)
*   [`radiusInMeters`](#) (obbligatorio)

### Filtri Specifici per Proprietà Residenziali (`PropertyCategory.RESIDENTIAL`):
*   [`minNumberOfFloors`](#)
*   [`minNumberOfRooms`](#)
*   [`minNumberOfBathrooms`](#)
*   [`minParkingSpaces`](#)
*   [`heating`](#)
*   [`acceptedGarden`](#)
*   [`mustBeFurnished`](#)
*   [`mustHaveElevator`](#)

### Filtri Specifici per Proprietà Commerciali (`PropertyCategory.COMMERCIAL`):
*   [`minNumberOfFloors`](#)
*   [`minNumberOfRooms`](#)
*   [`minNumberOfBathrooms`](#)
*   [`mustHaveWheelchairAccess`](#)

### Filtri Specifici per Garage (`PropertyCategory.GARAGE`):
*   [`minNumberOfFloors`](#)
*   [`mustHaveSurveillance`](#)

### Filtri Specifici per Terreni (`PropertyCategory.LAND`):
*   [`mustBeAccessibleFromStreet`](#)

## Esempi Pratici di Filtri e Possibilità di Ricerca

Il sistema consente una ricerca flessibile combinando filtri comuni, specifici per categoria e geografici. La ricerca geografica (`centerLatitude`, `centerLongitude`, `radiusInMeters`) è obbligatoria per tutte le query di filtro.

### Esempio 1: Ricerca di proprietà residenziali in vendita con filtri di prezzo e numero di stanze

```json
{
  "category": "RESIDENTIAL",
  "propertySubcategoryName": "Apartment",
  "contract": "SALE",
  "minPrice": 100000,
  "maxPrice": 300000,
  "minNumberOfRooms": 3,
  "centerLatitude": 41.902782,
  "centerLongitude": 12.496366,
  "radiusInMeters": 5000
}
```

### Esempio 2: Ricerca di garage in affitto con sorveglianza e area minima

```json
{
  "category": "GARAGE",
  "contract": "RENT",
  "minArea": 20,
  "mustHaveSurveillance": true,
  "centerLatitude": 45.464203,
  "centerLongitude": 9.189982,
  "radiusInMeters": 2000
}
```

### Esempio 3: Ricerca di terreni accessibili dalla strada

```json
{
  "category": "LAND",
  "mustBeAccessibleFromStreet": true,
  "centerLatitude": 40.8518,
  "centerLongitude": 14.2681,
  "radiusInMeters": 10000
}
```

### Esempio 4: Ricerca di proprietà con classificazione energetica specifica e riscaldamento centralizzato

```json
{
  "minEnergyRating": "A1",
  "heating": "Centralized",
  "centerLatitude": 41.902782,
  "centerLongitude": 12.496366,
  "radiusInMeters": 7500
}