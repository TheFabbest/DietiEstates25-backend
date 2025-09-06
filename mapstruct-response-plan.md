# Piano Architetturale: `PropertyResponseMapper` con Sealed Classes e Pattern Matching

## 1. Introduzione e Obiettivi

Questo documento delinea un piano architetturale per la creazione di un nuovo mapper MapStruct, `PropertyResponseMapper`, dedicato alla trasformazione delle entità `Property` in Data Transfer Objects (DTO) per le risposte API.

L'approccio precedente, basato sul `ResponseBuildingVisitor`, viene scartato a favore di una soluzione più moderna e robusta che sfrutta le **Sealed Classes** e il **Pattern Matching** di Java (introdotte in JEP 360 e migliorate nelle versioni successive).

**Obiettivi Principali:**

*   **Uniformare il Mapping:** Creare un unico punto di accesso per mappare qualsiasi sottotipo di `Property` al suo DTO corrispondente.
*   **Migliorare la Type-Safety:** Sfruttare il compilatore per garantire che tutti i sottotipi di `Property` siano gestiti, eliminando la possibilità di errori a runtime dovuti a tipi non considerati.
*   **Ridurre il Boilerplate:** Eliminare la necessità del pattern Visitor e delle logiche di dispatch manuali.
*   **Aumentare la Manutenibilità:** Semplificare l'aggiunta di nuovi tipi di proprietà in futuro.
*   **Aderire a Principi di Design Moderni:** Adottare feature recenti del linguaggio Java per un codice più pulito, leggibile ed efficiente.

## 2. Modifica della Gerarchia `Property` (Sealed Class)

Il primo passo fondamentale è modificare la gerarchia delle entità `Property` per renderla una `sealed class`. Questo cambiamento è il pilastro su cui si fonda l'intero approccio.

*   **File da modificare:** `src/main/java/com/dieti/dietiestatesbackend/entities/Property.java`
*   **Modifica:** La classe `Property` sarà trasformata in una `sealed class` o `interface`.

```java
// Esempio di implementazione in Property.java
public sealed abstract class Property permits ResidentialProperty, CommercialProperty, Land, Garage {
    // ... campi e metodi comuni
}
```

*   **Sottotipi Permessi:** I sottotipi `ResidentialProperty`, `CommercialProperty`, `Land`, e `Garage` dovranno essere dichiarati come `final` o `sealed` a loro volta e dovranno estendere la classe `Property`.

```java
// Esempio per ResidentialProperty.java
public final class ResidentialProperty extends Property {
    // ...
}

// Esempio per CommercialProperty.java
public final class CommercialProperty extends Property {
    // ...
}

// E così via per Land e Garage
```

## 3. Definizione del Nuovo Mapper: `PropertyResponseMapper`

Verrà creato un nuovo mapper MapStruct che centralizzerà la logica di conversione da entità a DTO.

*   **Nome File:** `PropertyResponseMapper.java`
*   **Location:** `src/main/java/com/dieti/dietiestatesbackend/mappers/PropertyResponseMapper.java`
*   **Annotazioni:** `@Mapper(componentModel = "spring")`. Non includerà `uses = {ResponseBuildingVisitor.class}`.

### Metodi Chiave del Mapper

1.  **Metodo di Dispatching Principale (`toPropertyResponse`)**
    Questo sarà un `default method` nell'interfaccia che utilizza un'espressione `switch` con pattern matching per delegare al mapper corretto.

    ```java
    @Mapper(componentModel = "spring")
    public interface PropertyResponseMapper {

        default PropertyResponse toPropertyResponse(Property property) {
            return switch (property) {
                case ResidentialProperty res -> toResidentialResponse(res);
                case CommercialProperty com -> toCommercialPropertyResponse(com);
                case Land land -> toLandResponse(land);
                case Garage garage -> toGarageResponse(garage);
            };
        }
        // ... altri metodi
    }
    ```
    Questo approccio garantisce che se un nuovo sottotipo di `Property` viene aggiunto, il compilatore segnalerà un errore nello `switch` se non viene gestito, forzando l'esaustività.

2.  **Metodi di Mapping Specifici per Sottotipo**
    Per ogni sottotipo, ci sarà un metodo di mapping dedicato. MapStruct implementerà automaticamente questi metodi. Saranno dichiarati con visibilità `public` per essere accessibili dal `default method`.

    ```java
    // All'interno dell'interfaccia PropertyResponseMapper

    ResidentialPropertyResponse toResidentialResponse(ResidentialProperty residential);

    CommercialPropertyResponse toCommercialPropertyResponse(CommercialProperty commercial);

    LandResponse toLandResponse(Land land);

    GarageResponse toGarageResponse(Garage garage);
    ```
    MapStruct gestirà il mapping dei campi con nomi identici. Per campi con nomi diversi o logiche semplici, si userà l'annotazione `@Mapping`.

