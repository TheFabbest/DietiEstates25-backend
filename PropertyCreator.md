# Piano Strategico di Refactoring: Pattern PropertyCreator

## 1. Introduzione e Obiettivi

Questo documento delinea un piano strategico e operativo per il refactoring del pattern `PropertyCreator` all'interno del progetto DietiEstatesBackend.

### 1.1. Contesto del Problema

L'attuale implementazione del pattern `PropertyCreator` presenta una violazione del **Principio di Sostituzione di Liskov (LSP)**. Ogni `Creator` concreto (es. `ResidentialPropertyCreator`) riceve un `CreatePropertyRequest` generico e deve verificare il tipo di richiesta tramite `instanceof` ed eseguire un cast esplicito per accedere ai dati specifici della sottoclasse (es. `CreateResidentialPropertyRequest`).

```java
// Esempio problematico in ResidentialPropertyCreator.java
if (!(request instanceof CreateResidentialPropertyRequest)) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request payload for RESIDENTIAL property");
}
CreateResidentialPropertyRequest r = (CreateResidentialPropertyRequest) request;
// ... da qui in poi si usa 'r'
```

Questo approccio, sebbene funzionante, introduce diverse criticità:
- **Violazione LSP:** Una sottoclasse del `PropertyCreator` non può essere usata trasparentemente al posto della sua classe base senza causare errori o richiedere controlli di tipo.
- **Fragilità:** L'aggiunta di nuovi tipi di proprietà richiede la modifica di logica esistente e introduce il rischio di errori di cast.
- **Scarsa Manutenibilità:** Il codice è più difficile da leggere, comprendere e mantenere.
- **Accoppiamento Indesiderato:** I `Creator` sono accoppiati alla struttura interna dei DTO, rendendo il sistema rigido.

### 1.2. Obiettivi del Refactoring

L'obiettivo primario è **eliminare l'uso di `instanceof` e i cast espliciti** nei `Creator` concreti, ottenendo un design che rispetti i principi SOLID.

Gli obiettivi secondari includono:
- **Migliorare la Type Safety:** Sfruttare il sistema di tipi di Java per garantire la correttezza a compile-time.
- **Aumentare la Manutenibilità e la Scalabilità:** Rendere semplice e sicuro l'aggiunta di nuovi tipi di proprietà in futuro.
- **Ridurre l'Accoppiamento:** Disaccoppiare la logica di creazione dalla gerarchia dei DTO.
- **Preservare la Chiarezza:** Scegliere una soluzione che sia potente ma anche comprensibile per il team di sviluppo.

## 2. Analisi delle Soluzioni Proposte

Verranno analizzate due principali soluzioni alternative per risolvere il problema.

### Soluzione 1: Utilizzo dei Generics (Type-Safe Creators)

Questa soluzione introduce un parametro generico nell'interfaccia `PropertyCreator` per legare ogni implementazione concreta al suo specifico tipo di `CreatePropertyRequest`.

#### 2.1.1. Descrizione di Alto Livello

L'interfaccia `PropertyCreator` verrebbe modificata come segue:

```java
public interface PropertyCreator<T extends CreatePropertyRequest> {
    Property create(T request,
                    User agent,
                    Contract contract,
                    PropertyCategory category,
                    Address address,
                    EntityManager entityManager,
                    Validator validator);

    PropertyType supports();
}
```

Di conseguenza, un'implementazione concreta diventerebbe:

```java
@Component
public class ResidentialPropertyCreator implements PropertyCreator<CreateResidentialPropertyRequest> {
    @Override
    public Property create(CreateResidentialPropertyRequest request, User agent, /*...altri parametri...*/) {
        // Nessun bisogno di instanceof o cast!
        // 'request' è già del tipo corretto.
        ResidentialProperty rp = PropertyMapper.toResidentialEntity(request);
        // ... resto della logica ...
        return rp;
    }

    @Override
    public PropertyType supports() {
        return PropertyType.RESIDENTIAL;
    }
}
```

La `PropertyCreatorFactory` rimarrebbe quasi identica, ma il `PropertyService` dovrebbe eseguire un cast "sicuro" al momento della chiamata, poiché la factory non può conoscere il tipo generico specifico.

#### 2.1.2. Vantaggi

