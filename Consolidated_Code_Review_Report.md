# Report Consolidato di Code Review: DietiEstatesBackend
**Data:** 31 Agosto 2025

## 1. Riepilogo Esecutivo

Questa revisione consolida due analisi complementari della codebase di DietiEstatesBackend. L'applicazione si basa su uno stack tecnologico moderno (Java 17, Spring Boot 3), ma presenta un significativo debito tecnico che ne compromette manutenibilità, robustezza, scalabilità e testabilità.

Le criticità spaziano da problemi architetturali sistemici—come colli di bottiglia nelle performance, violazioni dei confini tra layer e una quasi totale assenza di test—a debolezze a livello di codice come inconsistente gestione degli errori, scarsa type-safety e codice boilerplate che riduce la leggibilità.

Questo documento presenta un'analisi unificata e un piano d'azione strategico prioritizzato per risanare la codebase, allineandola ai principi di CLEAN CODE, CLEAN ARCHITECTURE e SOLID.

## 2. Criticità Architetturali e Sistemiche

### 2.1. Testabilità (Criticità Bloccante)

*   **Problema:** L'applicazione è priva di una suite di test automatici (unitari, di integrazione). L'unico file di test è disabilitato.
*   **Impatto:** Rende qualsiasi modifica o refactoring estremamente rischioso, impedisce la validazione automatica della correttezza del codice e ha contribuito direttamente al degrado del design generale, poiché il codice non è stato scritto per essere testabile.

### 2.2. Performance e Scalabilità (Criticità Molto Alta)

*   **Problema:** L'uso sistematico di `fetch = FetchType.EAGER` su quasi tutte le relazioni delle entità (`@ManyToOne`, `@OneToOne`).
*   **Impatto:** Causa il problema delle **"N+1 query"**, che degrada drasticamente le performance e rende l'applicazione non scalabile. Le performance crolleranno in modo non lineare all'aumentare dei dati.

### 2.3. Design del Software e Principi SOLID (Criticità Alta)

*   **Problema:** Violazioni sistematiche del Single Responsibility Principle (SRP) e di altri principi di buon design.
    *   **God Services:** Classi come `PropertyService` e `UserService` sono monoliti con troppe responsabilità (creazione, ricerca, validazione, risoluzione dipendenze), risultando in bassa coesione e alto accoppiamento.
    *   **Anemic Domain Model:** Le entità sono semplici contenitori di dati, prive di logica di business, che è invece sparsa impropriamente nei servizi.
    *   **Pattern Complessi e Usati Impropriamente:** Il pattern Visitor in `PropertyCreationService` è una soluzione eccessivamente complessa che viola il SRP, mescolando validazione, accesso ai dati e mappatura.
*   **Impatto:** Codice difficile da comprendere, testare e manutenere. Rigidità del sistema e difficoltà nell'introdurre modifiche.

### 2.4. Gestione delle Eccezioni e Confini tra Layer (Criticità Alta)

*   **Problema:** La gestione degli errori è ibrida e viola i confini architetturali.
    *   **Strategia Inconsistente:** Coesistono un `GlobalExceptionHandler`, handler locali nei controller e `try-catch` manuali, portando a risposte di errore non standardizzate.
    *   **Accoppiamento tra Layer:** I servizi lanciano `ResponseStatusException`, un'eccezione del layer web, accoppiando la logica di business all'infrastruttura HTTP.
*   **Impatto:** Manutenibilità ridotta, risposte API inconsistenti e violazione dei principi di un'architettura a layer pulita.

### 2.5. Sicurezza (Criticità Media)

*   **Problema:** Il token JWT contiene dati che possono diventare obsoleti, come i ruoli dell'utente (`isManager`, `roles`).
*   **Impatto:** Se i permessi di un utente cambiano, il token esistente rimane valido con i vecchi permessi fino alla sua scadenza, creando una potenziale falla di sicurezza e di coerenza dei dati.

