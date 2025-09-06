# Piano Strategico di Miglioramento Qualità Codice: DietiEstatesBackend

**Data:** 31 Agosto 2025

## 1. Introduzione

Questo documento espande il piano d'azione derivato dal report di code review del 31/08/2025. L'obiettivo non è solo elencare le azioni correttive, ma fornire un'analisi strategica per ogni decisione, evidenziando le motivazioni tecniche, i compromessi (trade-off) e le alternative considerate. Lo scopo è trasformare la codebase in un sistema più robusto, manutenibile e leggibile, che possa evolvere in modo sostenibile.

Il piano è suddiviso in tre fasi prioritarie, dalla più critica alla meno urgente.

---

## 2. Piano d'Azione Strategico Unificato

Il piano è stato riorganizzato per riflettere le priorità emerse dal report consolidato, applicando un'analisi strategica dettagliata a ogni punto d'azione. I criteri di valutazione oggettivi per ogni decisione includono: **Robustezza, Manutenibilità, Performance, Testabilità, e Developer Experience.**

---

### Fase 1: Fondamenta - Test, Performance e Gestione Errori (Priorità Immediata)

Questa fase affronta i rischi sistemici che minano la stabilità e la scalabilità dell'applicazione.

#### 2.1. Introduzione di una Strategia di Test Multi-livello

*   **Problema:** L'assenza totale di test automatici rende ogni modifica, incluso il refactoring, estremamente rischiosa. È impossibile verificare la non-regressione in modo efficiente e affidabile, compromettendo la robustezza e la manutenibilità.
*   **Soluzione Raccomandata:** Implementare una strategia di test piramidale (Integrazione, Unitari, API). Adottare TDD per il nuovo codice e scrivere "Characterization Tests" per l'esistente prima di ogni modifica.
*   **Motivazione:** Questa strategia fornisce una "rete di sicurezza" che massimizza la fiducia nel codice e abilita il refactoring sicuro. Migliora drasticamente la **Testabilità** e, di conseguenza, la **Robustezza** del sistema, poiché il codice viene scritto per essere intrinsecamente più modulare.
*   **Trade-off:** Richiede un investimento di tempo iniziale per la configurazione e la scrittura dei primi test, rallentando temporaneamente lo sviluppo di nuove feature. Questo è un debito tecnico che, una volta saldato, accelera lo sviluppo a lungo termine.
*   **Alternative Considerate:**
    *   **Alternativa 1: Solo test End-to-End (E2E).** Lenti, fragili e difficili da debuggare. Non forniscono un feedback rapido e preciso per localizzare i bug.
    *   **Alternativa 2: Solo test unitari.** Veloci ma insufficienti. Non verificano l'integrazione tra i componenti, lasciando scoperte aree critiche come l'interazione con il database.

#### 2.2. Risoluzione del Problema N+1 Query

*   **Problema:** L'uso sistematico di `fetch = FetchType.EAGER` causa un degrado drastico delle **Performance** e rende l'applicazione non scalabile.
*   **Soluzione Raccomandata:** Modificare tutte le relazioni in `FetchType.LAZY` e usare `JOIN FETCH` o `@EntityGraph` per il caricamento esplicito e controllato dei dati necessari in ogni specifico caso d'uso.
*   **Motivazione:** Elimina un collo di bottiglia critico, garantendo che l'applicazione possa scalare. Migliora le **Performance** in modo significativo e costringe a un design delle query più intenzionale.
*   **Trade-off:** Aumenta la complessità della scrittura delle query. Gli sviluppatori devono essere consapevoli del rischio di `LazyInitializationException` e gestire attivamente il fetching dei dati.
*   **Alternative Considerate:**
    *   **Alternativa 1: Usare DTO Projections.** Ottimo per le performance, ma aumenta il boilerplate e la complessità della mappatura.
    *   **Alternativa 2: Mantenere EAGER e usare `@BatchSize`.** Mitiga il problema ma non lo risolve alla radice; può comunque portare a un over-fetching di dati non necessari.

