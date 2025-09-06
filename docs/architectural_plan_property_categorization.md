---

# Piano Architetturale: Ottimizzazione del Sistema di Categorizzazione degli Immobili

Questo documento delinea il piano architetturale per eliminare la ridondanza e migliorare la chiarezza nella gestione delle categorie di immobili, basandosi sulla relazione tra `property_type` e `name` nella tabella `property_category`.

## 1. Obiettivo

Eliminare la ridondanza tra la classificazione del tipo di immobile (es. Residenziale, Commerciale) e la sua categoria specifica (es. Appartamento, Ufficio), garantendo coerenza, scalabilità e una chiara separazione delle responsabilità tra backend e frontend.

## 2. Analisi Preliminare e Problematica

La precedente implementazione presentava una potenziale ridondanza tra il campo `property_type` (utilizzato per la logica di business e la distinzione delle sottoclassi Java) e un concetto di `category` (che poteva sovrapporsi o essere una traduzione del `property_type`). Questa sovrapposizione generava confusione e potenziale incoerenza dei dati.

## 3. Soluzione Architetturale Proposta

La soluzione si basa sulla **specializzazione netta dei ruoli** delle colonne nella tabella `property_category` e sulla **centralizzazione della logica di presentazione nel frontend**.

### 3.1. Modello Dati Ottimizzato (`property_category`)

La tabella `property_category` sarà semplificata per contenere solo due colonne fondamentali per la classificazione, eliminando qualsiasi ridondanza e garantendo la canonizzazione dei dati:

*   **`property_type` (VARCHAR)**:
    *   **Ruolo**: Funge da **discriminatore tecnico** per la gerarchia delle classi JPA (es. `RESIDENTIAL`, `COMMERCIAL`, `LAND`, `GARAGE`).
    *   **Funzione Aggiuntiva**: Agisce anche come **chiave di raggruppamento canonica** per le categorie specifiche.
    *   **Gestione**: I suoi valori sono stringhe fisse e non tradotte, definite dagli sviluppatori per mappare le sottoclassi Java e i tipi di proprietà a livello di business logic.
    *   **Visibilità**: **Non è mai esposto direttamente all'utente finale**. È un dettaglio implementativo del backend.

*   **`name` (VARCHAR)**:
    *   **Ruolo**: Rappresenta il **nome specifico e univoco della categoria** (es. "Apartment", "Villa", "Office", "Shop").
    *   **Gestione**: È il valore che l'utente seleziona e che identifica in modo univoco la sottocategoria.
    *   **Visibilità**: È il nome che viene mostrato all'utente per la selezione della categoria più granulare.

**Struttura della Tabella `property_category`:**

| `property_type` (Chiave di Gruppo Canonica) | `name` (Nome Categoria Specifica) |
| :------------------------------------------ | :-------------------------------- |
| RESIDENTIAL                                 | Apartment                         |
| RESIDENTIAL                                 | Villa                             |
| COMMERCIAL                                  | Office                            |
| COMMERCIAL                                  | Shop                              |
| LAND                                        | Agricultural Land                 |
| GARAGE                                      | Single Garage                     |

### 3.2. Gestione della Presentazione (Internazionalizzazione e UI)

La logica di presentazione e traduzione dei `property_type` (es. da "RESIDENTIAL" a "Residenziale") è demandata interamente al **frontend**, sfruttando i meccanismi di internazionalizzazione (i18n).

*   **Backend**: Espone solo le chiavi tecniche (`property_type` e `name`).
*   **Frontend**: Utilizza file di traduzione (es. JSON) per mappare le chiavi tecniche (`RESIDENTIAL`) alle loro rappresentazioni leggibili dall'utente ("Residenziale") nella lingua selezionata.

### 3.3. Interazione Client-Server e Flusso Utente

Il flusso di interazione tra il client e il server sarà basato su un sistema di selezione a cascata, garantendo coerenza e una User Experience intuitiva:

