# Piano Strategico per l'Integrazione di MapStruct

Questo piano descrive la strategia per sostituire il `PropertyMapper` manuale con MapStruct, con l'obiettivo di ottenere una mappatura DTO-Entità type-safe, automatizzata e performante.

## 1. Aggiunta delle Dipendenze e Configurazione

Per integrare MapStruct, è necessario aggiungere le seguenti dipendenze e configurazioni al file `pom.xml`.

### Dipendenze Maven

```xml
<properties>
    <org.mapstruct.version>1.5.5.Final</org.mapstruct.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>${org.mapstruct.version}</version>
    </dependency>
</dependencies>
```

### Configurazione del `maven-compiler-plugin`

È fondamentale configurare il `maven-compiler-plugin` per abilitare l'elaborazione delle annotazioni di MapStruct durante la compilazione.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.1</version>
            <configuration>
                <source>17</source>
                <target>17</target>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>${org.mapstruct.version}</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## 2. Strategia di Implementazione dei Mapper

Verranno create interfacce mapper dedicate per ogni entità principale, seguendo le best practice di MapStruct.

### Interfaccia `PropertyMapper`

Questa interfaccia gestirà la mappatura polimorfica tra `Property` e i suoi sottotipi (`ResidentialProperty`, `CommercialProperty`, etc.) e i corrispondenti DTO di risposta.

```java
@Mapper(componentModel = "spring", uses = {AddressMapper.class, UserMapper.class})
public interface PropertyMapper {

    @SubclassMapping(source = ResidentialProperty.class, target = ResidentialPropertyResponse.class)
    @SubclassMapping(source = CommercialProperty.class, target = CommercialPropertyResponse.class)
    // Aggiungere mappature per Garage e Land
    PropertyResponse toResponse(Property property);

    // Mappature inverse da DTO a Entità
    @InheritInverseConfiguration
    Property toEntity(PropertyResponse response);
}
```

### Gestione Mappature Complesse

-   **Nomi di Campo Diversi**: `@Mapping(source = "nomeCampoSource", target = "nomeCampoTarget")` verrà utilizzato per mappare campi con nomi differenti.
-   **Conversioni di Tipo**: MapStruct gestisce automaticamente molte conversioni. Per logiche custom (es. `contract.name` -> `contract`), si useranno espressioni Java con `@Mapping(target = "contract", expression = "java(property.getContract().getName())")`.
-   **Logica Custom**: Per logiche complesse (es. la gestione del campo `floor`), si potranno definire metodi helper `default` o `static` direttamente nell'interfaccia del mapper.

### Esempio di `UserMapper`

```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
    User toEntity(UserResponse response);
}
```

## 3. Integrazione con il Codice Esistente

La sostituzione del `PropertyMapper` manuale avverrà in modo graduale.

1.  **Iniezione dei Mapper**: I nuovi mapper verranno iniettati come dipendenze nei service e controller dove sono necessari (es. `PropertyManagementService`, `PropertiesController`).
2.  **Sostituzione delle Chiamate**: Le chiamate a `PropertyMapper.toResponse(property)` verranno sostituite con `propertyMapper.toResponse(property)`.
3.  **Refactoring dei Metodi di Creazione**: I metodi come `toResidentialEntity` verranno sostituiti da metodi di mappatura nelle interfacce MapStruct, sfruttando `@InheritInverseConfiguration` per ridurre la duplicazione.

### Aree Impattate

-   `PropertyManagementService`: Sostituire l'uso di `PropertyMapper` manuale.
-   `PropertyQueryService`: Aggiornare le mappature nelle query.
-   `PropertiesController`: Utilizzare il nuovo `PropertyMapper` per le risposte.
-   `AuthenticationService`, `UserController`, `AuthController`: Se mappano entità `User`, utilizzeranno il nuovo `UserMapper`.

## 4. Validazione

La correttezza dei nuovi mapper verrà garantita tramite:

-   **Test Unitari**: Verranno creati test specifici per ogni interfaccia mapper per verificare che le conversioni DTO-Entità e viceversa avvengano correttamente, coprendo anche i casi limite e le mappature complesse.
-   **Test di Integrazione**: I test di integrazione esistenti verranno eseguiti per assicurare che la sostituzione del mapper non abbia introdotto regressioni nel comportamento dell'applicazione.

## Diagramma di Flusso (Mermaid)

```mermaid
graph TD
    A[Richiesta API] --> B{Controller};
    B --> C[Service];
    C --> D{Repository};
    D --> E[Database];
    E --> D;
    D --> C;
    C -- Chiama --> F[MapStruct Mapper];
    F -- Mappa Entity -> DTO --> C;
    C --> B;
    B -- Risposta API con DTO --> A;