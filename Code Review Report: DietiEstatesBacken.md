# Code Review Report: DietiEstatesBackend

**Data:** 22/08/2025
**Autore:** Roo, Technical Architect

## 1. Sommario Esecutivo

Questa revisione analizza la codebase del progetto DietiEstatesBackend con un focus su sicurezza, performance, manutenibilità e aderenza ai principi di architettura software (SOLID, CLEAN CODE).

L'architettura generale è moderna, basata su Spring Boot e un'autenticazione JWT stateless. Tuttavia, sono state identificate diverse criticità architetturali e code smell che, se non affrontati, potrebbero compromettere la scalabilità, la sicurezza e la manutenibilità a lungo termine del sistema.

Le aree di intervento principali riguardano:
*   **Gestione della Sicurezza:** Inefficienze e violazioni dei principi SOLID.
*   **Progettazione dei Service:** Metodi monolitici che violano il Single Responsibility Principle.
*   **Gestione dei Dati:** Affidamento a strutture dati non tipizzate e mapping manuale.

Di seguito vengono dettagliate le criticità e le relative soluzioni strategiche.

---

## 2. Criticità Architetturali e di Sicurezza

### 2.1. [CRITICA] Violazione Open/Closed Principle e Code Smell in `SecurityUtil`

*   **File:** [`src/main/java/com/dieti/dietiestatesbackend/security/SecurityUtil.java:122`](src/main/java/com/dieti/dietiestatesbackend/security/SecurityUtil.java:122)
*   **Problema:** L'uso di `instanceof User` nel metodo `asUser` per eseguire il cast del `principal` viola il **Principio Open/Closed**. La classe è aperta alla modifica ma non all'estensione. Se si introducesse un nuovo tipo di `principal` (es. `ApiKeyPrincipal`), sarebbe necessario modificare `SecurityUtil`.
*   **Impatto:**
    *   **Manutenibilità:** La classe diventa un punto di modifica fragile e centralizzato.
    *   **Debito Tecnico:** Si accumula debito legato a un design rigido.
*   **Soluzione Proposta:**
    1.  **Introdurre un'interfaccia `AppPrincipal`:**
        ```java
        public interface AppPrincipal {
            Long getId();
            String getUsername();
            boolean isManager();
            Collection<? extends GrantedAuthority> getAuthorities();
        }
        ```
    2.  Fare in modo che l'entità `User` implementi `AppPrincipal`.
    3.  Modificare `JwtAuthenticationFilter` per costruire un `UsernamePasswordAuthenticationToken` con un `AppPrincipal` e non con l'entità `User` completa.
    4.  Modificare `SecurityUtil` per operare sull'astrazione `AppPrincipal`, eliminando la necessità di `instanceof` e cast.
*   **Trade-off:**
    *   **Vantaggi:** Design disaccoppiato, estensibile e aderente ai principi SOLID. Maggiore testabilità.
    *   **Svantaggi:** Leggero aumento della complessità iniziale per l'introduzione di una nuova astrazione.

### 2.2. [CRITICA] Bottleneck di Performance e Scalabilità nell'Autenticazione

*   **File:** [`src/main/java/com/dieti/dietiestatesbackend/security/JwtAuthenticationFilter.java:53`](src/main/java/com/dieti/dietiestatesbackend/security/JwtAuthenticationFilter.java:53)
*   **Problema:** Per ogni richiesta API autenticata, viene eseguita una query al database (`userService.getUserByUsername(...)`) per recuperare l'intera entità `User`. Ulteriori query vengono eseguite in `SecurityUtil` per validare i permessi.
*   **Impatto:**
    *   **Performance:** Aumenta la latenza di ogni richiesta.
    *   **Scalabilità:** Il database diventa un collo di bottiglia sotto carico, limitando la capacità del sistema di gestire un alto volume di traffico.
*   **Soluzione Proposta:**
    1.  **Includere i permessi come *claims* nel JWT:** Durante la generazione del token (in `AccessTokenProvider`), aggiungere i dati necessari per l'autorizzazione (ID utente, ruoli, e il flag `isManager`) come claims.
    2.  **Modificare `JwtAuthenticationFilter`:** Estrarre i claims dal token e costruire l'oggetto `Authentication` senza interrogare il database. I dati dell'utente possono essere incapsulati in un DTO leggero (`AuthenticatedUser`) che implementa `AppPrincipal`.
*   **Trade-off:**
    *   **Vantaggi:** Riduzione drastica delle query al DB, miglioramento della latenza e della scalabilità.
    *   **Svantaggi:** I token JWT diventano leggermente più grandi. I dati sui permessi nel token possono diventare "stale" (non aggiornati) se i permessi di un utente cambiano. Questo rischio può essere mitigato con token a breve scadenza e un meccanismo di refresh efficiente (già presente).

### 2.3. [MEDIO] Logica di Autorizzazione Incompleta e Rischiosa

*   **File:** [`src/main/java/com/dieti/dietiestatesbackend/security/SecurityUtil.java:110`](src/main/java/com/dieti/dietiestatesbackend/security/SecurityUtil.java:110)
*   **Problema:** La logica di `canAccessContract` è codificata per consentire l'accesso solo ai manager, con un commento che indica un'incompletezza del modello dati.
*   **Impatto:**
    *   **Sicurezza:** È una soluzione temporanea che potrebbe nascondere una falla di sicurezza se i requisiti evolvono.
    *   **Manutenibilità:** Il codice non riflette la reale logica di business.
