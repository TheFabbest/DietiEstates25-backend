# Piano Finale di Evoluzione delle Performance

## 1. Stato dell'Arte e Obiettivi

L'architettura di ricerca attuale impiega un filtro "bounding box", un'ottima base di partenza. Questo piano delinea l'evoluzione verso un sistema più performante e scalabile, affrontando due criticità residue e una preoccupazione architetturale chiave.

**Obiettivi:**
1.  **Introdurre la Paginazione Obbligatoria**: Risolvere il problema più urgente per garantire tempi di risposta costanti.
2.  **Ottimizzare la Query Geografica**: Passare a query spaziali native per massima efficienza.
3.  **Garantire la Portabilità del Database**: Adottare soluzioni che non creino un vendor lock-in.

## 2. Piano d'Azione Dettagliato

### Task 1: [Paginazione] Introdurre `Pageable`

-   **Razionale**: Evitare il caricamento di interi dataset, causa di latenza e potenziale `OutOfMemoryError`.
-   **Interventi**: Modificare l'intera catena di ricerca (`Controller`, `Service`, `Repository`) per accettare `Pageable` e restituire `Page<PropertyResponse>`, sfruttando il supporto nativo di Spring Data JPA.

### Task 2: [Geospaziale] Evolvere da 'Bounding Box' a `ST_DWithin`

-   **Razionale**: Eliminare il calcolo in-memory (seconda fase del filtro attuale) e delegare il 100% del lavoro geospaziale al database, che lo esegue in modo esponenzialmente più veloce tramite indici GIST.
-   **Interventi**:
    1.  Aggiungere la dipendenza `hibernate-spatial`.
    2.  Modificare l'entità `Address` per usare un tipo `Point` standard JTS.
    3.  Aggiornare lo schema del DB con una colonna `geography` e un indice GIST tramite una migrazione Flyway.
    4.  In `PropertySpecifications`, sostituire la logica del bounding box con una chiamata alla funzione standard `dwithin`, che `Hibernate Spatial` tradurrà per il dialetto specifico del DB.

### Task 3: [Geospaziale] Analisi di Portabilità di Hibernate Spatial

-   **Preoccupazione**: L'uso di funzioni come `ST_DWithin` potrebbe legare l'applicazione a PostGIS.
-   **Soluzione/Analisi**:
    -   `Hibernate Spatial` agisce come un **livello di astrazione**. Traduce le funzioni spaziali standard (definite da OGC e JTS) nella sintassi nativa del database in uso (PostGIS, MySQL Spatial, Oracle Spatial, etc.).
    -   Utilizzando funzioni standard come `dwithin`, **la portabilità viene mantenuta**. Il codice JPQL rimane invariato in caso di migrazione del database.
    -   Il rischio di lock-in esisterebbe solo se usassimo funzioni altamente specifiche di PostGIS (es. `ST_ClusterWithin`), cosa che **questo piano non prevede**.
-   **Conclusione**: L'adozione di `Hibernate Spatial` per la nostra esigenza è la scelta corretta per bilanciare performance e portabilità.

### Task 4: [Caching] Implementare caching con Caffeine

-   **Razionale**: Diminuire le letture dal DB per dati a bassa volatilità.
-   **Interventi**: Usare `@Cacheable` sui metodi di servizio che recuperano dati come le categorie o i dettagli delle singole proprietà.

### Task 5: [Monitoraggio] Configurare Actuator e Prometheus

-   **Razionale**: Misurare l'impatto dei miglioramenti e monitorare la salute del sistema.
-   **Interventi**: Aggiungere `spring-boot-starter-actuator` per esporre metriche chiave.

## 3. Conclusione

Questo piano rappresenta una roadmap completa e a basso rischio per l'evoluzione del backend. Ogni passo è progettato per portare benefici tangibili in termini di performance, scalabilità e manutenibilità.

Il piano è pronto. Sono a sua disposizione per l'approvazione finale prima di suggerire il passaggio alla fase di implementazione.