#### 2.3. Centralizzazione e Disaccoppiamento della Gestione delle Eccezioni

*   **Problema:** La gestione degli errori è inconsistente, viola i confini tra layer (servizi che lanciano eccezioni web) e non fornisce feedback chiari, danneggiando **Robustezza** e **Manutenibilità**.
*   **Soluzione Raccomandata:** Rimuovere tutti gli handler locali. Rifattorizzare i servizi per lanciare eccezioni di dominio custom. Consolidare tutta la mappatura eccezione-risposta HTTP nel `GlobalExceptionHandler`.
*   **Motivazione:** Rispetta il principio DRY, garantisce risposte di errore uniformi e pulite, e disaccoppia la logica di business dall'infrastruttura web, migliorando la **Manutenibilità**.
*   **Trade-off:** Nessun trade-off significativo. Questa è una best practice consolidata che porta solo benefici in termini di pulizia del codice.
*   **Alternative Considerate:**
    *   **Alternativa 1: Gestione locale in ogni controller.** Causa duplicazione di codice, risposte eterogenee e rende facile dimenticare di gestire un'eccezione.
    *   **Alternativa 2: Usare un oggetto `Result` o `Either`.** Un pattern funzionale potente ma che introduce una complessità e uno stile di programmazione non idiomatico per molti sviluppatori Java/Spring.

---

### Fase 2: Rifattorizzazione Strutturale e Developer Experience (Medio Termine)

#### 2.4. Decomposizione dei "God Services" (SRP)

*   **Problema:** Classi monolitiche come `PropertyService` violano il Single Responsibility Principle, risultando in bassa coesione, alto accoppiamento e scarsa **Testabilità**.
*   **Soluzione Raccomandata:** Estrarre le responsabilità in classi più piccole e focalizzate (es. `PropertyFactory`, `PropertySearchService`), seguendo il principio di singola responsabilità.
*   **Motivazione:** Aumenta la coesione e la **Manutenibilità**. Rende il codice più facile da comprendere, testare e modificare senza effetti collaterali inattesi.
*   **Trade-off:** Aumenta il numero di classi nel progetto, ma questo è un compromesso positivo per una maggiore chiarezza e separazione delle responsabilità.
*   **Alternative Considerate:**
    *   **Alternativa 1: Mantenere i "Fat Services".** Soluzione più rapida nel breve termine, ma porta a un debito tecnico insostenibile.
    *   **Alternativa 2: Usare un Command Bus.** Pattern molto potente per il disaccoppiamento, ma probabilmente eccessivo per la complessità attuale del sistema.

#### 2.5. API Type-Safe e Auto-documentanti

*   **Problema:** L'uso di `JsonNode` e `ResponseEntity<Object>` bypassa la type-safety, nasconde i contratti API e aumenta il rischio di `RuntimeException`, minando la **Robustezza**.
*   **Soluzione Raccomandata:** Rifattorizzare l'endpoint di creazione proprietà per usare la deserializzazione polimorfica di Jackson con DTO specifici. Sostituire tutti i tipi di ritorno generici con DTO fortemente tipizzati.
*   **Motivazione:** Ripristina la validazione a compile-time, rende le API robuste, auto-documentanti e più facili da usare per i client. Migliora la **Developer Experience** sia per il backend che per il frontend.
*   **Trade-off:** Aumento del numero di classi DTO. Questo è un compromesso accettabile per la robustezza e chiarezza guadagnate.
*   **Alternative Considerate:**
    *   **Alternativa 1: Mantenere `JsonNode` con validazione manuale.** Fragile, complesso e soggetto a errori. Si finirebbe per riscrivere un sistema di validazione già offerto gratuitamente dal framework.
    *   **Alternativa 2: Endpoint separati per tipo (`/properties/residential`).** Molto esplicito, ma può portare a duplicazione di codice nel controller e viola parzialmente il principio Open/Closed.