*   **Soluzione Proposta:**
    1.  **Refactoring del Dominio:** Estendere l'entità `Contract` per includere una relazione con un proprietario (es. `agentId` o `userId`).
    2.  Aggiornare la logica di `canAccessContract` per basarsi su questa relazione, invece che su un permesso hard-coded.

---

## 3. Criticità di Design e Code Smell

### 3.1. [CRITICA] Anti-Pattern "Fat Service" in `PropertyService`

*   **File:** [`src/main/java/com/dieti/dietiestatesbackend/service/PropertyService.java:108`](src/main/java/com/dieti/dietiestatesbackend/service/PropertyService.java:108)
*   **Problema:** Il metodo `createProperty` è un monolite di oltre 200 righe che viola il **Single Responsibility Principle**. Gestisce validazione, risoluzione di dipendenze, parsing di dati non tipizzati e costruzione di entità multiple.
*   **Impatto:**
    *   **Manutenibilità:** Estremamente difficile da leggere, modificare e testare.
    *   **Affidabilità:** Alta complessità ciclomatica, che aumenta la probabilità di bug.
*   **Soluzione Proposta (Refactoring Strategico):**
    1.  **Pattern Factory/Strategy:** Introdurre un `PropertyFactory` per delegare la creazione delle entità specifiche.
        ```java
        public interface PropertyCreator {
            Property create(CreatePropertyRequest request);
        }
        // Esempio implementazione
        public class ResidentialPropertyCreator implements PropertyCreator { ... }
        ```
    2.  **DTO Specifici:** Sostituire la mappa `extras` con DTO fortemente tipizzati all'interno di `CreatePropertyRequest` (es. `residentialDetails`, `commercialDetails`), sfruttando il polimorfismo di Jackson con `@JsonTypeInfo`.
    3.  **Decomposizione:** Estrarre la logica di lookup (es. `findAgentByUsername`, `findContractByName`) in metodi privati o service ausiliari.

### 3.2. [CRITICA] Gestione Fragile dei Tipi di Proprietà

*   **File:** [`src/main/java/com/dieti/dietiestatesbackend/service/PropertyService.java:198`](src/main/java/com/dieti/dietiestatesbackend/service/PropertyService.java:198)
*   **Problema:** L'uso di uno `switch` sul tipo di proprietà e l'estrazione di dati da una `Map<String, Object>` tramite metodi helper (`asInteger`, etc.) è un grave code smell. C'è una perdita di informazioni di tipo che il sistema dovrebbe garantire staticamente.
*   **Impatto:**
    *   **Affidabilità:** Il codice è prono a `NullPointerException` e `ClassCastException` a runtime. Modifiche al frontend possono rompere il backend in modo silente.
*   **Soluzione Proposta:** Adottare DTO specifici e polimorfismo come descritto nel punto 3.1. Questo permette al compilatore e al framework di validare i dati, eliminando la necessità di parsing manuale e insicuro.

### 3.3. [MEDIO] Code Smell "Primitive Obsession" e Mapping Manuale

*   **File:** [`src/main/java/com/dieti/dietiestatesbackend/mappers/PropertyMapper.java`](src/main/java/com/dieti/dietiestatesbackend/mappers/PropertyMapper.java)
*   **Problema:** Il mapping tra DTO ed entità è interamente manuale. Questo è verboso e prono a errori.
*   **Impatto:**
    *   **Manutenibilità:** Aggiungere un campo richiede di aggiornare manualmente più punti del codice.
*   **Soluzione Proposta:**
    1.  **Adottare MapStruct:** Introdurre la dipendenza `org.mapstruct:mapstruct` e `org.mapstruct:mapstruct-processor`.
    2.  Definire interfacce di mapping annotate con `@Mapper`. MapStruct genererà automaticamente il codice di mapping al momento della compilazione.
*   **Trade-off:**
    *   **Vantaggi:** Codice più pulito, meno boilerplate, mapping type-safe e centralizzato.
    *   **Svantaggi:** Aggiunge una dipendenza e una fase di "code generation" al processo di build.

### 3.4. [BASSO] Violazione Single Responsibility Principle in `AuthController`

*   **File:** [`src/main/java/com/dieti/dietiestatesbackend/controller/AuthController.java:171`](src/main/java/com/dieti/dietiestatesbackend/controller/AuthController.java:171)
*   **Problema:** L'endpoint `getAgentInfo` è responsabile del recupero di dati utente, non dell'autenticazione.
*   **Impatto:**
    *   **Manutenibilità:** Rende la codebase meno intuitiva e organizzata.
*   **Soluzione Proposta:** Spostare l'endpoint in un `UserController` o `AgentController` dedicato.

## 4. Piano d'Azione Raccomandato

Si raccomanda di affrontare queste criticità nel seguente ordine di priorità:

1.  **Refactoring della Sicurezza (Priorità Alta):** Implementare le soluzioni 2.1 e 2.2 per risolvere i problemi di scalabilità e design in `SecurityUtil` e `JwtAuthenticationFilter`. Questo è fondamentale per la stabilità a lungo termine.
2.  **Refactoring di `PropertyService` (Priorità Alta):** Applicare le soluzioni 3.1 e 3.2 per smantellare il metodo `createProperty`. Questo migliorerà drasticamente la manutenibilità e l'affidabilità del core business logic.
3.  **Introduzione di MapStruct (Priorità Media):** Adottare MapStruct (soluzione 3.3) per pulire il codice di mapping.
4.  **Fix Minori (Priorità Bassa):** Risolvere i problemi rimanenti (2.3, 3.4) per migliorare la coerenza e la pulizia generale del codice.