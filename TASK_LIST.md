# Elenco Dettagliato dei Compiti per DietiEstatesBackend - Roadmap di Refactoring e Sicurezza

**Contesto:** Questo documento elenca i compiti di refactoring e miglioramento della sicurezza identificati durante le recenti code review e security review del progetto DietiEstatesBackend. I compiti sono raggruppati per fase e priorità, fornendo una roadmap chiara per l'implementazione.

---

## 1. Fase 1: Interventi Immediati (Sicurezza Critica e Stabilità)

Questi compiti hanno la massima priorità e devono essere affrontati immediatamente per mitigare i rischi critici di sicurezza e migliorare la stabilità del sistema.

### Compiti:

*   **Rifattorizzare l'autenticazione:** Modificare [`AuthController.login()`](src/main/java/com/dieti/dietiestatesbackend/controller/AuthController.java:58) per usare `AuthenticationManager` di Spring Security.
    *   **Priorità:** CRITICA
    *   **Descrizione:** L'attuale implementazione di login personalizzata bypassa i meccanismi di sicurezza di Spring, aumentando il rischio di vulnerabilità.
*   **Implementare controlli IDOR:** Aggiungere `@PreAuthorize` o logica di verifica sui controller/service per gli endpoint vulnerabili.
    *   **Priorità:** CRITICA
    *   **Endpoint interessati:** [`/contracts`](src/main/java/com/dieti/dietiestatesbackend/controller/ContractController.java:17), [`/agent/info/{id}`](src/main/java/com/dieti/dietiestatesbackend/controller/AuthController.java:147), [`/properties/details/{id}`](src/main/java/com/dieti/dietiestatesbackend/controller/PropertiesController.java:56), [`/offers/agent_offers/{id}`](src/main/java/com/dieti/dietiestatesbackend/controller/OfferController.java:27), [`/offers/agent_visits/{id}`](src/main/java/com/dieti/dietiestatesbackend/controller/VisitController.java:29).
    *   **Descrizione:** Accesso non autorizzato a risorse tramite ID senza adeguati controlli di autorizzazione.
*   **Sostituire `RefreshTokenRepository`:** Implementare una soluzione persistente per i refresh token (DB o Redis).
    *   **Priorità:** CRITICA
    *   **File Interessato:** [`RefreshTokenRepository.java`](src/main/java/com/dieti/dietiestatesbackend/security/RefreshTokenRepository.java)
    *   **Descrizione:** L'attuale gestione in-memory dei token porta a perdita di stato e vulnerabilità di concorrenza.
*   **Aggiornare API JWT:** Sostituire i metodi deprecati della libreria `jjwt`.
    *   **Priorità:** ALTA
    *   **File Interessato:** [`RefreshTokenProvider.java:26`](src/main/java/com/dieti/dietiestatesbackend/security/RefreshTokenProvider.java:26)
    *   **Descrizione:** Uso di API JWT deprecate che possono causare problemi con futuri aggiornamenti.
*   **Esternalizzare Configurazioni e Secret:** Spostare tutti i valori hardcodati o fragili in `application.properties` (o `yml`) e variabili d'ambiente.
    *   **Priorità:** ALTA
    *   **File Interessati:** [`GoogleTokenValidator.java:16`](src/main/java/com/dieti/dietiestatesbackend/security/GoogleTokenValidator.java:16), [`PropertyImageUtils.java:17`](src/main/java/com/dieti/dietiestatesbackend/util/PropertyImageUtils.java:17), [`AccessTokenProvider.java:20`](src/main/java/com/dieti/dietiestatesbackend/security/AccessTokenProvider.java:20)
    *   **Descrizione:** Hardcoding di configurazioni sensibili rende difficile la gestione multi-ambiente e aumenta i rischi.
*   **Configurare Profili di Produzione Sicuri:** Creare `application-prod.properties` per disabilitare `ddl-auto=update`, `show_sql=true`, `include-message=always` e proteggere Swagger.
    *   **Priorità:** ALTA
    *   **File Interessati:** [`application.properties`](src/main/resources/application.properties), [`SecurityConfig.java`](src/main/java/com/dieti/dietiestatesbackend/config/SecurityConfig.java:1)
    *   **Descrizione:** Le attuali configurazioni espongono dettagli interni e superfici di attacco in produzione.

---

## 2. Fase 2: Miglioramenti Architetturali e di Manutenibilità

Questi compiti mirano a migliorare la struttura del codice, la sua manutenibilità e la facilità di estensione.

### Compiti:

