# Piano Architetturale Dettagliato: Implementazione Upload Immagini su Azure

Questo documento espande il piano architetturale per l'implementazione della logica di upload delle immagini, fornendo contesto e motivazioni per ogni decisione tecnica.

---

### **Contesto Generale**

L'obiettivo è integrare un sistema di upload di immagini per le proprietà immobiliari. Attualmente, il backend può creare una proprietà, ma non gestisce i file delle immagini associate. Vogliamo che l'upload delle immagini e la creazione della voce della proprietà nel database siano un'unica **operazione atomica**: o entrambe riescono, o entrambe falliscono, per non lasciare dati "orfani" (immagini senza una proprietà o una proprietà senza le sue immagini). Utilizzeremo Azure Blob Storage come sistema di archiviazione cloud per la sua scalabilità, affidabilità e integrazione con l'ecosistema Java.

---

### **1. Configurare `AzureBlobStorageService`**

*   **Cosa faremo:**
    1.  Inietteremo nella classe `AzureBlobStorageService.java` le configurazioni definite in `application.properties`: la stringa di connessione (`azure.storage.connection-string`) e il nome del container (`azure.storage.container-name`).
    2.  Useremo queste configurazioni per inizializzare due oggetti fondamentali dell'SDK di Azure: `BlobServiceClient` e `BlobContainerClient`. Questa inizializzazione avverrà una sola volta, all'avvio dell'applicazione, tramite un metodo annotato con `@PostConstruct`.

*   **Perché lo facciamo:**
    *   **Centralizzazione della Configurazione:** Invece di scrivere i dati sensibili (come la connection string) direttamente nel codice, li leggiamo da un file di properties. Questo rende l'applicazione più sicura e flessibile, poiché possiamo cambiare le credenziali senza modificare e ricompilare il codice.
    *   **Efficienza:** Il `BlobServiceClient` è un oggetto pesante che gestisce la connessione e l'autenticazione con Azure. Crearlo una sola volta e riutilizzarlo per tutte le operazioni di upload è molto più efficiente che crearne uno nuovo per ogni richiesta. Il `BlobContainerClient` rappresenta il "contenitore" (simile a una cartella) su Azure dove verranno salvati tutti i nostri file; anche questo va creato una sola volta.

---

### **2. Implementare la Logica di Upload in `AzureBlobStorageService`**

*   **Cosa faremo:**
    1.  All'interno del metodo `uploadImages`, dopo le validazioni già presenti su numero, tipo e dimensione dei file, itereremo sulla lista di `MultipartFile` ricevuti.
    2.  Per ogni file, genereremo un nome univoco per il blob (il file su Azure) seguendo la convenzione `path/indice.webp` (es. `01H8X.../0.webp`, `01H8X.../1.webp`). Il `path` sarà l'ULID (Identificatore Univoco Lessicograficamente Ordinabile) che rappresenta la directory delle immagini per quella specifica proprietà.
    3.  Useremo il `BlobContainerClient` per ottenere un `BlobClient` per il nome specifico del blob.
    4.  Chiameremo il metodo `upload` del `BlobClient`, passandogli il flusso di dati del file (`file.getInputStream()`) e la sua lunghezza. Imposteremo anche l'header `Content-Type` corretto.

*   **Perché lo facciamo:**
    *   **Organizzazione dei File:** La struttura `path/indice.webp` ci permette di raggruppare facilmente tutte le immagini di una proprietà. L'ULID è scelto perché, a differenza dell'UUID, è ordinabile temporalmente, il che può essere utile per debug o manutenzione.
    *   **Efficienza di Memoria:** Usare `getInputStream()` è fondamentale. Permette di "trasmettere" (stream) i dati del file direttamente da chi ha fatto la richiesta al cloud di Azure, senza che il nostro server debba caricare l'intero file (che potrebbe essere di svariati megabyte) nella sua memoria. Questo riduce il consumo di RAM e rende l'applicazione più scalabile.
    *   **Esperienza Utente:** Impostare il `Content-Type` (es. `image/webp`) è cruciale. Dice al browser che il file è un'immagine e che deve visualizzarla, invece di trattarlo come un file generico da scaricare.