- **Type Safety a Compile-Time:** Il compilatore garantisce che ogni `Creator` riceva il tipo di DTO che si aspetta. Questo è il vantaggio più significativo.
- **Eliminazione di `instanceof` e Cast:** Il codice dei `Creator` diventa pulito, robusto e aderente al LSP.
- **Manutenibilità Elevata:** Aggiungere un nuovo tipo di proprietà è semplice e sicuro: basta creare un nuovo DTO, un nuovo `Creator` che implementa l'interfaccia con il tipo corretto e il sistema funzionerà senza modifiche alla logica esistente.
- **Chiarezza e Idiomaticità:** È una soluzione standard e ben compresa dagli sviluppatori Java.

#### 2.1.3. Svantaggi

- **Complessità nella Factory/Service:** Il `PropertyService`, che orchestra la chiamata, perde parzialmente la type safety. Deve invocare il `Creator` con un cast o usare un metodo helper "unsafe". A causa della *type erasure* dei generics in Java, non è possibile risolvere il tipo `T` a runtime in modo pulito all'interno della factory.
- **Cast Residuo:** Un cast (sebbene controllato e localizzato) sarà ancora necessario nel `PropertyService` prima di invocare il metodo `create`.

    ```java
    // Nel PropertyService
    PropertyCreator rawCreator = propertyCreatorFactory.getCreator(request.getPropertyType());
    // Questo cast è inevitabile con questo approccio
    @SuppressWarnings("unchecked")
    PropertyCreator<CreatePropertyRequest> castedCreator = (PropertyCreator<CreatePropertyRequest>) rawCreator;
    Property property = castedCreator.create(request, ...);
    ```

#### 2.1.4. Compromessi (Trade-offs)

- **Si guadagna** una totale type safety e pulizia all'interno dei `Creator`, che è dove risiede la logica di business più complessa e variabile.
- **Si perde** una parte della type safety nel punto di orchestrazione (`PropertyService`), concentrando l'unico punto "unsafe" del sistema in un unico, ben definito e controllato punto.

#### 2.1.5. Rischi Associati

- **Rischio Tecnico (Basso):** Un errore di logica nella `PropertyCreatorFactory` potrebbe associare un `PropertyType` al `Creator` sbagliato. Sebbene improbabile data l'implementazione attuale (basata su `Map` e `supports()`), questo causerebbe un `ClassCastException` a runtime nel `PropertyService`.

#### 2.1.6. Strategie di Mitigazione

- **Test Unitari Robusti:** Scrivere test unitari per la `PropertyCreatorFactory` che verifichino che per ogni `PropertyType` venga restituito il `Creator` corretto.
- **Documentazione:** Commentare chiaramente il punto del cast nel `PropertyService`, spiegando perché è necessario e considerato sicuro in quel contesto.

#### 2.1.7. Cosa Fare (Best Practices)

- **Localizzare il Cast:** Mantenere il cast "unsafe" solo all'interno del `PropertyService`, immediatamente prima di chiamare `creator.create()`.
- **Usare `@SuppressWarnings("unchecked")`:** Sopprimere l'warning del compilatore solo per quella specifica operazione di cast, aggiungendo un commento che ne spieghi la ragione.
- **Mantenere la Logica Semplice:** La `PropertyCreatorFactory` deve rimanere semplice e basarsi unicamente sul metodo `supports()` per la mappatura.

#### 2.1.8. Cosa Non Fare (Anti-patterns)

- **Evitare Cast Multipli:** Non spargere cast in giro per il codice. Il punto "unsafe" deve essere uno solo.
- **Non Tentare di Risolvere la Type Erasure:** Evitare soluzioni complesse (es. reflection, `TypeToken`) per eliminare l'unico cast nel service. Introdurrebbero più complessità di quanta ne risolverebbero.
- **Non Inserire Logica nella Factory:** La factory non deve fare altro che restituire il creator corretto.

### Soluzione 2: Visitor Pattern

Il Visitor Pattern è un pattern comportamentale che permette di separare un algoritmo dalla struttura di oggetti su cui opera. In questo caso, l'algoritmo è la "creazione di una `Property`" e la struttura di oggetti è la gerarchia dei `CreatePropertyRequest`.

#### 2.2.1. Descrizione di Alto Livello

Questa soluzione richiede di modificare la gerarchia dei DTO per renderla "visitabile" e di creare un `Visitor` che contenga la logica di creazione.

