# Proposta API per la Cronologia Immobili

## Riepilogo

Questa API RESTful consente al frontend di recuperare i dettagli completi di una lista di immobili visitati di recente, inviando un array di ID.

## Endpoint

`POST /api/properties/history`

## Metodo

`POST`

## Descrizione

Recupera i dettagli completi per una lista di immobili, identificati dai loro ID. Questo approccio è stato scelto per efficienza, permettendo al client di ottenere tutte le informazioni necessarie con una singola richiesta, invece di effettuare chiamate multiple per ogni immobile.

## Request Body

Il corpo della richiesta deve essere un oggetto JSON contenente un array di ID di immobili.

-   **Formato**: `application/json`
-   **Campo**: `propertyIds` (Array di stringhe, obbligatorio)

**Esempio:**

```json
{
  "propertyIds": ["id1", "id2", "id3"]
}
```

### Validazione

-   Il campo `propertyIds` è obbligatorio.
-   `propertyIds` deve essere un array di stringhe non vuoto.
-   Se la validazione fallisce, il server risponderà con `400 Bad Request`.

## Response Body (Successo)

In caso di successo, la risposta conterrà un array di oggetti `PropertyDTO`, arricchiti con le coordinate geografiche.

-   **Codice**: `200 OK`
-   **Corpo**: Array di oggetti `PropertyDTO`.

**Esempio:**

```json
[
  {
    "id": "string",
    "title": "string",
    "description": "string",
    "address": "string",
    "price": "number",
    "agentId": "string",
    "status": "'active' | 'sold' | 'rented' | 'inactive'",
    "latitude": "number",
    "longitude": "number",
    "createdAt": "string", // Formato ISO 8601
    "updatedAt": "string"  // Formato ISO 8601
  }
]
```

**Note sui campi:**

-   I campi `latitude` e `longitude`, sebbene non presenti in `PropertyDTO`, sono inclusi per fornire dati di localizzazione completi. Potrebbero essere considerati opzionali a seconda delle esigenze di visualizzazione del frontend.

## Response Body (Errore)

Le risposte di errore seguiranno un formato standard per garantire coerenza.

-   **`400 Bad Request`**: Inviato quando il corpo della richiesta non è valido (es. `propertyIds` mancante o non è un array).
-   **`500 Internal Server Error`**: Inviato per qualsiasi altro errore non gestito lato server.

**Esempio di corpo errore:**

```json
{
  "error": "string",
  "message": "string"
}
```

## Diagramma di Sequenza

Il diagramma seguente illustra il flusso di interazione tra i componenti del sistema.

```mermaid
sequenceDiagram
    participant Client as Frontend
    participant Cache as PropertyHistoryCache
    participant Server as Backend API

    Client->>Cache: getPropertyIds()
    Cache-->>Client: Restituisce [id1, id2, ...]

    alt Se la cache non è vuota
        Client->>Server: POST /api/properties/history
        Server-->>Client: 200 OK [PropertyDTO, ...]
    else La cache è vuota
        Client->>Client: Mostra stato vuoto
    end