3.  **Metodo per Mappare Liste (`toPropertyResponseList`)**
    Un metodo per gestire la conversione di liste di proprietà, che itererà e applicherà la logica di dispatching definita in `toPropertyResponse`.

    ```java
    // All'interno dell'interfaccia PropertyResponseMapper
    List<PropertyResponse> toPropertyResponseList(List<Property> properties);
    ```

## 4. Processo di Deprecazione e Sostituzione

La migrazione dal vecchio `PropertyMapper` al nuovo `PropertyResponseMapper` avverrà in 4 fasi controllate per minimizzare i rischi.

1.  **Fase 1: Creazione e Implementazione**
    *   Modificare la gerarchia `Property` come descritto nella Sezione 2.
    *   Creare e implementare il nuovo `PropertyResponseMapper` come descritto nella Sezione 3.
    *   Scrivere test unitari per il nuovo mapper per garantirne il corretto funzionamento.

2.  **Fase 2: Sostituzione delle Chiamate**
    *   Identificare tutte le occorrenze in cui `PropertyMapper` (e di conseguenza `ResponseBuildingVisitor`) viene utilizzato per il mapping entità -> DTO.
    *   Sostituire le chiamate a `propertyMapper.toPropertyResponse(property)` con `propertyResponseMapper.toPropertyResponse(property)`.
    *   I componenti principali interessati saranno `PropertiesController` e `PropertyManagementService`.

3.  **Fase 3: Deprecazione Formale**
    *   Una volta che tutte le chiamate sono state sostituite e i test di integrazione passano, annotare la classe `PropertyMapper` e il `ResponseBuildingVisitor` con `@Deprecated`.
    *   Questo serve come avviso per gli sviluppatori che queste classi non devono più essere utilizzate.

4.  **Fase 4: Eliminazione**
    *   Dopo un periodo di consolidamento (es. un ciclo di sprint), i file `PropertyMapper.java` e `ResponseBuildingVisitor.java` possono essere eliminati in sicurezza dal codebase.

## 5. Impatto su Altri Componenti

*   **Controller (`PropertiesController`)**: Inietterà e utilizzerà `PropertyResponseMapper` invece di `PropertyMapper` per convertire le entità `Property` recuperate dal servizio nei DTO di risposta.
*   **Servizi (`PropertyManagementService`, `PropertyQueryService`)**: Qualsiasi servizio che attualmente restituisce un DTO di `Property` dovrà essere aggiornato per utilizzare il nuovo `PropertyResponseMapper`.

## 6. Strategia di Test

*   **Test Unitari**: Saranno cruciali per il `PropertyResponseMapper`. Si dovrà creare un test per ogni ramo del `switch` nel pattern matching, assicurando che ogni sottotipo di `Property` sia mappato correttamente al suo DTO specifico.
*   **Test di Integrazione**: I test esistenti per gli endpoint API (es. `GET /properties/{id}`) dovranno essere eseguiti per verificare che non ci siano regressioni. Questi test confermeranno che l'integrazione tra controller, servizio e il nuovo mapper funzioni come previsto.

## 7. Analisi dei Compromessi (Trade-off)

### Vantaggi

*   **Sicurezza a Tempo di Compilazione:** Il vantaggio più grande. Il pattern matching esaustivo sulle `sealed classes` previene errori a runtime dovuti a nuovi tipi di proprietà non gestiti.
*   **Codice Semplificato e Leggibile:** La logica di dispatch è contenuta in un'espressione `switch` chiara e concisa, eliminando la complessità del Visitor Pattern.
*   **Manutenzione Facilitata:** Aggiungere un nuovo tipo di proprietà richiederà di aggiungerlo alla clausola `permits` e di aggiungere un nuovo `case` nello `switch`. Il compilatore guiderà lo sviluppatore nel processo.
*   **Performance:** Le espressioni `switch` moderne sono altamente ottimizzate dalla JVM.

### Svantaggi

*   **Requisito JDK:** Richiede una versione di Java che supporti pienamente le `sealed classes` e il pattern matching per `switch` (idealmente JDK 17+).
*   **Refactoring Iniziale:** La modifica della gerarchia delle entità `Property` in `sealed` è un intervento strutturale che richiede attenzione.

## 8. Diagramma del Flusso

```mermaid
graph TD
    subgraph Controller/Service Layer
        A[PropertiesController] --> B[PropertyManagementService];
    end

    subgraph Service Layer
        B --> C{Recupera Entità Property};
    end

    subgraph Mapper Layer
        D[PropertyResponseMapper]
    end

    subgraph DTO Layer
        E[PropertyResponse DTO]
    end

    C --> |property| D;

    D --> |switch property| F{Pattern Matching};
    F -->|is ResidentialProperty| G[toResidentialResponse];
    F -->|is CommercialProperty| H[toCommercialPropertyResponse];
    F -->|is Land| I[toLandResponse];
    F -->|is Garage| J[toGarageResponse];

    G --> E;
    H --> E;
    I --> E;
    J --> E;

    D --> |propertyResponse| A;

    style F fill:#f9f,stroke:#333,stroke-width:2px
    style D fill:#bbf,stroke:#333,stroke-width:2px