1.  **Modificare i DTO (Elementi Visitabili):**
    Si aggiunge un metodo `accept` all'interfaccia/classe base `CreatePropertyRequest` e a tutte le sue sottoclassi.

    ```java
    // In CreatePropertyRequest.java (diventa astratta)
    public abstract <R> R accept(RequestVisitor<R> visitor);

    // In CreateResidentialPropertyRequest.java
    @Override
    public <R> R accept(RequestVisitor<R> visitor) {
        return visitor.visit(this);
    }
    // ... e così via per gli altri DTO
    ```

2.  **Creare l'interfaccia Visitor:**
    Si definisce un'interfaccia `RequestVisitor` con un metodo `visit` per ogni tipo di DTO concreto.

    ```java
    public interface RequestVisitor<R> {
        R visit(CreateResidentialPropertyRequest request);
        R visit(CreateCommercialPropertyRequest request);
        R visit(CreateLandPropertyRequest request);
        R visit(CreateGaragePropertyRequest request);
    }
    ```

3.  **Implementare il Concrete Visitor:**
    La logica di tutti i `Creator` viene consolidata in un'unica classe, `PropertyCreationVisitor`, che implementa `RequestVisitor<Property>`.

    ```java
    public class PropertyCreationVisitor implements RequestVisitor<Property> {
        // ... dipendenze (agent, contract, entityManager, etc.) passate nel costruttore

        @Override
        public Property visit(CreateResidentialPropertyRequest request) {
            // Logica di creazione per Residential, 'request' è già del tipo corretto.
            ResidentialProperty rp = PropertyMapper.toResidentialEntity(request);
            // ...
            return rp;
        }

        @Override
        public Property visit(CreateCommercialPropertyRequest request) {
            // Logica per Commercial...
        }
        // ... altri metodi visit
    }
    ```

4.  **Orchestrare nel `PropertyService`:**
    Il `PropertyService` non usa più la `PropertyCreatorFactory`. Invece, istanzia il `PropertyCreationVisitor` e lo passa al metodo `accept` del DTO.

    ```java
    // Nel PropertyService
    // ... risolve agent, contract, etc.
    RequestVisitor<Property> visitor = new PropertyCreationVisitor(agent, contract, ...);
    Property property = request.accept(visitor); // Double-Dispatch
    // ...
    ```

#### 2.2.2. Vantaggi

- **Eliminazione Completa di Cast e `instanceof`:** È la soluzione più pulita dal punto di vista della type safety, eliminando ogni necessità di controllo di tipo manuale.
- **Aderenza a Open/Closed Principle (OCP):** È possibile aggiungere nuove operazioni (nuovi visitor) senza modificare i DTO. Per esempio, si potrebbe creare un `RequestValidationVisitor` per la validazione.
- **Centralizzazione della Logica:** La logica di creazione per tutti i tipi di proprietà è raggruppata in un'unica classe (`PropertyCreationVisitor`), il che può rendere più facile la comprensione dell'intero processo.
- **Disaccoppiamento:** La logica di creazione è completamente disaccoppiata dai DTO.

#### 2.2.3. Svantaggi

- **Violazione di OCP sui DTO:** L'aggiunta di un nuovo tipo di DTO (es. `CreateOfficePropertyRequest`) richiede la modifica dell'interfaccia `RequestVisitor` (aggiungendo un nuovo metodo `visit`) e di tutte le sue implementazioni concrete. Questo è il principale svantaggio del pattern.
- **Maggiore Complessità Iniziale:** Il pattern Visitor è più complesso da comprendere e implementare rispetto ai Generics, specialmente per chi non ha familiarità con esso. Introduce più classi e interfacce (il visitor, il metodo `accept`).
- **Logica Frammentata:** Sebbene la logica di creazione sia centralizzata, è distribuita su più metodi `visit` all'interno della stessa classe.

#### 2.2.4. Compromessi (Trade-offs)

- **Si guadagna** la possibilità di aggiungere nuove *operazioni* in modo pulito e una type safety assoluta.
- **Si perde** la facilità di aggiungere nuovi *tipi di dato*. L'architettura diventa più rigida rispetto all'aggiunta di nuove sottoclassi di `CreatePropertyRequest`.

#### 2.2.5. Rischi Associati

- **Rischio di Complessità (Medio):** Il team potrebbe trovare il pattern eccessivamente complesso per il problema da risolvere, aumentando i tempi di sviluppo e manutenzione futuri.
- **Rischio di "Visitor Pesanti" (Basso):** Il `PropertyCreationVisitor` potrebbe diventare una classe molto grande se la logica di creazione per ogni tipo è complessa.

