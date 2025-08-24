# Convenzioni API - Naming JSON e Validazione

Scopo: definire la convenzione dei nomi dei campi JSON e le regole di validazione per gli endpoint di creazione proprietà.

1) Naming
- Usare camelCase per tutti i nomi dei campi JSON (es. numeroPianiTotali, numeroLocali, addressRequest).
- I DTO Java devono usare lo stesso naming camelCase.

2) Campi specifici per tipologia
- propertyType: "RESIDENTIAL" | "COMMERCIAL" | "LAND" | "GARAGE"
- RESIDENTIAL: campi obbligatori: numeroLocali, numeroBagni, giardino, numeroPianiTotali
- COMMERCIAL: campi obbligatori: numeroLocali, piano, numeroBagni, numeroPianiTotali

3) Address
- Fornire addressId (Long) oppure addressRequest (object) — almeno uno obbligatorio.
- addressRequest fields: country, province, city, street, civic, building, latitude, longitude

4) Validazione
- Validazione a due livelli:
  - Annotazioni Jakarta (es. @NotNull, @Min, @Digits) sui DTO specifici.
  - Controlli runtime nel service per campi obbligatori quando si usa il DTO unificato.
- Errore per input non valido: HTTP 400 con payload contenente dettagli dei campi mancanti/invalidi.

5) Additional properties
- Il DTO base (CreatePropertyRequest) accetta campi aggiuntivi tramite JsonAnySetter in additionalProperties.
- I nomi attesi per i campi aggiuntivi devono essere in camelCase (es. numeroPianiTotali).

6) Esempio payload (RESIDENTIAL)

{
  "propertyType": "RESIDENTIAL",
  "description": "Descrizione breve",
  "price": 250000.00,
  "area": 120,
  "contractId": 1,
  "propertyCategoryId": 2,
  "status": "AVAILABLE",
  "energyRating": "A",
  "agentId": 10,
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
  "images": ["path1.jpg","path2.jpg"],
  "numeroLocali": 5,
  "numeroBagni": 2,
  "giardino": "NO",
  "numeroPianiTotali": 2,
  "isArredato": false,
  "piani": ["1"]
}

7) Azioni consigliate frontend
- Usare camelCase e validare i campi prima dell'invio.
- Allineare i nomi degli enum al backend (MAIUSCOLO).

8) Azioni consigliate backend
- Mantenere annotazioni di validazione sui DTO specifici.
- Documentare gli endpoint e i payload con OpenAPI/Swagger.
- Fornire esempi espliciti (come sopra) nella documentazione.

Fine.