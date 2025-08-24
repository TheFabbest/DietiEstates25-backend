# Piano di Sviluppo per l'Indicizzazione del Database PostgreSQL

Questo documento delinea la strategia per l'introduzione di indici e helper di monitoraggio nel database PostgreSQL a supporto dell'applicazione DietiEstates25.

## 1. Analisi e Identificazione delle Aree Critiche

L'analisi del codice ha rivelato le seguenti aree che beneficeranno maggiormente dell'indicizzazione:

-   **Ricerca e Filtro delle Proprietà (`Property`):** La classe `PropertySpecifications.java` costruisce query dinamiche molto complesse. I campi più utilizzati per il filtraggio, e quindi i principali candidati per l'indicizzazione, sono:
    -   `price` (query di tipo range)
    -   `area` (query di tipo range)
    -   `yearBuilt` (query di tipo range)
    -   `status` (query di uguaglianza)
    -   `propertyCategory` (join e uguaglianza)
    -   `contract` (join e uguaglianza)
    -   `description` (ricerca testuale con `LIKE`)

-   **Query su Offerte e Visite (`Offer`, `Visit`):** I repository `OfferRepository` e `VisitRepository` contengono query native che filtrano i risultati in base all'ID dell'utente (`user_id`) e al suo ruolo (`is_agent`). Il campo `user_id` è una foreign key ed è un candidato ideale per un indice.

-   **Join tra Entità:** Le query dinamiche fanno largo uso di `JOIN` tra la tabella `Property` e le tabelle specializzate (`ResidentialProperty`, `CommercialProperty`, etc.). Le colonne utilizzate per queste join (le foreign key) devono essere indicizzate per garantire performance ottimali.

## 2. Obiettivi di Performance

Gli obiettivi misurabili di questa iniziativa sono:

-   **Ridurre il tempo di risposta:** Diminuire di almeno il **50%** il tempo medio di risposta per le API di ricerca e filtro delle proprietà.
-   **Ottimizzare l'uso delle risorse:** Ridurre il carico su CPU e I/O del server PostgreSQL durante le operazioni di ricerca complesse.
-   **Garantire la scalabilità:** Mantenere performance elevate anche con l'aumento del volume di dati (es. raddoppio del numero di proprietà e offerte).

## 3. Strategia di Implementazione

### 3.1. Tipi di Indici da Creare

Si propone la creazione dei seguenti indici **B-Tree**, ideali per le query di range e uguaglianza:

-   **Tabella `properties`:**
    -   `price`
    -   `area`
    -   `yearBuilt`
    -   `status`
    -   `property_category_id` (foreign key)
    -   `contract_id` (foreign key)

-   **Tabella `offers`:**
    -   `user_id` (foreign key)

-   **Tabella `visits`:**
    -   `user_id` (foreign key)

Per la ricerca testuale sulla **descrizione della proprietà**, si consiglia un indice **GIN** con l'estensione `pg_trgm` per ottimizzare le query `LIKE`:

```sql
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX idx_properties_description_gin ON properties USING gin (description gin_trgm_ops);
```

### 3.2. Modalità di Creazione degli Indici

Il progetto attualmente non utilizza uno strumento di migrazione del database come Flyway o Liquibase. Si raccomanda fortemente di integrare **Flyway** nel progetto per una gestione versionata, automatizzata e sicura delle modifiche allo schema.

**Piano d'azione:**
1.  Aggiungere la dipendenza di Flyway al file `pom.xml`.
2.  Creare uno script di migrazione SQL nella cartella `src/main/resources/db/migration` (es. `V2__Add_performance_indexes.sql`).
3.  Nello script, definire i comandi `CREATE INDEX` per tutti gli indici identificati.

### 3.3. Sviluppo di Helper per il Monitoraggio

Per validare l'efficacia degli indici e per future ottimizzazioni, si propone di:

1.  **Creare un `PerformanceMonitoringService`:** Un servizio Spring che permetta di eseguire `EXPLAIN ANALYZE` su query specifiche. Questo helper sarà utile in fase di sviluppo per verificare che gli indici vengano effettivamente utilizzati.
2.  **Configurare il logging delle query lente:** Abilitare in PostgreSQL (`postgresql.conf`) il logging delle query che superano una certa soglia di tempo (es. 200ms) per identificare proattivamente colli di bottiglia.

## 4. Considerazioni

-   **Impatto sulle Scritture:** L'aggiunta di indici comporterà un leggero overhead sulle operazioni di `INSERT`, `UPDATE` e `DELETE`. Dato che il carico di lavoro previsto è prevalentemente di lettura (ricerca di immobili), questo è un compromesso accettabile.
-   **Manutenzione degli Indici:** Sarà importante monitorare la salute degli indici e assicurarsi che i processi di `VACUUM` e `ANALYZE` di PostgreSQL funzionino correttamente per evitare il degrado delle performance.
-   **Validazione:** Ogni nuovo indice dovrà essere validato tramite test di carico e analisi dei piani di esecuzione (`EXPLAIN ANALYZE`) per confermare il suo impatto positivo.