#### 2.2.6. Strategie di Mitigazione

- **Formazione e Documentazione:** Assicurarsi che il team comprenda bene il Visitor Pattern. Documentare chiaramente il ruolo di ogni componente.
- **Refactoring del Visitor:** Se il `PropertyCreationVisitor` diventa troppo grande, si può valutare di dividerlo usando altri pattern (es. delegando a classi helper private).

#### 2.2.7. Cosa Fare (Best Practices)

- **Mantenere i Visitor Focalizzati:** Ogni visitor dovrebbe avere una sola responsabilità (es. creare, validare, serializzare).
- **Rendere i DTO Immutabili:** Se possibile, rendere i DTO e le loro proprietà `final` per evitare stati inconsistenti.
- **Usare un tipo di Ritorno Generico (`<R>`):** Questo rende il visitor riutilizzabile per operazioni che restituiscono tipi diversi (es. `Property`, `Boolean` per la validazione, `String` per la serializzazione).

#### 2.2.8. Cosa Non Fare (Anti-patterns)

- **Non Inserire Logica nel Metodo `accept`:** Il metodo `accept` deve contenere solo la chiamata `visitor.visit(this)`.
- **Non Abusare del Pattern:** Usare il Visitor solo quando si ha una gerarchia di tipi stabile e la necessità di eseguire operazioni diverse su di essa. Se si prevedono di aggiungere frequentemente nuovi tipi di DTO, questo pattern non è la scelta ideale.

## 3. Valutazione Comparativa e Raccomandazione

Entrambe le soluzioni risolvono efficacemente la violazione del LSP, ma lo fanno con approcci e compromessi differenti. La scelta dipende da quale aspetto della manutenibilità si vuole privilegiare: la facilità di aggiungere nuovi *tipi di dato* o nuovi *tipi di operazione*.

| Criterio | Soluzione 1: Generics | Soluzione 2: Visitor Pattern |
| :--- | :--- | :--- |
| **Type Safety** | Ottima (con un cast `unsafe` localizzato) | Eccellente (nessun cast) |
| **Aggiunta nuovo tipo di Proprietà** | **Molto Facile:** Si crea il DTO e il `Creator`, si registra nella factory. Nessuna modifica a codice esistente. | **Complesso:** Richiede la modifica dell'interfaccia `Visitor` e di tutte le sue implementazioni. |
| **Aggiunta nuova Operazione** | Complesso: Richiederebbe un'altra gerarchia di `Creator` (es. `PropertyValidator`). | **Molto Facile:** Si crea un nuovo `Visitor` (es. `RequestValidationVisitor`). Nessuna modifica ai DTO. |
| **Complessità Implementativa** | Bassa. È un'estensione naturale del design esistente. | Media/Alta. Introduce un pattern meno comune e più classi. |
| **Accoppiamento** | Il `Creator` è legato al suo DTO specifico. Il `Service` è debolmente accoppiato alla factory. | Il `Visitor` è accoppiato all'intera gerarchia dei DTO. I DTO sono accoppiati all'interfaccia `Visitor`. |
| **Aderenza a SOLID** | Risolve LSP. Buona aderenza a SRP e OCP (per i `Creator`). | Risolve LSP. Ottima aderenza a OCP (per le operazioni), ma lo viola per i tipi di dato. |

### 3.1. Raccomandazione

Considerando il contesto specifico del progetto DietiEstatesBackend, dove è più probabile che vengano aggiunti nuovi tipi di proprietà (o che quelli esistenti vengano modificati) piuttosto che operazioni completamente nuove sulla gerarchia dei DTO, la **Soluzione 1 (Utilizzo dei Generics) è fortemente raccomandata**.

**Motivazioni:**

1.  **Pragmatismo e Semplicità:** La soluzione con i Generics è un'evoluzione naturale dell'architettura esistente. È più semplice da implementare e da comprendere per il team, riducendo il rischio di errori e i tempi di sviluppo.
2.  **Flessibilità nel Contesto Giusto:** Ottimizza il caso d'uso più probabile: l'estensione della gerarchia delle proprietà. Il Visitor Pattern, al contrario, ottimizza per un caso d'uso (aggiungere nuove operazioni) che al momento non è richiesto e che potrebbe non verificarsi mai.
3.  **Rischio Contenuto:** L'unico punto "debole" (il cast nel `PropertyService`) è estremamente localizzato, facile da documentare, testare e gestire. Il rischio associato è significativamente inferiore rispetto all'introduzione di un pattern complesso che irrigidisce l'evoluzione della struttura dati principale.
4.  **Minore Impatto:** Il refactoring verso i Generics richiede modifiche meno invasive rispetto all'introduzione del Visitor, che impatterebbe l'intera gerarchia dei DTO.