#### 2.6. Miglioramento della Developer Experience (Lombok)

*   **Problema:** Le classi sono appesantite da codice boilerplate scritto manualmente, che riduce la leggibilità e aumenta la probabilità di errori.
*   **Soluzione Raccomandata:** Introdurre **Lombok** per generare il boilerplate (`@Data`, `@Builder`, etc.) a compile-time.
*   **Motivazione:** Riduce drasticamente il codice verboso, migliora la **Developer Experience** e la **Manutenibilità** rendendo le classi più leggibili.
*   **Trade-off:** Richiede l'installazione di un plugin nell'IDE e introduce un elemento di "magia" (manipolazione del bytecode), ma è uno standard de-facto nell'ecosistema Java.
*   **Alternative Considerate:**
    *   **Alternativa 1: Usare Java Records per i DTO.** Ottima scelta per i DTO, ma non adatti per le entità JPA. Una strategia ibrida (Records per DTO, Lombok per Entità) è la soluzione ideale.
    *   **Alternativa 2: Scrivere tutto a mano.** Causa del problema attuale. Verboso, soggetto a errori e inefficiente.

---

### Fase 3: Affinamento e Pulizia (Lungo Termine)

#### 3.1. Disaccoppiamento della Sicurezza

*   **Problema:** Il token JWT contiene dati (es. ruoli) che possono diventare obsoleti, creando potenziali falle di sicurezza.
*   **Soluzione Raccomandata:** Semplificare il payload del JWT al solo ID utente. Implementare un `UserDetailsService` per caricare i dati freschi (ruoli inclusi) dal database a ogni richiesta.
*   **Motivazione:** Aumenta la **Sicurezza** e la coerenza dei dati, eliminando il rischio di utilizzare permessi obsoleti.
*   **Trade-off:** Introduce una chiamata al database per ogni richiesta autenticata. Questo impatto sulle **Performance** è generalmente trascurabile e può essere mitigato con una cache di secondo livello (es. Caffeine, Redis).
*   **Alternative Considerate:**
    *   **Alternativa 1: Mantenere i ruoli nel token.** Più performante, ma insicuro. Adatto solo per sistemi in cui i permessi cambiano molto raramente.
    *   **Alternativa 2: Meccanismo di revoca dei token.** Molto più complesso da implementare (richiede una blacklist, es. in Redis) e probabilmente eccessivo per le esigenze attuali.

#### 3.2. Pulizia della Configurazione e Codice Obsoleto

*   **Problema:** La configurazione di `ddl-auto=update` è rischiosa e sono presenti file orfani, riducendo l'affidabilità e la **Manutenibilità**.
*   **Soluzione Raccomandata:** Abilitare Flyway per una gestione controllata delle migrazioni, impostare `ddl-auto=validate` per sicurezza, esternalizzare i secret e rimuovere codice non utilizzato.
*   **Motivazione:** Garantisce la stabilità e la sicurezza dell'ambiente di produzione e mantiene la codebase pulita e comprensibile per i nuovi sviluppatori.
*   **Trade-off:** Nessun trade-off. Si tratta di allinearsi a best practice consolidate per la gestione di applicazioni in produzione.
*   **Alternative Considerate:** Nessuna alternativa ragionevole. L'approccio raccomandato è lo standard industriale per la gestione dello schema del database in applicazioni Spring Boot.

## 5. Conclusione

L'implementazione di questo piano strategico, seguendo l'ordine di priorità definito, trasformerà la codebase di DietiEstatesBackend. Si passerà da un sistema fragile e difficile da modificare a uno robusto, testabile e manutenibile. L'investimento iniziale nel setup dei test e nel refactoring strutturale sarà ampiamente ripagato dalla riduzione del tempo di debugging, dalla maggiore fiducia nelle modifiche e da una maggiore velocità di sviluppo a lungo termine.