### 2.6. Configurazione di Progetto (Criticità Media)

*   **Problema:** La configurazione di `application.properties` abilita `spring.jpa.hibernate.ddl-auto=update` mentre Flyway è disabilitato.
*   **Impatto:** Rischio elevato di corruzione o perdita di dati in produzione, vanificando lo scopo di uno strumento di migrazione come Flyway.

## 3. Criticità a Livello di Codice e Developer Experience

### 3.1. Leggibilità e Chiarezza

*   **Codice Boilerplate Eccessivo:** L'assenza di librerie come Lombok costringe alla scrittura manuale di getter, setter e costruttori, appesantendo le classi e riducendone la leggibilità.
*   **Tipi di Ritorno Generici (`ResponseEntity<Object>`):** Nascondere il contratto di risposta effettivo delle API rende più difficile per i client l'integrazione e la comprensione del comportamento atteso.
*   **Inconsistenze di Nomenclatura:** Nomi di campi non allineati tra DTO ed Entità (es. `totalFloors` vs `numberOfFloors`) aumentano la complessità della mappatura.

### 3.2. Soluzioni Non Type-Safe

*   **Deserializzazione con `JsonNode`:** L'endpoint di creazione proprietà accetta un `JsonNode` generico, bypassando la validazione a compile-time e aumentando il rischio di `RuntimeException` per input malformati.
*   **Logica di Parsing Insicura:** La conversione di stringhe a numeri nei mapper con valori di default nasconde potenziali errori del client.

### 3.3. Codice Obsoleto

*   **File Orfani:** La presenza di file vuoti o obsoleti (es. `PropertyCreatorFactory.java`) crea confusione per i nuovi sviluppatori.

## 4. Piano d'Azione Strategico Unificato

### Fase 1: Fondamenta - Test e Performance (Priorità Immediata)
1.  **Introdurre una Strategia di Test:** Adottare TDD per il nuovo codice e scrivere "Characterization Tests" per l'esistente prima di ogni refactoring.
2.  **Risolvere il Problema N+1:** Modificare tutte le relazioni in `FetchType.LAZY` e usare `JOIN FETCH` o `@EntityGraph` per il caricamento esplicito dei dati necessari.
3.  **Centralizzare e Disaccoppiare la Gestione delle Eccezioni:** Rimuovere tutti gli handler locali, rifattorizzare i servizi per lanciare eccezioni di dominio custom e consolidare la mappatura a risposte HTTP nel `GlobalExceptionHandler`.

### Fase 2: Rifattorizzazione Strutturale (Medio Termine)
4.  **Decomporre i "God Services":** Estrarre le responsabilità da `PropertyService` e `UserService` in classi più piccole e focalizzate (es. `PropertyFactory`, `UserManagementService`).
5.  **Semplificare la Creazione delle Proprietà:** Sostituire il pattern Visitor con un **Pattern Factory** o **Builder** che incapsuli l'intera logica di creazione in modo atomico.
6.  **Migliorare la Developer Experience:** Introdurre **Lombok** per eliminare il codice boilerplate e **MapStruct** per una mappatura DTO-Entità type-safe e automatizzata.
7.  **Disaccoppiare la Sicurezza:** Semplificare il payload del JWT al solo ID utente e implementare un `UserDetailsService` per caricare i dati freschi (ruoli inclusi) dal DB a ogni richiesta.

### Fase 3: Affinamento e Pulizia (Lungo Termine)
8.  **Arricchire il Modello di Dominio:** Spostare gradualmente la logica di business dai servizi alle entità per aumentare la coesione.
9.  **Rendere le API Type-Safe:** Rifattorizzare gli endpoint per usare DTO specifici nei tipi di ritorno e nelle richieste, eliminando `ResponseEntity<Object>` e `JsonNode`.
10. **Pulire la Configurazione:** Abilitare Flyway, impostare `ddl-auto=validate`, esternalizzare tutti i secret e rimuovere le dipendenze non necessarie.