In sintesi, la soluzione con i Generics offre il miglior equilibrio tra correttezza formale, manutenibilità pratica e complessità implementativa per le esigenze di questo progetto.

## 4. Piano Operativo (per Soluzione 1: Generics)

Di seguito, i passaggi concreti per implementare il refactoring usando i Generics.

1.  **Aggiornare `PropertyCreator.java`:**
    -   Introdurre il parametro generico `<T extends CreatePropertyRequest>`.
    -   Modificare la firma del metodo `create` per usare `T` come tipo del parametro `request`.

2.  **Aggiornare i `Creator` Concreti:**
    -   Per ogni implementazione (es. `ResidentialPropertyCreator`):
        -   Aggiornare la dichiarazione della classe per implementare `PropertyCreator<CreateResidentialPropertyRequest>`.
        -   Rimuovere il blocco `instanceof` e il cast.
        -   La firma del metodo `create` userà direttamente il DTO specifico.

3.  **Aggiornare `PropertyCreatorFactory.java`:**
    -   Non sono richieste modifiche dirette al codice della factory, ma bisogna essere consapevoli che ora lavora con tipi "raw".

4.  **Aggiornare `PropertyService.java`:**
    -   Nel metodo `createProperty`, localizzare la chiamata a `creator.create()`.
    -   Introdurre il cast controllato:
        ```java
        PropertyCreator rawCreator = propertyCreatorFactory.getCreator(request.getPropertyType());
        @SuppressWarnings("unchecked")
        PropertyCreator<CreatePropertyRequest> castedCreator = (PropertyCreator<CreatePropertyRequest>) rawCreator;
        Property property = castedCreator.create(request, agent, contract, category, address, entityManager, validator);
        ```
    -   Aggiungere un commento per spiegare il cast.

5.  **Eseguire Test:**
    -   Lanciare tutti i test esistenti per assicurarsi che non ci siano regressioni.
    -   Scrivere nuovi test unitari per la `PropertyCreatorFactory` per verificare la corretta mappatura `PropertyType` -> `Creator`.
    -   Eseguire test di integrazione per il servizio `createProperty` con tutti i tipi di proprietà supportati.

### Soluzione 3: Composizione nei DTO e Dispatcher Interno (Switch-based)

Questa soluzione elimina la gerarchia di ereditarietà dei DTO `CreatePropertyRequest` a favore di un unico DTO che usa la composizione. La logica di creazione, invece di essere delegata a `Creator` esterni, viene gestita da metodi privati all'interno del `PropertyService`, selezionati tramite uno `switch` sul `propertyType`.

#### 2.3.1. Descrizione di Alto Livello

1.  **Refactoring dei DTO:**
    Si elimina l'ereditarietà. Esisterà un'unica classe `CreatePropertyRequest` che conterrà campi comuni e oggetti nidificati opzionali per i dati specifici.

    ```java
    public class CreatePropertyRequest {
        @NotNull
        private PropertyType propertyType;
        // ... tutti i campi comuni (description, price, area, etc.)

        // Dati specifici, opzionali (validati a livello di servizio)
        private ResidentialDetails residentialDetails;
        private CommercialDetails commercialDetails;
        // ... e così via per LAND e GARAGE

        // Getters e Setters...
    }

    // Esempio di classe nidificata (semplice POJO)
    public class ResidentialDetails {
        private Integer numberOfRooms;
        private Integer numberOfBathrooms;
        // ...
    }
    ```
    Le annotazioni `@JsonTypeInfo` e `@JsonSubTypes` vengono rimosse.