---

### **3. Integrare l'Upload in `PropertyManagementService` (Strategia "Storage-First")**

*   **Cosa faremo:**
    1.  Nel metodo `createPropertyWithImages` di `PropertyManagementService.java`, per prima cosa genereremo un `ULID` univoco.
    2.  **Come primo passo**, chiameremo `fileStorageService.uploadImages(...)`, passando l'ULID e i file. Questa è l'operazione I/O più lunga e la eseguiamo subito per ottimizzare l'hot path.
    3.  Se l'upload ha successo, procederemo a creare l'entità `Property` e a salvarla nel database.
    4.  L'intera operazione di salvataggio nel database sarà avvolta in un blocco `try-catch`.

*   **Perché lo facciamo:**
    *   **Ottimizzazione dell'Hot Path:** Eseguire l'operazione più lenta (upload di rete) per prima riduce il tempo in cui la transazione del database rimane potenzialmente aperta e migliora la reattività percepita.
    *   **Fail Fast:** Se l'upload fallisce (es. per problemi di connessione o file corrotti), l'operazione termina immediatamente, senza nemmeno tentare di interagire con il database.
    *   **Ruolo di Orchestratore:** Il servizio orchestra la sequenza `Storage -> DB` e gestisce la logica di compensazione in caso di fallimento del secondo passo.

---

### **4. Gestire le Eccezioni con Compensazione Sincrona**

*   **Cosa faremo:**
    1.  Se il salvataggio nel database (all'interno del blocco `try`) fallisce per qualsiasi motivo (es. violazione di un constraint, DB non raggiungibile), l'eccezione verrà catturata dal blocco `catch`.
    2.  **Come prima azione nel `catch` block**, invocheremo un nuovo metodo `fileStorageService.deleteImages(ulid)` per eliminare immediatamente i file appena caricati su Azure. Questa è l'azione di **compensazione sincrona**.
    3.  Dopo aver eseguito la pulizia, l'eccezione originale verrà rilanciata per notificare al chiamante che l'operazione è fallita.

*   **Perché lo facciamo:**
    *   **Garanzia di Consistenza:** Questo pattern garantisce che non rimangano file orfani su Azure. Se il record nel DB non può essere creato, i file associati vengono rimossi istantaneamente.
    *   **Nessuna "Eventual Consistency":** A differenza di un job di pulizia schedulato, la consistenza viene ripristinata immediatamente all'interno della stessa richiesta. Questo rende il sistema più deterministico e più facile da debuggare.
    *   **Miglior Compromesso:** Otteniamo un hot path veloce (tipico dello "Storage-First") combinato con la robustezza di una transazione di compensazione immediata, senza la complessità di un'architettura Saga distribuita o la latenza di un cleanup batch.
---

### **5. Estendere `FileStorageService` con la Logica di Compensazione**

*   **Cosa faremo:**
    1.  Aggiungeremo un nuovo metodo `void deleteImages(String directoryUlid)` all'interfaccia `FileStorageService`.
    2.  Implementeremo questo metodo in `AzureBlobStorageService`. La logica dovrà listare tutti i blob che iniziano con il prefisso `directoryUlid/` e cancellarli in un ciclo.
    3.  Questa implementazione dovrebbe essere robusta, includendo un logging adeguato e, idealmente, una politica di retry per gestire fallimenti transitori durante la cancellazione.

*   **Perché lo facciamo:**
    *   **Separazione delle Responsabilità:** Il `PropertyManagementService` non deve sapere *come* eliminare i file da Azure. La sua responsabilità è solo quella di orchestrare il flusso e richiedere un'azione di compensazione. Tutta la logica di interazione con Azure rimane confinata nel `AzureBlobStorageService`.
    *   **Manutenibilità e Riusabilità:** Centralizzare la logica di cancellazione la rende facile da testare, manutenere e potenzialmente riutilizzare in altre parti dell'applicazione, se necessario.
    *   **Clean Design:** Aderisce al principio di singola responsabilità, rendendo il codice più pulito e comprensibile.