1.  **Recupero Tipi di Proprietà (Dropdown 1 - "Tipo Generale"):**
    *   Il frontend effettua una chiamata a un nuovo endpoint del backend, ad esempio `GET /api/property-types`.
    *   Il backend restituisce una lista di stringhe rappresentanti i `property_type` unici presenti in `property_category` (es. `["RESIDENTIAL", "COMMERCIAL", "LAND", "GARAGE"]`).
    *   Il frontend riceve queste chiavi tecniche e le traduce per la visualizzazione all'utente utilizzando i suoi meccanismi di i18n (es. "Residenziale", "Commerciale").
    *   L'utente seleziona un'opzione (es. "Residenziale"). Il frontend conserva la chiave tecnica corrispondente (es. "RESIDENTIAL").

2.  **Recupero Categorie Specifiche (Dropdown 2 - "Categoria Specifica"):**
    *   Dopo la selezione del tipo generale, il frontend effettua una seconda chiamata al backend, passando la chiave tecnica del tipo selezionato, ad esempio `GET /api/categories?type=RESIDENTIAL`.
    *   Il backend interroga la tabella `property_category` per trovare tutti i `name` associati a quel `property_type` (es. `SELECT name FROM property_category WHERE property_type = 'RESIDENTIAL'`).
    *   Il backend restituisce la lista dei nomi delle categorie specifiche (es. `["Apartment", "Villa", "Penthouse"]`).
    *   Il frontend popola il secondo dropdown con questi nomi.
    *   L'utente seleziona una categoria specifica (es. "Apartment").

3.  **Creazione dell'Immobile (Salvataggio):**
    *   Quando l'utente procede al salvataggio, il frontend invia al backend il `name` della categoria specifica selezionata (es. `{"categoryName": "Apartment", ...}`). **È cruciale che il `property_type` non venga inviato dal client in questa fase.**
    *   Il `PropertyService` nel backend riceve il `categoryName`.
    *   Esegue una lookup nel database per trovare la riga completa di `property_category` corrispondente al `categoryName` fornito.
    *   Dalla riga recuperata, estrae il `property_type` associato (es. "RESIDENTIAL").
    *   Utilizza questo `property_type` (che è la "single source of truth") per determinare la sottoclasse di `Property` da istanziare (es. `ResidentialProperty`) tramite la `PropertyCreatorFactory`.
    *   L'immobile viene salvato, e il discriminatore JPA nella tabella `property` viene popolato correttamente con il `property_type` derivato.

## 4. Benefici e Vantaggi

*   **Eliminazione della Ridondanza:** Il database non contiene più informazioni duplicate o logicamente sovrapposte.
*   **Single Source of Truth:** Il `property_type` nella tabella `property_category` è l'unica fonte autorevole per la classificazione tecnica dell'immobile.
*   **Separazione delle Responsabilità (SoC):**
    *   **Backend**: Gestisce la logica di business, la persistenza dei dati e la validazione, operando su chiavi canoniche e agnostiche dalla lingua.
    *   **Frontend**: Si occupa della presentazione, dell'interazione utente e dell'internazionalizzazione.
*   **Coerenza dei Dati:** Impossibilità di creare immobili con un tipo e una categoria incoerenti, poiché il tipo è sempre derivato dalla categoria scelta.
*   **Flessibilità e Manutenibilità:** Modifiche ai nomi visuali o aggiunta di nuove traduzioni non richiedono modifiche al backend o al database.
*   **Robustezza e Sicurezza:** La logica di creazione è guidata da dati interni e controllati dal backend, riducendo la superficie di attacco e prevenendo manipolazioni client-side.
*   **Aderenza ai Principi SOLID:** In particolare, rafforza il Single Responsibility Principle e l'Open/Closed Principle.

Questo piano fornisce una soluzione pulita, efficiente e scalabile per la categorizzazione degli immobili.