2.  **Refactoring del `PropertyService`:**
    Le classi `PropertyCreator` e `PropertyCreatorFactory` vengono eliminate. La logica viene spostata in metodi privati e type-safe all'interno del `PropertyService`.

    ```java
    // Nel PropertyService
    public PropertyResponse createProperty(CreatePropertyRequest request) {
        // ... risoluzione delle dipendenze comuni (agent, address, etc.)

        Property property;
        switch (request.getPropertyType()) {
            case RESIDENTIAL:
                // Validazione: assicura che residentialDetails non sia nullo
                if (request.getResidentialDetails() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "residentialDetails is required for RESIDENTIAL property");
                }
                property = createSpecificResidentialProperty(request, request.getResidentialDetails(), agent, ...);
                break;
            case COMMERCIAL:
                // ... logica simile per commercial
                break;
            // ... altri casi
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported property type: " + request.getPropertyType());
        }

        // ... logica comune di salvataggio
        return PropertyMapper.toResponse(propertyRepository.save(property));
    }

    // Metodo privato e type-safe
    private ResidentialProperty createSpecificResidentialProperty(
            CreatePropertyRequest commonRequest,
            ResidentialDetails details,
            User agent, Contract contract, ...) {

        // La logica qui è pulita, non ha bisogno di cast.
        // Usa 'commonRequest' per i dati comuni e 'details' per quelli specifici.
        ResidentialProperty rp = new ResidentialProperty();
        // ... mapping da commonRequest e details ...
        return rp;
    }
    ```

#### 2.3.2. Vantaggi

- **Massima Semplicità:** È la soluzione più facile da capire. Non introduce pattern complessi (Visitor) né le sottigliezze della type erasure (Generics).
- **Eliminazione di Classi:** Riduce il numero di classi nel progetto eliminando l'intera gerarchia dei `Creator` e la `Factory`.
- **Centralizzazione della Logica:** Tutta la logica di creazione è contenuta nel `PropertyService`. Questo può rendere più facile seguire il flusso di creazione dall'inizio alla fine.
- **Type-Safe nei Metodi Privati:** La logica di business specifica opera su DTO fortemente tipizzati (`ResidentialDetails`), eliminando i cast.

#### 2.3.3. Svantaggi

- **Violazione del Principio Open/Closed (OCP):** Questo è lo svantaggio principale. Aggiungere un nuovo `PropertyType` richiede di **modificare** il `PropertyService` per aggiungere un nuovo `case` allo `switch` e un nuovo metodo privato. Il sistema non è "chiuso" alla modifica.
- **Potenziale "God Class":** Il `PropertyService` rischia di diventare molto grande e di violare il Single Responsibility Principle (SRP) se la logica di creazione per ogni tipo è complessa.
- **DTO potenzialmente Gonfio:** La classe `CreatePropertyRequest` potrebbe diventare grande e contenere molti campi `null`, a seconda del numero di tipi di proprietà.

#### 2.3.4. Compromessi (Trade-offs)

- **Si guadagna** semplicità e immediatezza a livello di codice.
- **Si perde** la flessibilità e l'estensibilità. Il design diventa più rigido e richiede modifiche dirette per ogni nuova estensione.

#### 2.3.5. Rischi Associati

- **Rischio di Manutenibilità (Alto):** Con l'aumentare dei tipi di proprietà, lo `switch` diventerà sempre più grande e complesso, aumentando il rischio di introdurre bug durante le modifiche.
- **Rischio di Validazione Dimenticata (Basso):** Uno sviluppatore potrebbe dimenticare di aggiungere il controllo `if (details == null)` nel nuovo `case` dello `switch`, portando a `NullPointerException`.

#### 2.3.6. Strategie di Mitigazione

- **Test Unitari Esaustivi:** Il `PropertyService` deve avere una copertura di test molto alta, con un test per ogni `case` dello `switch` e per i casi di default e di dati mancanti.
- **Mantenere i Metodi Privati Piccoli:** La logica all'interno dei metodi `createSpecific...` deve essere il più semplice possibile. Logiche complesse andrebbero estratte in classi helper private.

#### 2.3.7. Cosa Fare (Best Practices)

- **Usare questo pattern se il numero di tipi è piccolo e stabile.** È una soluzione pragmatica se non si prevede una crescita continua.
- **Validare sempre l'input:** Controllare sempre che l'oggetto `details` specifico non sia nullo prima di passarlo al metodo privato.
- **Raggruppare la logica:** Tenere i metodi privati `createSpecific...` vicini al metodo `createProperty` principale per migliorare la leggibilità.

#### 2.3.8. Cosa Non Fare (Anti-patterns)

- **Non usare questo approccio se si prevede di aggiungere frequentemente nuovi tipi di proprietà.** La manutenzione diventerebbe rapidamente un incubo.
- **Evitare di mettere logica complessa direttamente nei `case` dello `switch`.** Delegare sempre a metodi privati ben definiti.