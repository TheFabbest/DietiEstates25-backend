# Piano d'Azione Consolidato per la Rifattorizzazione di DietiEstatesBackend

## 1. Introduzione

Questo documento delinea il piano strategico e operativo per la rifattorizzazione del backend del progetto DietiEstates. L'obiettivo è risolvere il debito tecnico identificato, migliorare la manutenibilità, la scalabilità e la robustezza del codice, e allineare l'architettura alle moderne best practice di sviluppo software.

Il piano è il risultato della consolidazione di una code review approfondita e di un'analisi architetturale, e definisce una sequenza logica di interventi per minimizzare i rischi e massimizzare l'impatto positivo.

**Principi Guida:**
*   **SOLID:** Ogni modifica mirerà a rafforzare i principi SOLID, in particolare la Single Responsibility (SRP) e l'Open/Closed Principle (OCP).
*   **DRY (Don't Repeat Yourself):** Verrà eliminata la duplicazione di codice e logica.
*   **Chiarezza e Semplicità:** L'architettura verrà semplificata per essere più facile da comprendere e manutenere.
*   **Sfruttamento Idiomatico degli Strumenti:** Verranno utilizzate appieno le funzionalità di framework e librerie come Spring, Jackson e MapStruct.

---

## 2. Piano Operativo Dettagliato

### **Fase 1: Pulizia e Preparazione Iniziale**

*   **Obiettivo:** Rimuovere codice obsoleto e ridondante per preparare il terreno a modifiche più significative.
*   **Task 1.1: Eliminare `AuthService.java`**
    *   **Azione:** Rimuovere il file `src/main/java/com/dieti/dietiestatesbackend/service/AuthService.java` dal progetto, poiché è deprecato.
*   **Task 1.2: Rimuovere Getter/Setter Ridondanti**
    *   **Azione:** In `Property.java` e nelle sue sottoclassi, rimuovere tutti i metodi getter e setter espliciti, affidandosi completamente alle annotazioni Lombok (`@Getter`, `@Setter`) già presenti.

---

### **Fase 2: Rifattorizzazione del Flusso di Creazione Proprietà**

*   **Obiettivo:** Ristrutturare completamente il flusso di creazione delle proprietà per renderlo typesafe, robusto e manutenibile, centralizzando le responsabilità in modo logico.

#### **Sotto-fase 2.1: Semplificazione della Deserializzazione dei DTO**

*   **Razionale:** L'attuale `CreatePropertyRequestDeserializer` viola il SRP, è accoppiato con Spring e rende la manutenzione un incubo. Verrà sostituito con le funzionalità native di Jackson.
*   **Task 2.1.1: Eliminare il Deserializzatore Custom**
    *   **Azione:** Rimuovere il file `CreatePropertyRequestDeserializer.java`.
*   **Task 2.1.2: Implementare Deserializzazione Polimorfica con Jackson**
    *   **Azione:** Modificare `CreatePropertyRequest` per renderla una `sealed interface`.
    *   **Azione:** Annotare `CreatePropertyRequest` con `@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "propertyType")` e `@JsonSubTypes({...})`.
    *   **Contratto API:** Il client dovrà includere un campo `propertyType` (es. "RESIDENTIAL", "COMMERCIAL") nel payload JSON per permettere a Jackson di istanziare la sottoclasse corretta.
    *   **Azione:** Assicurarsi che tutte le classi che implementano `CreatePropertyRequest` siano dichiarate come `final` o `sealed`.

#### **Sotto-fase 2.2: Rifattorizzazione della Struttura dei DTO**

*   **Razionale:** Eliminare la duplicazione di campi tra i DTO.
*   **Task 2.2.1: Introdurre Astrazione per Immobili Edificati**
    *   **Azione:** Creare una `public sealed abstract class CreateBuildingPropertyRequest` che estende `CreatePropertyRequest` e contiene campi comuni come `numberOfRooms`, `yearBuilt`, etc.
    *   **Azione:** Modificare `CreateResidentialPropertyRequest` e `CreateCommercialPropertyRequest` affinché estendano `CreateBuildingPropertyRequest`.
*   **Task 2.2.2: Risolvere Ambiguità Indirizzo**
    *   **Azione:** Aggiungere una validazione custom (`@AssertTrue`) su `CreatePropertyRequest` che assicuri che solo uno tra `addressId` e `addressRequest` sia fornito, restituendo un errore 400 in caso contrario.

#### **Sotto-fase 2.3: Ottimizzazione del Mapper e del Service**

*   **Razionale:** Centralizzare tutta la logica di mapping in MapStruct e semplificare drasticamente il service.
*   **Task 2.3.1: Potenziare `PropertyCreationMapper`**
    *   **Azione:** Annotare l'interfaccia `PropertyCreationMapper` con `@Mapper(componentModel = "spring", uses = {AgentLookupService.class, CategoryLookupService.class, ContractLookupService.class})`.
    *   **Azione:** Sfruttare la `uses` clause per mappare direttamente gli ID/nomi a entità complete (es. `@Mapping(source = "agentUsername", target = "agent")`). MapStruct invocherà automaticamente i metodi di lookup necessari.
*   **Task 2.3.2: Eliminare `PropertyDependencyResolver`**
    *   **Azione:** Una volta che il mapper gestisce i lookup, il componente `PropertyDependencyResolver` diventa obsoleto e deve essere eliminato.
*   **Task 2.3.3: Snellire `PropertyCreationService`**
    *   **Azione:** Rimuovere tutta la logica di mapping manuale e le chiamate al resolver.
    *   **Azione:** Il metodo `createProperty` dovrà solo ricevere il DTO, passarlo al mapper per ottenere un'entità completa e persisterla. Il pattern matching `switch` sui DTO `sealed` rimane per la logica di business specifica (se necessaria) o per una semplice chiamata al metodo di mapping corretto.

---

### **Fase 3: Rifattorizzazione Mapping Entità -> DTO di Risposta**

*   **Obiettivo:** Disaccoppiare il dominio dalla presentazione, eliminando il pattern Visitor.
*   **Task 3.1: Rimuovere il Pattern Visitor**
    *   **Azione:** Eliminare il metodo `accept(PropertyVisitor visitor)` da `Property.java` e sottoclassi.
    *   **Azione:** Eliminare l'interfaccia `PropertyVisitor` e le sue implementazioni (es. `ResponseBuildingVisitor`).
*   **Task 3.2: Potenziare `PropertyMapper` con Mapping Polimorfico**
    *   **Azione:** Annotare il metodo `toResponse(Property property)` in `PropertyMapper` con `@SubclassMapping` per ogni sottoclasse di `Property` (es. `@SubclassMapping(source = ResidentialProperty.class, target = ResidentialPropertyResponse.class)`).
*   **Task 3.3: Aggiornare i Punti di Utilizzo**
    *   **Azione:** Modificare `PropertiesController` e ogni altro punto del codice per utilizzare direttamente `propertyMapper.toResponse(property)`.

---

### **Fase 4: Riorganizzazione e Naming dei Controller**

*   **Obiettivo:** Migliorare la Single Responsibility e la chiarezza semantica degli endpoint.
*   **Task 4.1: Creare Controller Specifici**
    *   **Azione:** Creare `PropertyLookupController` e spostarvi gli endpoint `GET /api/property-types` e `GET /api/categories`.
    *   **Azione:** Creare `PropertyImageController` e spostarvi l'endpoint `GET /thumbnails/{id}`.
*   **Task 4.2: Rinominare l'Endpoint di Creazione**
    *   **Azione:** In `PropertiesController`, rinominare l'endpoint `POST /properties` in `POST /properties/create` per maggiore chiarezza semantica.

---

### **Fase 5: Gestione Errori e Finalizzazione**

*   **Obiettivo:** Garantire una gestione degli errori robusta e coerente.
*   **Task 5.1: Correggere Gestione Errori in `getThumbnails`**
    *   **Azione:** Modificare il metodo (ora in `PropertyImageController`) per verificare l'esistenza della risorsa (`resource.exists()`) e restituire `404 Not Found` se assente.
    *   **Azione:** Includere un blocco `try-catch` per `MalformedURLException` che restituisca `500 Internal Server Error` con un messaggio di log adeguato.

---

## 4. Conclusione

L'esecuzione di questo piano porterà a una codebase significativamente più pulita, robusta e manutenibile. Ogni fase è progettata per essere un passo logico e incrementale, riducendo i rischi di regressione e fornendo valore tangibile a ogni completamento.