*   **Creare DTO di Risposta:** Implementare DTO specifici per tutte le entità esposte nell'API.
    *   **Priorità:** ALTA
    *   **File Interessati:** [`PropertyResponse.java`](src/main/java/com/dieti/dietiestatesbackend/dto/response/PropertyResponse.java) e altri DTO/entità esposti.
    *   **Descrizione:** Disaccoppiare l'API dal modello del database per maggiore flessibilità e sicurezza.
*   **Gestione delle Relazioni JPA:** Modificare tutte le relazioni `@ManyToOne` e `@OneToMany` a `FetchType.LAZY` e usare `JOIN FETCH` nelle query.
    *   **Priorità:** ALTA
    *   **File Interessati:** [`Property.java`](src/main/java/com/dieti/dietiestatesbackend/entities/Property.java), [`Agency.java`](src/main/java/com/dieti/dietiestatesbackend/entities/Agency.java), [`User.java`](src/main/java/com/dieti/dietiestatesbackend/entities/User.java)
    *   **Descrizione:** Ottimizzare le performance del database e prevenire problemi N+1.
*   **Unificazione DTO di Creazione Proprietà:** Eliminare la gerarchia `PropertyRequest` e rifattorizzare `CreatePropertyRequest` per usare il polimorfismo di Jackson, eliminando la mappa `additionalProperties` a favore di oggetti fortemente tipizzati.
    *   **Priorità:** ALTA
    *   **File Interessati:** Package `src/main/java/com/dieti/dietiestatesbackend/dto/request/`
    *   **Descrizione:** Semplificare e rendere più robusta la gestione dei DTO di input.
*   **Refattorizzare `PropertyService`:** Estrarre la logica complessa di `createProperty` in classi dedicate (es. Factory o Strategy Pattern).
    *   **Priorità:** ALTA
    *   **File Interessato:** [`PropertyService.java:108`](src/main/java/com/dieti/dietiestatesbackend/service/PropertyService.java:108)
    *   **Descrizione:** Migliorare la manutenibilità, la testabilità e la conformità al SRP.
*   **Centralizzare Gestione Eccezioni:** Rimuovere `@ExceptionHandler` locali e gestire tutte le eccezioni tramite [`GlobalExceptionHandler`](src/main/java/com/dieti/dietiestatesbackend/exception/GlobalExceptionHandler.java:1).
    *   **Priorità:** MEDIA
    *   **File Interessati:** [`PropertiesController.java:105`](src/main/java/com/dieti/dietiestatesbackend/controller/PropertiesController.java:105) e altri controller.
    *   **Descrizione:** Garantire formati di errore consistenti e prevenire la fuga di informazioni sensibili.
*   **Normalizzare Gestione Dipendenze:** Rimuovere il bean `Connection` e iniettare `PasswordEncoder` e altri bean tramite Spring.
    *   **Priorità:** MEDIA
    *   **File Interessati:** [`UserService.java:14`](src/main/java/com/dieti/dietiestatesbackend/service/UserService.java:14), [`JdbcConfig.java:19`](src/main/java/com/dieti/dietiestatesbackend/config/JdbcConfig.java:19)
    *   **Descrizione:** Aderire alle best practice di Spring per la gestione delle dipendenze.

---

## 3. Fase 3: Ottimizzazioni e Best Practice Aggiuntive

Questi compiti rappresentano miglioramenti a lungo termine per l'ottimizzazione e l'adozione di best practice.

### Compiti:

*   **Valutare QueryDSL:** Considerare l'adozione di QueryDSL per la gestione di query complesse e dinamiche.
    *   **Priorità:** BASSA
    *   **File Interessato:** [`PropertySpecifications.java`](src/main/java/com/dieti/dietiestatesbackend/specification/PropertySpecifications.java)
    *   **Descrizione:** Semplificare la scrittura e la manutenibilità di query complesse.
*   **Analisi di Dipendenze (SCA):** Integrare strumenti di Software Composition Analysis (es. OWASP Dependency-Check) nel pipeline CI/CD.
    *   **Priorità:** MEDIA
    *   **File Interessato:** [`pom.xml`](pom.xml)
    *   **Descrizione:** Scansionare regolarmente le dipendenze alla ricerca di vulnerabilità note.
*   **Refactoring `AccessTokenProvider`:** Trasformare in un bean Spring e ottimizzare la gestione dei refresh token usati.
    *   **Priorità:** MEDIA
    *   **File Interessato:** [`AccessTokenProvider.java:1`](src/main/java/com/dieti/dietiestatesbackend/security/AccessTokenProvider.java:1)
    *   **Descrizione:** Migliorare la flessibilità, la testabilità e la gestione dei token.
