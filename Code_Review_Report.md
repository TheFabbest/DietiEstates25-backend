### **Report di Code Review Completo: DietiEstates Backend**

**Data:** 4 Settembre 2025

#### **Giudizio Complessivo**

Il progetto "DietiEstates Backend" presenta una **solida base architetturale**, con una chiara adozione di pattern a strati e l'impiego di funzionalità avanzate di Spring Boot e JPA. Questo denota una buona comprensione dei principi di design del software e delle performance.

Tuttavia, il progetto è afflitto da **criticità significative** che ne compromettono la robustezza, la sicurezza e, soprattutto, la manutenibilità a lungo termine. Il **debito tecnico accumulato, in particolare l'assenza quasi totale di test automatici, rappresenta il rischio più grande** e deve essere affrontato con la massima priorità.

Il progetto **non è pronto per uno sviluppo professionale a lungo termine né per un'implementazione in produzione stabile e sicura** senza un intervento significativo. I refactoring e le azioni correttive proposte di seguito sono da considerarsi **necessari** per elevare la qualità del codice a uno standard professionale e garantire la futura evoluzione del software.

---

### **Analisi Dettagliata File per File**

#### **1. [`src/main/java/com/dieti/dietiestatesbackend/DietiEstatesBackend.java`](src/main/java/com/dieti/dietiestatesbackend/DietiEstatesBackend.java:1)**

*   **Punti di Forza:** Standard Spring Boot, `@EnableJpaAuditing` per auditing automatico.
*   **Aree di Miglioramento e Rischi:** Potenziale import obsoleto di `SecurityAutoConfiguration`.

#### **2. [`src/main/java/com/dieti/dietiestatesbackend/config/AppConfig.java`](src/main/java/com/dieti/dietiestatesbackend/config/AppConfig.java:1)**

*   **Punti di Forza:** Configurazione `ObjectMapper` centralizzata, uso di `findAndRegisterModules()`, `FAIL_ON_UNKNOWN_PROPERTIES, false` per resilienza.
*   **Aree di Miglioramento e Rischi:** `FAIL_ON_UNKNOWN_PROPERTIES, false` può mascherare errori di input; valutare trade-off.

#### **3. [`src/main/java/com/dieti/dietiestatesbackend/config/JdbcConfig.java`](src/main/java/com/dieti/dietiestatesbackend/config/JdbcConfig.java:1)**

*   **Punti di Forza:** `@EnableScheduling`, `ScheduledExecutorService` con `DaemonThreadFactory`.
*   **Aree di Miglioramento e Rischi:**
    *   **Bean `Connection` (Critico):** La definizione di un bean `Connection` è **estremamente problematica** per la concorrenza e la gestione delle risorse.
        *   **Suggerimento:** **Rimuovere immediatamente il bean `Connection`**.

#### **4. [`src/main/java/com/dieti/dietiestatesbackend/controller/AddressController.java`](src/main/java/com/dieti/dietiestatesbackend/controller/AddressController.java:1)**

*   **Punti di Forza:** Delega al service, uso di `Optional`, risposte HTTP appropriate.
*   **Aree di Miglioramento e Rischi:**
    *   **Tipo di Ritorno Generico:** `ResponseEntity<Object>`.
        *   **Suggerimento:** Usare tipi specifici (es. `ResponseEntity<AddressResponse>`).

#### **5. [`src/main/java/com/dieti/dietiestatesbackend/controller/AuthController.java`](src/main/java/com/dieti/dietiestatesbackend/controller/AuthController.java:1)**

*   **Punti di Forza:** Separazione delle responsabilità (parziale), validazione input, gestione token refresh, endpoint logout.
*   **Aree di Miglioramento e Rischi:**
    *   **Logica di Business nel Controller (Critico):** Logica di autenticazione e costruzione risposte nel controller.
        *   **Suggerimento:** Spostare la logica di autenticazione nell'`AuthenticationService`.
    *   **Gestione Incoerente degli Errori:** Bypass del `GlobalExceptionHandler`, codici di stato non standard (es. 498).
        *   **Suggerimento:** Lanciare eccezioni custom gestite centralmente, usare codici HTTP standard.

#### **6. [`src/main/java/com/dieti/dietiestatesbackend/controller/ContractController.java`](src/main/java/com/dieti/dietiestatesbackend/controller/ContractController.java:1)**

*   **Punti di Forza:** Semplice, specifico, sicurezza a livello di metodo.
*   **Aree di Miglioramento e Rischi:**
    *   **Violazione Separazione Layer:** Accesso diretto al `ContractRepository`.
        *   **Suggerimento:** Introdurre un `ContractService`.
    *   **Esposizione Diretta Entità:** Restituisce `List<Contract>`.
        *   **Suggerimento:** Usare DTO di risposta (`List<ContractResponse>`).

#### **7. [`src/main/java/com/dieti/dietiestatesbackend/controller/OfferController.java`](src/main/java/com/dieti/dietiestatesbackend/controller/OfferController.java:1)**

*   **Punti di Forza:** Delega al service, sicurezza a livello di metodo avanzata.
*   **Aree di Miglioramento e Rischi:**
    *   **Tipo di Ritorno Generico:** `ResponseEntity<Object>`.
        *   **Suggerimento:** Usare tipi specifici (es. `ResponseEntity<List<OfferResponse>>`).

#### **8. [`src/main/java/com/dieti/dietiestatesbackend/controller/PropertiesController.java`](src/main/java/com/dieti/dietiestatesbackend/controller/PropertiesController.java:1)**

*   **Punti di Forza:** Delega al service, sicurezza dichiarativa, gestione errori di validazione.
*   **Aree di Miglioramento e Rischi:**
    *   **Violazione SRP ("God Object"):** Troppe responsabilità.
        *   **Suggerimento:** Suddividere in controller più piccoli.
    *   **Accoppiamento con Mapper/Utility:** Chiama direttamente `PropertyMapper.toResponse`, logica di file in controller.
        *   **Suggerimento:** Service deve restituire DTO, logica file in service dedicato.
    *   **Tipi di Ritorno Generici:** `ResponseEntity<Object>`.
        *   **Suggerimento:** Usare tipi specifici.

#### **9. [`src/main/java/com/dieti/dietiestatesbackend/controller/UserController.java`](src/main/java/com/dieti/dietiestatesbackend/controller/UserController.java:1)**

*   **Punti di Forza:** Delega al service, sicurezza a livello di metodo, uso di `UserResponse`.
*   **Aree di Miglioramento e Rischi:**
    *   **Logica di Business nel Controller:** Verifica `user.isAgent()`, costruzione manuale di `UserResponse`.
        *   **Suggerimento:** Spostare nel service o in un mapper.
    *   **Gestione Errori Incoerente:** Bypass del `GlobalExceptionHandler`.
        *   **Suggerimento:** Lanciare eccezioni gestite centralmente.

#### **10. [`src/main/java/com/dieti/dietiestatesbackend/controller/VisitController.java`](src/main/java/com/dieti/dietiestatesbackend/controller/VisitController.java:1)**

*   **Punti di Forza:** Delega al service, sicurezza a livello di metodo avanzata.
*   **Aree di Miglioramento e Rischi:**
    *   **Tipo di Ritorno Generico:** `ResponseEntity<Object>`.
        *   **Suggerimento:** Usare tipi specifici (es. `ResponseEntity<List<VisitResponse>>`).

#### **11. [`src/main/java/com/dieti/dietiestatesbackend/dto/request/AddressRequest.java`](src/main/java/com/dieti/dietiestatesbackend/dto/request/AddressRequest.java:1)**

*   **Punti di Forza:** Uso di Lombok, validazione dichiarativa (`@NotBlank`, `@JsonProperty`).
*   **Aree di Miglioramento e Rischi:**
    *   **Mancanza Validazione `streetNumber`:** Non ha `@NotBlank`.
        *   **Suggerimento:** Aggiungere `@NotBlank` se obbligatorio.
    *   **Commento Obsoleto:** Riferimento a `IndirizzoRequest.java`.
        *   **Suggerimento:** Rimuovere.

#### **12. [`src/main/java/com/dieti/dietiestatesbackend/dto/request/AuthRequest.java`](src/main/java/com/dieti/dietiestatesbackend/dto/request/AuthRequest.java:1)**

*   **Punti di Forza:** Uso di Lombok, validazione `@NotBlank`, `@Email`.
*   **Aree di Miglioramento e Rischi:**
    *   **Mancanza Validazione Complessità Password (Critico):** Solo `@NotBlank`.
        *   **Suggerimento:** Aggiungere `@Size` e `@Pattern` per requisiti di complessità.

#### **13. [`src/main/java/com/dieti/dietiestatesbackend/dto/request/CreateCommercialPropertyRequest.java`](src/main/java/com/dieti/dietiestatesbackend/dto/request/CreateCommercialPropertyRequest.java:1)**

*   **Punti di Forza:** Ereditarietà corretta, validazione dichiarativa.
*   **Aree di Miglioramento e Rischi:**
    *   **`@Data` e Mutabilità:** I DTO dovrebbero essere immutabili.
        *   **Suggerimento:** Usare `@Value` o solo `@Getter` con costruttore.
    *   **Mancanza Validazione `floor`:** Solo `@NotNull`.
        *   **Suggerimento:** Aggiungere `@Min` o `@Max`.

#### **14. [`src/main/java/com/dieti/dietiestatesbackend/dto/request/CreateGaragePropertyRequest.java`](src/main/java/com/dieti/dietiestatesbackend/dto/request/CreateGaragePropertyRequest.java:1)**

*   **Punti di Forza:** Ereditarietà corretta, validazione `@Min`.
*   **Aree di Miglioramento e Rischi:**
    *   **`@Data` e Mutabilità:** I DTO dovrebbero essere immutabili.
        *   **Suggerimento:** Usare `@Value` o solo `@Getter` con costruttore.
    *   **Mancanza `@NotNull` per `numberOfFloors`:**
        *   **Suggerimento:** Aggiungere `@NotNull`.

#### **15. [`src/main/java/com/dieti/dietiestatesbackend/dto/request/CreateLandPropertyRequest.java`](src/main/java/com/dieti/dietiestatesbackend/dto/request/CreateLandPropertyRequest.java:1)**

*   **Punti di Forza:** Ereditarietà corretta.
*   **Aree di Miglioramento e Rischi:**
    *   **`@Data` e Mutabilità:** I DTO dovrebbero essere immutabili.
        *   **Suggerimento:** Usare `@Value` o solo `@Getter` con costruttore.
    *   **`Boolean` vs `boolean` per `hasRoadAccess`:** Ambiguita sul `null`.
        *   **Suggerimento:** Chiarire l'intenzione e usare il tipo appropriato.
    *   **Commento Obsoleto:**
        *   **Suggerimento:** Aggiornare o rimuovere.

#### **16. [`src/main/java/com/dieti/dietiestatesbackend/dto/request/CreatePropertyRequest.java`](src/main/java/com/dieti/dietiestatesbackend/dto/request/CreatePropertyRequest.java:1)**

*   **Punti di Forza:** `sealed class` e pattern matching, validazione custom `@ExistingEntity`, esternazione deserializzazione polimorfica.
*   **Aree di Miglioramento e Rischi:**
    *   **`@Data` e Mutabilità:** I DTO dovrebbero essere immutabili.
        *   **Suggerimento:** Usare `@Value` o solo `@Getter` con costruttore.
    *   **Mancanza `@NotNull` per Campi Comuni:** Molti campi obbligatori senza `@NotNull`.
        *   **Suggerimento:** Aggiungere `@NotNull` a tutti i campi obbligatori.
    *   **Validazione Insufficiente Numerici:** `price`, `area` senza `@Min`/`@DecimalMin`.
        *   **Suggerimento:** Aggiungere `@DecimalMin(value = "0.01")` per `price` e `@Min(1)` per `area`.

#### **17. [`src/main/java/com/dieti/dietiestatesbackend/dto/request/CreatePropertyRequestDeserializer.java`](src/main/java/com/dieti/dietiestatesbackend/dto/request/CreatePropertyRequestDeserializer.java:1)**

*   **Punti di Forza:** Gestione deserializzazione polimorfica, accesso `ApplicationContext`, gestione esplicita tipi.
*   **Aree di Miglioramento e Rischi:**
    *   **Accesso Statico `ApplicationContext` (Critico):** Grave anti-pattern.
        *   **Suggerimento:** **Rimuovere campo `static` e `ApplicationContextAware`**. Iniettare `CategoryLookupService` direttamente nel costruttore del deserializzatore registrandolo come modulo Jackson.
    *   **`System.out.println`:** Per logging.
        *   **Suggerimento:** Usare SLF4J logger.
    *   **Gestione Manuale Campi JSON:** Verboso e incline a errori.
        *   **Suggerimento:** Delegare la deserializzazione a Jackson dopo aver determinato il tipo.
    *   **Dipendenza da `IOException`:** Per errori di business.
        *   **Suggerimento:** Lanciare eccezioni di dominio più specifiche.

#### **18. [`src/main/java/com/dieti/dietiestatesbackend/dto/request/FilterRequest.java`](src/main/java/com/dieti/dietiestatesbackend/dto/request/FilterRequest.java:1)**

*   **Punti di Forza:** Uso di Lombok, validazione dichiarativa (`@Min`, `@Max`).
*   **Aree di Miglioramento e Rischi:**
    *   **Validazione Incrociata Mancante:** (es. `minPrice` > `maxPrice`).
        *   **Suggerimento:** Implementare validazione custom a livello di classe.
    *   **Validazione `propertyType`/`propertyCategory`:** Nessuna validazione esistenza.
        *   **Suggerimento:** Usare validazioni custom (es. `@ExistingEntity`).
    *   **`@Data` e Mutabilità:** I DTO dovrebbero essere immutabili.
        *   **Suggerimento:** Usare `@Value` o solo `@Getter` con costruttore.

#### **19. [`src/main/java/com/dieti/dietiestatesbackend/dto/request/GoogleAuthRequest.java`](src/main/java/com/dieti/dietiestatesbackend/dto/request/GoogleAuthRequest.java:1)**

*   **Punti di Forza:** Uso di Lombok, validazione `@NotBlank` per `token`.
*   **Aree di Miglioramento e Rischi:**
    *   **Mancanza Validazione `username`, `name`, `surname`:** Se obbligatori.
        *   **Suggerimento:** Aggiungere `@NotBlank` se necessario.
    *   **`@Data` e Mutabilità:** I DTO dovrebbero essere immutabili.
        *   **Suggerimento:** Usare `@Value` o solo `@Getter` con costruttore.

#### **20. [`src/main/java/com/dieti/dietiestatesbackend/dto/request/PropertyRequest.java`](src/main/java/com/dieti/dietiestatesbackend/dto/request/PropertyRequest.java:1)**

*   **Punti di Forza:** Validazione dichiarativa.
*   **Aree di Miglioramento e Rischi:**
    *   **Codice Morto/Obsoleto (Critico):** Sembra essere un DTO obsoleto, duplicato da `CreatePropertyRequest`.
        *   **Suggerimento:** **Rimuovere completamente se non utilizzato**.
    *   **Mancanza di Lombok:** Boilerplate manuale.
        *   **Suggerimento:** Convertire a Lombok se mantenuto.
    *   **Commento `TODO` Obsoleto:**
        *   **Suggerimento:** Rimuovere.

#### **21. [`src/main/java/com/dieti/dietiestatesbackend/dto/request/RefreshRequest.java`](src/main/java/com/dieti/dietiestatesbackend/dto/request/RefreshRequest.java:1)**

*   **Punti di Forza:** Uso di Lombok, validazione `@NotBlank`.
*   **Aree di Miglioramento e Rischi:**
    *   **`@Data` e Mutabilità:** I DTO dovrebbero essere immutabili.
        *   **Suggerimento:** Usare `@Value` o solo `@Getter` con costruttore.

#### **22. [`src/main/java/com/dieti/dietiestatesbackend/dto/request/SignupRequest.java`](src/main/java/com/dieti/dietiestatesbackend/dto/request/SignupRequest.java:1)**

*   **Punti di Forza:** Uso di Lombok, **validazione robusta della password (eccellente)**, validazione completa.
*   **Aree di Miglioramento e Rischi:**
    *   **`@Data` e Mutabilità:** I DTO dovrebbero essere immutabili.
        *   **Suggerimento:** Usare `@Value` o solo `@Getter` con costruttore.

#### **23. [`src/main/java/com/dieti/dietiestatesbackend/dto/response/AuthResponse.java`](src/main/java/com/dieti/dietiestatesbackend/dto/response/AuthResponse.java:1)**

*   **Punti di Forza:** Immutabilità (campi `final`), inclusione ruoli.
*   **Aree di Miglioramento e Rischi:**
    *   **Costruttore Incompleto/Fuorviante:** Costruttore che imposta `availableRoles` a `null`.
        *   **Suggerimento:** Rimuovere il costruttore incompleto.
    *   **Mancanza di Lombok:** Boilerplate manuale.
        *   **Suggerimento:** Usare `@Value`.

#### **24. [`src/main/java/com/dieti/dietiestatesbackend/dto/response/CommercialPropertyResponse.java`](src/main/java/com/dieti/dietiestatesbackend/dto/response/CommercialPropertyResponse.java:1)**

*   **Punti di Forza:** Ereditarietà corretta, uso di Lombok.
*   **Aree di Miglioramento e Rischi:**
    *   **`@Getter`/`@Setter` e Mutabilità:** I DTO dovrebbero essere immutabili.
        *   **Suggerimento:** Usare `@Value` o solo `@Getter` con costruttore.
    *   **`floors` come `List<String>`:** Mancanza di chiarezza semantica.
        *   **Suggerimento:** Chiarire il significato o usare `List<Integer>`.

#### **25. [`src/main/java/com/dieti/dietiestatesbackend/dto/response/GarageResponse.java`](src/main/java/com/dieti/dietiestatesbackend/dto/response/GarageResponse.java:1)**

*   **Punti di Forza:** Ereditarietà corretta, uso di Lombok.
*   **Aree di Miglioramento e Rischi:**
    *   **`@Data` e Mutabilità:** I DTO dovrebbero essere immutabili.
        *   **Suggerimento:** Usare `@Value` o solo `@Getter` con costruttore.
    *   **`floors` come `List<String>`:** Mancanza di chiarezza semantica.
        *   **Suggerimento:** Chiarire il significato o usare `List<Integer>`.

#### **26. [`src/main/java/com/dieti/dietiestatesbackend/dto/response/LandResponse.java`](src/main/java/com/dieti/dietiestatesbackend/dto/response/LandResponse.java:1)**

*   **Punti di Forza:** Ereditarietà corretta, uso di Lombok.
*   **Aree di Miglioramento e Rischi:**
    *   **`@Data` e Mutabilità:** I DTO dovrebbero essere immutabili.
        *   **Suggerimento:** Usare `@Value` o solo `@Getter` con costruttore.

#### **27. [`src/main/java/com/dieti/dietiestatesbackend/dto/response/PropertyResponse.java`](src/main/java/com/dieti/dietiestatesbackend/dto/response/PropertyResponse.java:1)**

*   **Punti di Forza:** Uso di Lombok, inclusione dettagli relazione.
*   **Aree di Miglioramento e Rischi:**
    *   **`@Getter`/`@Setter` e Mutabilità:** I DTO dovrebbero essere immutabili.
        *   **Suggerimento:** Usare `@Value` o solo `@Getter` con costruttore.
    *   **"Primitive Obsession":** Campi come `contract`, `propertyCategory` come `String`.
        *   **Suggerimento:** Usare DTO annidati per maggiore espressività.
    *   **Commenti `TODO` Obsoleti/Mancanti:**
        *   **Suggerimento:** Rimuovere i commenti `TODO` o implementarli.
    *   **Esposizione ID Interni:** `id_agent`, `id_address`.
        *   **Suggerimento:** Considerare DTO annidati o UUID.

#### **28. [`src/main/java/com/dieti/dietiestatesbackend/dto/response/ResidentialPropertyResponse.java`](src/main/java/com/dieti/dietiestatesbackend/dto/response/ResidentialPropertyResponse.java:1)**

*   **Punti di Forza:** DTO annidato per `HeatingDTO` (eccellente), uso di Enum per `Garden`.
*   **Aree di Miglioramento e Rischi:**
    *   **Mancanza di Lombok:** Boilerplate manuale.
        *   **Suggerimento:** Convertire a Lombok.
    *   **`floors` come `List<String>`:** Mancanza di chiarezza semantica.
        *   **Suggerimento:** Chiarire il significato o usare `List<Integer>`.
    *   **`totalFloors` e `floors`:** Potenziale ridondanza.
        *   **Suggerimento:** Chiarire la relazione o rimuovere ridondanza.

#### **29. [`src/main/java/com/dieti/dietiestatesbackend/dto/response/UserResponse.java`](src/main/java/com/dieti/dietiestatesbackend/dto/response/UserResponse.java:1)**

*   **Punti di Forza:** Semplicità.
*   **Aree di Miglioramento e Rischi:**
    *   **Mancanza di Lombok:** Boilerplate manuale.
        *   **Suggerimento:** Convertire a Lombok.
    *   **Mutabilità:**
        *   **Suggerimento:** Rendere immutabile.

#### **30. [`src/main/java/com/dieti/dietiestatesbackend/entities/Address.java`](src/main/java/com/dieti/dietiestatesbackend/entities/Address.java:1)**

*   **Punti di Forza:** Uso di Lombok, validazione dichiarativa, `@Embedded` per `Coordinates`, `toString` personalizzato.
*   **Aree di Miglioramento e Rischi:**
    *   **Nome Campo Inconsistente:** `street_number` (snake_case) vs `streetNumber` nel DTO.
        *   **Suggerimento:** Rinomina in `streetNumber` e usa `@Column(name = "street_number")`.
    *   **Mancanza Validazione `street_number`/`building`:** Se obbligatori.
        *   **Suggerimento:** Aggiungere `@NotBlank` o `@NotNull`.

#### **31. [`src/main/java/com/dieti/dietiestatesbackend/entities/Agency.java`](src/main/java/com/dieti/dietiestatesbackend/entities/Agency.java:1)**

*   **Punti di Forza:** Uso di Lombok (`@ToString(exclude = "users")`), relazioni corrette con `CascadeType.ALL` e `orphanRemoval`, `@JsonBackReference`.
*   **Aree di Miglioramento e Rischi:**
    *   **Accoppiamento con Jackson:** `@JsonBackReference` nell'entità.
        *   **Suggerimento:** Rimuovere annotazioni Jackson dalle entità.

#### **32. [`src/main/java/com/dieti/dietiestatesbackend/entities/BaseEntity.java`](src/main/java/com/dieti/dietiestatesbackend/entities/BaseEntity.java:1)**

*   **Punti di Forza:** Centralizzazione campi comuni, auditing automatico (`@EnableJpaAuditing`), strategia `IDENTITY` per ID.
*   **Aree di Miglioramento e Rischi:**
    *   **Mancanza di Lombok:** Boilerplate manuale.
        *   **Suggerimento:** Convertire a Lombok.
    *   **`Serializable`:** Potenziale ridondanza.
        *   **Suggerimento:** Rimuovere se non necessaria.

#### **33. [`src/main/java/com/dieti/dietiestatesbackend/entities/CommercialProperty.java`](src/main/java/com/dieti/dietiestatesbackend/entities/CommercialProperty.java:1)**

*   **Punti di Forza:** Implementazione corretta ereditarietà `JOINED`, Visitor Pattern, uso di Lombok.
*   **Aree di Migioramento e Rischi:**
    *   **Getter Manuale Ridondante:** `getHasWheelchairAccess()`.
        *   **Suggerimento:** Refactorare codice dipendente o configurare Lombok.
    *   **Nomenclatura Inconsistente:** `numeroVetrine` (italiano).
        *   **Suggerimento:** Rinomina in inglese (`shopWindowCount`).

#### **34. [`src/main/java/com/dieti/dietiestatesbackend/entities/Contract.java`](src/main/java/com/dieti/dietiestatesbackend/entities/Contract.java:1)**

*   **Punti di Forza:** Uso di Lombok, validazione `@NotBlank`, `unique = true`, `is_active`.
*   **Aree di Miglioramento e Rischi:**
    *   **Costruttore `public`:** JPA best practice `protected`.
        *   **Suggerimento:** Cambiare a `AccessLevel.PROTECTED`.
    *   **Mancanza `@ToString`/`@EqualsAndHashCode`:**
        *   **Suggerimento:** Aggiungere.

#### **35. [`src/main/java/com/dieti/dietiestatesbackend/entities/Coordinates.java`](src/main/java/com/dieti/dietiestatesbackend/entities/Coordinates.java:1)**

*   **Punti di Forza:** `@Embeddable` come Value Object, uso di Lombok, `@Digits`, `BigDecimal`.
*   **Aree di Miglioramento e Rischi:**
    *   **Mutabilità:** I campi sono mutabili.
        *   **Suggerimento:** Rendere immutabile (rimuovere `@Setter`, campi `final`).

#### **36. [`src/main/java/com/dieti/dietiestatesbackend/entities/Garage.java`](src/main/java/com/dieti/dietiestatesbackend/entities/Garage.java:1)**

*   **Punti di Forza:** Implementazione corretta ereditarietà `JOINED`, Visitor Pattern, uso di Lombok.
*   **Aree di Miglioramento e Rischi:**
    *   **Mancanza `@NotNull` per `numberOfFloors`:**
        *   **Suggerimento:** Aggiungere `@NotNull`.
    *   **Costruttore `public`:** JPA best practice `protected`.
        *   **Suggerimento:** Cambiare a `AccessLevel.PROTECTED`.

#### **37. [`src/main/java/com/dieti/dietiestatesbackend/entities/Heating.java`](src/main/java/com/dieti/dietiestatesbackend/entities/Heating.java:1)**

*   **Punti di Forza:** Uso di Lombok, validazione `@NotBlank`, `unique = true`, `is_active`.
*   **Aree di Miglioramento e Rischi:**
    *   **Costruttore `public`:** JPA best practice `protected`.
        *   **Suggerimento:** Cambiare a `AccessLevel.PROTECTED`.
    *   **Mancanza `@ToString`/`@EqualsAndHashCode`:**
        *   **Suggerimento:** Aggiungere.

#### **38. [`src/main/java/com/dieti/dietiestatesbackend/entities/Land.java`](src/main/java/com/dieti/dietiestatesbackend/entities/Land.java:1)**

*   **Punti di Forza:** Implementazione corretta ereditarietà `JOINED`, Visitor Pattern.
*   **Aree di Miglioramento e Rischi:**
    *   **Boilerplate Getter/Setter:** Getter e setter manuali.
        *   **Suggerimento:** Usare Lombok (`@Getter`, `@Setter`).

#### **39. [`src/main/java/com/dieti/dietiestatesbackend/entities/Offer.java`](src/main/java/com/dieti/dietiestatesbackend/entities/Offer.java:1)**

*   **Punti di Forza:** Uso di Lombok, validazione dichiarativa, Lazy Loading, Enum per `OfferStatus`.
*   **Aree di Miglioramento e Rischi:**
    *   **Costruttore `public`:** JPA best practice `protected`.
        *   **Suggerimento:** Cambiare a `AccessLevel.PROTECTED`.
    *   **Mancanza `@ToString`/`@EqualsAndHashCode`:**
        *   **Suggerimento:** Aggiungere.

#### **40. [`src/main/java/com/dieti/dietiestatesbackend/entities/PropertyCategory.java`](src/main/java/com/dieti/dietiestatesbackend/entities/PropertyCategory.java:1)**

*   **Punti di Forza:** Uso di Lombok, validazione `@NotBlank`, `unique = true`, `is_active`.
*   **Aree di Miglioramento e Rischi:**
    *   **Costruttore `public`:** JPA best practice `protected`.
        *   **Suggerimento:** Cambiare a `AccessLevel.PROTECTED`.
    *   **Mancanza `@ToString`/`@EqualsAndHashCode`:**
        *   **Suggerimento:** Aggiungere.

#### **41. [`src/main/java/com/dieti/dietiestatesbackend/entities/PropertyVisitor.java`](src/main/java/com/dieti/dietiestatesbackend/entities/PropertyVisitor.java:1)**

*   **Punti di Forza:** Adesione al Principio Open/Closed, evita `instanceof`.
*   **Aree di Miglioramento e Rischi:** Nessuna.

#### **42. [`src/main/java/com/dieti/dietiestatesbackend/entities/RefreshToken.java`](src/main/java/com/dieti/dietiestatesbackend/entities/RefreshToken.java:1)**

*   **Punti di Forza:** Uso di Lombok, validazione dichiarativa, relazioni corrette, indici sul database, `unique = true`.
*   **Aree di Miglioramento e Rischi:**
    *   **Costruttore `public`:** JPA best practice `protected`.
        *   **Suggerimento:** Cambiare a `AccessLevel.PROTECTED`.
    *   **Mancanza `@ToString`/`@EqualsAndHashCode`:**
        *   **Suggerimento:** Aggiungere.

#### **43. [`src/main/java/com/dieti/dietiestatesbackend/entities/Visit.java`](src/main/java/com/dieti/dietiestatesbackend/entities/Visit.java:1)**

*   **Punti di Forza:** Uso di Lombok, validazione dichiarativa, Lazy Loading, Enum per `VisitStatus`.
*   **Aree di Miglioramento e Rischi:**
    *   **Costruttore `public`:** JPA best practice `protected`.
        *   **Suggerimento:** Cambiare a `AccessLevel.PROTECTED`.
    *   **Mancanza `@ToString`/`@EqualsAndHashCode`:**
        *   **Suggerimento:** Aggiungere.

#### **44. [`src/main/java/com/dieti/dietiestatesbackend/enums/EnergyRating.java`](src/main/java/com/dieti/dietiestatesbackend/enums/EnergyRating.java:1)**

*   **Punti di Forza:** Uso appropriato di Enum, chiarezza.
*   **Aree di Miglioramento e Rischi:**
    *   **Mancanza Mappatura Valore/Descrizione:**
        *   **Suggerimento:** Aggiungere campi per descrizioni user-friendly.

#### **45. [`src/main/java/com/dieti/dietiestatesbackend/enums/Garden.java`](src/main/java/com/dieti/dietiestatesbackend/enums/Garden.java:1)**

*   **Punti di Forza:** Uso appropriato di Enum, chiarezza.
*   **Aree di Miglioramento e Rischi:**
    *   **Mancanza Mappatura Valore/Descrizione:**
        *   **Suggerimento:** Aggiungere campi per descrizioni user-friendly.

#### **46. [`src/main/java/com/dieti/dietiestatesbackend/enums/OfferStatus.java`](src/main/java/com/dieti/dietiestatesbackend/enums/OfferStatus.java:1)**

*   **Punti di Forza:** Uso appropriato di Enum, chiarezza.
*   **Aree di Miglioramento e Rischi:**
    *   **Mancanza Mappatura Valore/Descrizione:**
        *   **Suggerimento:** Aggiungere campi per descrizioni user-friendly.
    *   **Transizioni di Stato:** Nessuna logica di transizione definita.
        *   **Suggerimento:** Implementare logica di transizione nel service layer.

#### **47. [`src/main/java/com/dieti/dietiestatesbackend/enums/PropertyStatus.java`](src/main/java/com/dieti/dietiestatesbackend/enums/PropertyStatus.java:1)**

*   **Punti di Forza:** Uso appropriato di Enum, chiarezza.
*   **Aree di Miglioramento e Rischi:**
    *   **Mancanza Mappatura Valore/Descrizione:**
        *   **Suggerimento:** Aggiungere campi per descrizioni user-friendly.

#### **48. [`src/main/java/com/dieti/dietiestatesbackend/enums/PropertyType.java`](src/main/java/com/dieti/dietiestatesbackend/enums/PropertyType.java:1)**

*   **Punti di Forza:** Uso appropriato di Enum, chiarezza, ruolo cruciale in deserializzazione polimorfica.
*   **Aree di Miglioramento e Rischi:**
    *   **Mancanza Mappatura Valore/Descrizione:**
        *   **Suggerimento:** Aggiungere campi per descrizioni user-friendly.

#### **49. [`src/main/java/com/dieti/dietiestatesbackend/enums/TipologiaProprieta.java`](src/main/java/com/dieti/dietiestatesbackend/enums/TipologiaProprieta.java:1)**

*   **Punti di Forza:** Uso appropriato di Enum, chiarezza.
*   **Aree di Miglioramento e Rischi:**
    *   **Mancanza Mappatura Valore/Descrizione:**
        *   **Suggerimento:** Aggiungere campi per descrizioni user-friendly.

#### **50. [`src/main/java/com/dieti/dietiestatesbackend/enums/VisitStatus.java`](src/main/java/com/dieti/dietiestatesbackend/enums/VisitStatus.java:1)**

*   **Punti di Forza:** Uso appropriato di Enum, chiarezza.
*   **Aree di Miglioramento e Rischi:**
    *   **Mancanza Mappatura Valore/Descrizione:**
        *   **Suggerimento:** Aggiungere campi per descrizioni user-friendly.
    *   **Transizioni di Stato:** Nessuna logica di transizione definita.
        *   **Suggerimento:** Implementare logica di transizione nel service layer.

#### **51. [`src/main/java/com/dieti/dietiestatesbackend/exception/EntityNotFoundException.java`](src/main/java/com/dieti/dietiestatesbackend/exception/EntityNotFoundException.java:1)**

*   **Punti di Forza:** Specificità, `RuntimeException`.
*   **Aree di Miglioramento e Rischi:**
    *   **Mancanza Costruttore con `cause`:**
        *   **Suggerimento:** Aggiungere costruttore con `Throwable cause`.

#### **52. [`src/main/java/com/dieti/dietiestatesbackend/exception/GeocodingException.java`](src/main/java/com/dieti/dietiestatesbackend/exception/GeocodingException.java:1)**

*   **Punti di Forza:** Specificità, `RuntimeException`, costruttore con `cause`.
*   **Aree di Miglioramento e Rischi:** Nessuna.

#### **53. [`src/main/java/com/dieti/dietiestatesbackend/exception/HashingException.java`](src/main/java/com/dieti/dietiestatesbackend/exception/HashingException.java:1)**

*   **Punti di Forza:** Specificità, `RuntimeException`, costruttore con `cause`.
*   **Aree di Miglioramento e Rischi:** Nessuna.

#### **54. [`src/main/java/com/dieti/dietiestatesbackend/exception/InvalidPayloadException.java`](src/main/java/com/dieti/dietiestatesbackend/exception/InvalidPayloadException.java:1)**

*   **Punti di Forza:** Specificità per validazione semantica, contenuto dettagliato, `RuntimeException`.
*   **Aree di Miglioramento e Rischi:**
    *   **Incoerenza con `GlobalExceptionHandler`:** Formato di risposta diverso.
        *   **Suggerimento:** Standardizzare il formato di risposta in `GlobalExceptionHandler`.

#### **55. [`src/main/java/com/dieti/dietiestatesbackend/mappers/PropertyMapper.java`](src/main/java/com/dieti/dietiestatesbackend/mappers/PropertyMapper.java:1)**

*   **Punti di Forza:** Uso del Visitor Pattern, gestione relazioni.
*   **Aree di Miglioramento e Rischi:**
    *   **Duplicazione Logica Mapping (Critico):** Mappatura DTO-Entità duplicata rispetto a `PropertyCreationMapper`.
        *   **Suggerimento:** **Rimuovere completamente la sezione di mappatura DTO-Entità**.
    *   **Mappatura Manuale Campi Comuni:**
        *   **Suggerimento:** Usare MapStruct anche per la mappatura entità-DTO di risposta.

#### **56. [`src/main/java/com/dieti/dietiestatesbackend/mappers/ResponseBuildingVisitor.java`](src/main/java/com/dieti/dietiestatesbackend/mappers/ResponseBuildingVisitor.java:1)**

*   **Punti di Forza:** Corretta implementazione Visitor Pattern, separazione responsabilità.
*   **Aree di Miglioramento e Rischi:**
    *   **Mancanza di Lombok:** Boilerplate manuale.
        *   **Suggerimento:** Convertire a Lombok.
    *   **Mappatura Manuale Campi Specifici:**
        *   **Suggerimento:** Usare MapStruct all'interno dei metodi `visit`.
    *   **Gestione `floors` con `try-catch`:**
        *   **Suggerimento:** La validazione dovrebbe avvenire a monte.

#### **57. [`src/main/java/com/dieti/dietiestatesbackend/repositories/AddressRepository.java`](src/main/java/com/dieti/dietiestatesbackend/repositories/AddressRepository.java:1)**

*   **Punti di Forza:** Semplicità, corretta annotazione.
*   **Aree di Miglioramento e Rischi:** Nessuna.

#### **58. [`src/main/java/com/dieti/dietiestatesbackend/repositories/ContractRepository.java`](src/main/java/com/dieti/dietiestatesbackend/repositories/ContractRepository.java:1)**

*   **Punti di Forza:** Semplicità, metodi derivati.
*   **Aree di Miglioramento e Rischi:** Nessuna.

#### **59. [`src/main/java/com/dieti/dietiestatesbackend/repositories/HeatingRepository.java`](src/main/java/com/dieti/dietiestatesbackend/repositories/HeatingRepository.java:1)**

*   **Punti di Forza:** Semplicità, metodi derivati.
*   **Aree di Miglioramento e Rischi:** Nessuna.

#### **60. [`src/main/java/com/dieti/dietiestatesbackend/repositories/OfferRepository.java`](src/main/java/com/dieti/dietiestatesbackend/repositories/OfferRepository.java:1)**

*   **Punti di Forza:** Semplicità, query nativa con spiegazione.
*   **Aree di Miglioramento e Rischi:**
    *   **Query Nativa:** Riduce portabilità.
        *   **Suggerimento:** Convertire a JPQL se possibile.
    *   **Mancanza Paginazione/Lazy Loading:** Carica tutte le offerte in memoria.
        *   **Suggerimento:** Usare `Page<Offer>` o `Stream<Offer>`.

#### **61. [`src/main/java/com/dieti/dietiestatesbackend/repositories/PropertyCategoryRepository.java`](src/main/java/com/dieti/dietiestatesbackend/repositories/PropertyCategoryRepository.java:1)**

*   **Punti di Forza:** Semplicità, metodi derivati.
*   **Aree di Miglioramento e Rischi:** Nessuna.

#### **62. [`src/main/java/com/dieti/dietiestatesbackend/repositories/RefreshTokenRepository.java`](src/main/java/com/dieti/dietiestatesbackend/repositories/RefreshTokenRepository.java:1)**

*   **Punti di Forza:** Semplicità, query ottimizzata, metodi derivati per eliminazione.
*   **Aree di Miglioramento e Rischi:** Nessuna.

#### **63. [`src/main/java/com/dieti/dietiestatesbackend/repositories/UserRepository.java`](src/main/java/com/dieti/dietiestatesbackend/repositories/UserRepository.java:1)**

*   **Punti di Forza:** Semplicità, metodi derivati.
*   **Aree di Miglioramento e Rischi:** Nessuna.

#### **64. [`src/main/java/com/dieti/dietiestatesbackend/security/AccessTokenProvider.java`](src/main/java/com/dieti/dietiestatesbackend/security/AccessTokenProvider.java:1)**

*   **Punti di Forza:** Esternalizzazione segreto, validazione chiave, generazione token robusta, delega a `TokenHelper`.
*   **Aree di Miglioramento e Rischi:**
    *   **Gestione Silenziosa Eccezioni (Critico):** Inghiotte eccezioni JWT nei getter dei claim.
        *   **Suggerimento:** **Propagare le eccezioni** e gestirle a livello superiore.
    *   **Redundancy `validateToken`:** Potenziale ridondanza.
        *   **Suggerimento:** Verificare e rimuovere se superflua.
    *   **Dipendenza da `User` Concreto:**
        *   **Suggerimento:** Dipendere da un'interfaccia più generica.

#### **65. [`src/main/java/com/dieti/dietiestatesbackend/security/AppPrincipal.java`](src/main/java/com/dieti/dietiestatesbackend/security/AppPrincipal.java:1)**

*   **Punti di Forza:** Estensione modello principal, disaccoppiamento.
*   **Aree di Miglioramento e Rischi:**
    *   **Modello Ruoli Rigido (Riflesso):** `isManager()` riflette modello booleano.
        *   **Suggerimento:** Adattare se il modello di ruoli evolve.

#### **66. [`src/main/java/com/dieti/dietiestatesbackend/security/AuthenticatedUser.java`](src/main/java/com/dieti/dietiestatesbackend/security/AuthenticatedUser.java:1)**

*   **Punti di Forza:** Immutabilità (eccellente), implementazione `equals`/`hashCode`, separazione responsabilità.
*   **Aree di Miglioramento e Rischi:**
    *   **Modello Ruoli Rigido (Riflesso):** `isManager` riflette modello booleano.
        *   **Suggerimento:** Adattare se il modello di ruoli evolve.

#### **67. [`src/main/java/com/dieti/dietiestatesbackend/security/CustomUserDetailsService.java`](src/main/java/com/dieti/dietiestatesbackend/security/CustomUserDetailsService.java:1)**

*   **Punti di Forza:** Standard Spring Security, delega a `UserService`, gestione `UsernameNotFoundException`.
*   **Aree di Miglioramento e Rischi:**
    *   **Dipendenza da `User` Concreto:**
        *   **Suggerimento:** `UserService` dovrebbe restituire `UserDetails` o `AuthenticatedUser`.

#### **68. [`src/main/java/com/dieti/dietiestatesbackend/security/GoogleTokenValidator.java`](src/main/java/com/dieti/dietiestatesbackend/security/GoogleTokenValidator.java:1)**

*   **Punti di Forza:** Validazione token ID Google, iniezione client ID, gestione audience, lancio `SecurityException`.
*   **Aree di Miglioramento e Rischi:**
    *   **Hardcoding Client ID:** In `application.properties`.
        *   **Suggerimento:** Spostare in variabili d'ambiente.
    *   **Eccezioni Generiche:** `IOException`, `GeneralSecurityException`.
        *   **Suggerimento:** Convertire in eccezioni più specifiche.

#### **69. [`src/main/java/com/dieti/dietiestatesbackend/security/JwtClaims.java`](src/main/java/com/dieti/dietiestatesbackend/security/JwtClaims.java:1)**

*   **Punti di Forza:** Scopo definito, costanti per nomi claim, `@JsonProperty`.
*   **Aree di Miglioramento e Rischi:**
    *   **Mancanza di Lombok:** Boilerplate manuale.
        *   **Suggerimento:** Convertire a Lombok (`@Value`).
    *   **Mutabilità:**
        *   **Suggerimento:** Rendere immutabile.
    *   **`isManager` come `Boolean` (wrapper):** Potenziale `null` per booleano.
        *   **Suggerimento:** Usare `boolean` e gestire default.

#### **70. [`src/main/java/com/dieti/dietiestatesbackend/security/RefreshTokenProvider.java`](src/main/java/com/dieti/dietiestatesbackend/security/RefreshTokenProvider.java:1)**

*   **Punti di Forza:** Sicurezza (hash, UUID opachi), rotazione token (eccellente), gestione scadenza, transazionalità.
*   **Aree di Migioramento e Rischi:**
    *   **`REFRESH_TOKEN_DURATION_MS` Hardcoded:**
        *   **Suggerimento:** Spostare in `application.properties`.
    *   **Dipendenza da `User` Concreto:**
        *   **Suggerimento:** Dipendere da un'interfaccia più generica.
    *   **Creazione Manuale `createdAt`:** Ridondante con auditing.
        *   **Suggerimento:** Rimuovere.
    *   **`IllegalStateException` per "Not Found":**
        *   **Suggerimento:** Lanciare `EntityNotFoundException`.

#### **71. [`src/main/java/com/dieti/dietiestatesbackend/security/SecurityUtil.java`](src/main/java/com/dieti/dietiestatesbackend/security/SecurityUtil.java:1)**

*   **Punti di Forza:** Centralizzazione logica autorizzazione, esposizione per SpEL, gestione null.
*   **Aree di Miglioramento e Rischi:**
    *   **Mancanza Cache `canAccessProperty` (Performance):** Query DB ripetuta.
        *   **Suggerimento:** Implementare caching.
    *   **Modello Ruoli Rigido (Riflesso):**
        *   **Suggerimento:** Adattare se il modello di ruoli evolve.
    *   **Lazy Loading Nelle Regole (Potenziale):** Rischio `LazyInitializationException`.
        *   **Suggerimento:** Assicurarsi che le relazioni necessarie siano caricate eager.

#### **72. [`src/main/java/com/dieti/dietiestatesbackend/security/TokenHelper.java`](src/main/java/com/dieti/dietiestatesbackend/security/TokenHelper.java:1)**

*   **Punti di Forza:** Centralizzazione logica JWT, validazione chiave, iniezione `ObjectMapper`.
*   **Aree di Miglioramento e Rischi:**
    *   **Gestione Silenziosa Eccezioni (Critico):** Inghiotte eccezioni JWT in `getClaimFromToken`.
        *   **Suggerimento:** **Propagare le eccezioni** e gestirle a livello superiore.

#### **73. [`src/main/java/com/dieti/dietiestatesbackend/service/AddressService.java`](src/main/java/com/dieti/dietiestatesbackend/service/AddressService.java:1)**

*   **Punti di Forza:** Separazione interfacce, uso di `Optional`.
*   **Aree di Miglioramento e Rischi:**
    *   **Mancanza DTO di Risposta:** Restituisce entità `Address`.
        *   **Suggerimento:** Restituire `Optional<AddressResponse>`.

#### **74. [`src/main/java/com/dieti/dietiestatesbackend/service/AddressServiceImpl.java`](src/main/java/com/dieti/dietiestatesbackend/service/AddressServiceImpl.java:1)**

*   **Punti di Forza:** Implementazione service, transazionalità, integrazione geocoding, gestione `Optional`, eccezione specifica.
*   **Aree di Miglioramento e Rischi:**
    *   **Mappatura Manuale DTO-Entità:** Boilerplate.
        *   **Suggerimento:** Usare MapStruct.
    *   **Impostazione Manuale `createdAt`:** Ridondante.
        *   **Suggerimento:** Rimuovere.
    *   **Nome Campo Inconsistente:** `street_number`.
        *   **Suggerimento:** Rinomina in `streetNumber`.

#### **75. [`src/main/java/com/dieti/dietiestatesbackend/service/AuthenticationService.java`](src/main/java/com/dieti/dietiestatesbackend/service/AuthenticationService.java:1)**

*   **Punti di Forza:** Centralizzazione logica autenticazione, validazione password, transazionalità, `LogoutResult` record.
*   **Aree di Miglioramento e Rischi:**
    *   **Validazione Password Duplicata:**
        *   **Suggerimento:** Affidarsi solo a validazione DTO.
    *   **Gestione Errori Registrazione:** Eccezioni generiche.
        *   **Suggerimento:** Usare eccezioni custom più specifiche.
    *   **Gestione Errori in `logout`:** Cattura `Exception` generica.
        *   **Suggerimento:** Non catturare `Exception`, lasciare propagare.

#### **76. [`src/main/java/com/dieti/dietiestatesbackend/service/AuthService.java`](src/main/java/com/dieti/dietiestatesbackend/service/AuthService.java:1)**

*   **Punti di Forza:** Deprecazione esplicita, non bean.
*   **Aree di Miglioramento e Rischi:**
    *   **Codice Morto (Potenziale):**
        *   **Suggerimento:** Rimuovere completamente se non utilizzato.

#### **77. [`src/main/java/com/dieti/dietiestatesbackend/service/OfferService.java`](src/main/java/com/dieti/dietiestatesbackend/service/OfferService.java:1)**

*   **Punti di Forza:** Implementazione service, transazionalità, gestione `EntityNotFoundException`.
*   **Aree di Miglioramento e Rischi:**
    *   **Mancanza Paginazione/Lazy Loading:** `getAgentOffers` carica tutto in memoria.
        *   **Suggerimento:** Usare paginazione o `Stream`.

#### **78. [`src/main/java/com/dieti/dietiestatesbackend/service/PasswordValidator.java`](src/main/java/com/dieti/dietiestatesbackend/service/PasswordValidator.java:1)**

*   **Punti di Forza:** Complessità password con regex, pattern pre-compilato.
*   **Aree di Miglioramento e Rischi:**
    *   **Validazione Duplicata/Non Utilizzata:**
        *   **Suggerimento:** Rimuovere se la validazione è già sul DTO.

#### **79. [`src/main/java/com/dieti/dietiestatesbackend/service/PropertyDependencyResolver.java`](src/main/java/com/dieti/dietiestatesbackend/service/PropertyDependencyResolver.java:1)**

*   **Punti di Forza:** Eccellente separazione responsabilità, gestione lookup, uso `orElseThrow`, validazione input, iniezione dipendenze.
*   **Aree di Miglioramento e Rischi:**
    *   **`IllegalArgumentException` per Campi Mancanti:**
        *   **Suggerimento:** Lanciare `InvalidPayloadException` o eccezione più specifica.

#### **80. [`src/main/java/com/dieti/dietiestatesbackend/service/PropertyManagementService.java`](src/main/java/com/dieti/dietiestatesbackend/service/PropertyManagementService.java:1)**

*   **Punti di Forza:** Separazione responsabilità, orchestrazione, transazionalità, logging.
*   **Aree di Miglioramento e Rischi:**
    *   **Validazione Duplicata:** `validationService.validate(request)`.
        *   **Suggerimento:** Rimuovere se validazione DTO è sul controller.
    *   **Lancio `IllegalArgumentException`:**
        *   **Suggerimento:** Lanciare `InvalidPayloadException`.

#### **81. [`src/main/java/com/dieti/dietiestatesbackend/service/PropertyQueryService.java`](src/main/java/com/dieti/dietiestatesbackend/service/PropertyQueryService.java:1)**

*   **Punti di Forza:** Adesione CQS, gestione paginazione, delega al repository, uso `PropertySpecifications`, gestione `EntityNotFoundException`.
*   **Aree di Miglioramento e Rischi:**
    *   **Paginazione Liste in Memoria (Critico):** `searchProperties` carica tutti i risultati.
        *   **Suggerimento:** **Modificare il repository per paginare a livello di DB**.
    *   **Normalizzazione Keyword Duplicata:**
        *   **Suggerimento:** Centralizzare il metodo `normalize`.

#### **82. [`src/main/java/com/dieti/dietiestatesbackend/service/PropertyService.java`](src/main/java/com/dieti/dietiestatesbackend/service/PropertyService.java:1)**

*   **Punti di Forza:** Facciata (Facade Pattern), separazione CQS, transazionalità.
*   **Aree di Miglioramento e Rischi:**
    *   **Duplicazione Metodo `normalize`:**
        *   **Suggerimento:** Centralizzare.
    *   **Commenti Obsoleti/Non Necessari:**
        *   **Suggerimento:** Rimuovere.
    *   **`DEFAULT_LEGACY_PAGE_SIZE` Hardcoded:**
        *   **Suggerimento:** Spostare in `application.properties`.

#### **83. [`src/main/java/com/dieti/dietiestatesbackend/service/UserManagementService.java`](src/main/java/com/dieti/dietiestatesbackend/service/UserManagementService.java:1)**

*   **Punti di Forza:** Separazione responsabilità, transazionalità, hashing password, verifica esistenza utente.
*   **Aree di Miglioramento e Rischi:**
    *   **Gestione Errori Registrazione:** Eccezioni generiche.
        *   **Suggerimento:** Usare eccezioni custom più specifiche.
    *   **Validazione Input `createGoogleUser`:** Manuale e duplicata.
        *   **Suggerimento:** Affidarsi a validazione DTO.
    *   **Modello Ruoli Rigido (Riflesso):**
        *   **Suggerimento:** Adattare se il modello di ruoli evolve.
    *   **Password `null` per Utenti Google:** Potenziale rischio sicurezza.
        *   **Suggerimento:** Assicurarsi che campo sia `nullable`, considerare generazione password casuale.

#### **84. [`src/main/java/com/dieti/dietiestatesbackend/service/UserQueryService.java`](src/main/java/com/dieti/dietiestatesbackend/service/UserQueryService.java:1)**

*   **Punti di Forza:** Adesione CQS, delega al repository, uso `Optional`.
*   **Aree di Miglioramento e Rischi:**
    *   **Iniezione `PasswordEncoder` (Violazione SRP):** Service di query non dovrebbe verificare password.
        *   **Suggerimento:** Spostare logica verifica password in `AuthenticationService`.
    *   **Lancio `IllegalArgumentException` per "Not Found":** Generica.
        *   **Suggerimento:** Lanciare `EntityNotFoundException` o `UserNotFoundException`.

#### **85. [`src/main/java/com/dieti/dietiestatesbackend/service/UserService.java`](src/main/java/com/dieti/dietiestatesbackend/service/UserService.java:1)**

*   **Punti di Forza:** Facciata (Facade Pattern), separazione CQS, transazionalità.
*   **Aree di Miglioramento e Rischi:**
    *   **Duplicazione Metodi:** Sovrapposizione con `UserQueryService`/`UserManagementService`.
        *   **Suggerimento:** Valutare se `UserService` aggiunge valore reale o se può essere rimosso.

#### **86. [`src/main/java/com/dieti/dietiestatesbackend/service/ValidationService.java`](src/main/java/com/dieti/dietiestatesbackend/service/ValidationService.java:1)**

*   **Punti di Forza:** Centralizzazione validazione, iniezione `Validator`, messaggi errore aggregati.
*   **Aree di Miglioramento e Rischi:**
    *   **Validazione Duplicata (Critico):** Spesso ridondante con validazione Spring MVC.
        *   **Suggerimento:** Rimuovere se non esegue validazione semantica aggiuntiva.
    *   **Cast Non Controllato:**
        *   **Suggerimento:** Trovare approccio più pulito.
    *   **Lancio `IllegalArgumentException`:** Generica.
        *   **Suggerimento:** Lanciare `InvalidPayloadException`.

#### **87. [`src/main/java/com/dieti/dietiestatesbackend/service/VisitService.java`](src/main/java/com/dieti/dietiestatesbackend/service/VisitService.java:1)**

*   **Punti di Forza:** Implementazione service, transazionalità, gestione `EntityNotFoundException`.
*   **Aree di Miglioramento e Rischi:**
    *   **Mancanza Paginazione/Lazy Loading:** `getAgentVisits` carica tutto in memoria.
        *   **Suggerimento:** Usare paginazione o `Stream`.

#### **88. [`src/main/java/com/dieti/dietiestatesbackend/service/geocoding/Coordinates.java`](src/main/java/com/dieti/dietiestatesbackend/service/geocoding/Coordinates.java:1)**

*   **Punti di Forza:** Uso di `record` (eccellente), `BigDecimal`.
*   **Aree di Miglioramento e Rischi:**
    *   **Mancanza Validazione:**
        *   **Suggerimento:** Aggiungere `@NotNull`, `@DecimalMin`/`@DecimalMax`.

#### **89. [`src/main/java/com/dieti/dietiestatesbackend/service/geocoding/GeocodingService.java`](src/main/java/com/dieti/dietiestatesbackend/service/geocoding/GeocodingService.java:1)**

*   **Punti di Forza:** Disaccoppiamento, gestione `Optional`, eccezione specifica.
*   **Aree di Miglioramento e Rischi:** Nessuna.

#### **90. [`src/main/java/com/dieti/dietiestatesbackend/service/geocoding/MockGeocodingService.java`](src/main/java/com/dieti/dietiestatesbackend/service/geocoding/MockGeocodingService.java:1)**

*   **Punti di Forza:** Utilità per sviluppo/test, conformità interfaccia, gestione `IllegalArgumentException`.
*   **Aree di Miglioramento e Rischi:**
    *   **Comportamento del Mock:** Restituisce sempre coordinate fisse.
        *   **Suggerimento:** Migliorare per simulare scenari più realistici se necessario.
    *   **Mancanza `@Primary`:** Potenziale ambiguità se ci sono più implementazioni.
        *   **Suggerimento:** Aggiungere `@Primary` in profilo dev.

#### **91. [`src/main/java/com/dieti/dietiestatesbackend/service/lookup/AgentLookupService.java`](src/main/java/com/dieti/dietiestatesbackend/service/lookup/AgentLookupService.java:1)**

*   **Punti di Forza:** Separazione interfacce, focalizzazione, uso di `Optional`.
*   **Aree di Miglioramento e Rischi:** Nessuna.

#### **92. [`src/main/java/com/dieti/dietiestatesbackend/service/lookup/AgentLookupServiceImpl.java`](src/main/java/com/dieti/dietiestatesbackend/service/lookup/AgentLookupServiceImpl.java:1)**

*   **Punti di Forza:** Implementazione service, delega al repository, verifica `isAgent`.
*   **Aree di Miglioramento e Rischi:**
    *   **Dipendenza da `User` Concreto/Modello Ruoli Rigido:**
        *   **Suggerimento:** Adattare se il modello di ruoli evolve.

#### **93. [`src/main/java/com/dieti/dietiestatesbackend/service/lookup/CategoryLookupService.java`](src/main/java/com/dieti/dietiestatesbackend/service/lookup/CategoryLookupService.java:1)**

*   **Punti di Forza:** Separazione interfacce, focalizzazione, `@Cacheable` (ottimo per performance), metodi specifici per UI, `@Deprecated`.
*   **Aree di Miglioramento e Rischi:** Nessuna.

#### **94. [`src/main/java/com/dieti/dietiestatesbackend/service/lookup/CategoryLookupServiceImpl.java`](src/main/java/com/dieti/dietiestatesbackend/service/lookup/CategoryLookupServiceImpl.java:1)**

*   **Punti di Forza:** Implementazione service, query JPQL dirette, gestione valori null/vuoti, filtro `isActive`.
*   **Aree di Miglioramento e Rischi:**
    *   **Uso `EntityManager` Diretto:** Accoppia al persistence layer, meno testabile.
        *   **Suggerimento:** Sostituire con `JpaRepository`.
    *   **`findByName` e `getResultList`:** Inefficiente per singolo risultato.
        *   **Suggerimento:** Usare `getSingleResult()` o `getSingleResultOrNull()`.

#### **95. [`src/main/java/com/dieti/dietiestatesbackend/service/lookup/ContractLookupService.java`](src/main/java/com/dieti/dietiestatesbackend/service/lookup/ContractLookupService.java:1)**

*   **Punti di Forza:** Disaccoppiamento, focalizzazione, uso di `Optional`.
*   **Aree di Miglioramento e Rischi:** Nessuna.

#### **96. [`src/main/java/com/dieti/dietiestatesbackend/service/lookup/ContractLookupServiceImpl.java`](src/main/java/com/dieti/dietiestatesbackend/service/lookup/ContractLookupServiceImpl.java:1)**

*   **Punti di Forza:** Implementazione service, delega al repository, gestione valori null/vuoti.
*   **Aree di Miglioramento e Rischi:** Nessuna.

#### **97. [`src/main/java/com/dieti/dietiestatesbackend/specification/PropertySpecifications.java`](src/main/java/com/dieti/dietiestatesbackend/specification/PropertySpecifications.java:1)**

*   **Punti di Forza:** Uso di JPA Specifications (eccellente), `cb.treat` per polimorfismo, fetch eager, costanti per attributi, metodi helper.
*   **Aree di Miglioramento e Rischi:**
    *   **Eccezione Ignorata:** `IllegalArgumentException` in `fetchCommonJoins`.
        *   **Suggerimento:** Rimuovere `try-catch` se non necessario.
    *   **Duplicazione Logica `cb.equal(root.type(), ...)`:**
        *   **Suggerimento:** Creare metodo helper.
    *   **Logica `addCommonPropertyTypeFloorsFilter`:** Potenziale ambiguità.
        *   **Suggerimento:** Chiarire o modificare la logica del filtro.

#### **98. [`src/main/java/com/dieti/dietiestatesbackend/util/DaemonThreadFactory.java`](src/main/java/com/dieti/dietiestatesbackend/util/DaemonThreadFactory.java:1)**

*   **Punti di Forza:** Uso corretto di `ThreadFactory`, Daemon Threads.
*   **Aree di Miglioramento e Rischi:** Nessuna.

#### **99. [`src/main/java/com/dieti/dietiestatesbackend/util/PropertyImageUtils.java`](src/main/java/com/dieti/dietiestatesbackend/util/PropertyImageUtils.java:1)**

*   **Punti di Forza:** Separazione responsabilità, configurazione esternalizzata.
*   **Aree di Miglioramento e Rischi:**
    *   **Percorso Base Immagini Hardcoded (Critico):** Accoppiamento ambientale.
        *   **Suggerimento:** Esternalizzare in variabile d'ambiente o astrarre lo storage.
    *   **Ridondanza Campi `Value`:** Copia in campi `Value` separati.
        *   **Suggerimento:** Usare direttamente i campi iniettati da `@Value`.

#### **100. [`src/main/java/com/dieti/dietiestatesbackend/validation/EntityExistenceChecker.java`](src/main/java/com/dieti/dietiestatesbackend/validation/EntityExistenceChecker.java:1)**

*   **Punti di Forza:** Separazione responsabilità, disaccoppiamento.
*   **Aree di Miglioramento e Rischi:** Nessuna.

#### **101. [`src/main/java/com/dieti/dietiestatesbackend/validation/ExistingEntity.java`](src/main/java/com/dieti/dietiestatesbackend/validation/ExistingEntity.java:1)**

*   **Punti di Forza:** Validazione semantica dichiarativa, riutilizzabilità, messaggio personalizzabile.
*   **Aree di Miglioramento e Rischi:** Nessuna.

#### **102. [`src/main/java/com/dieti/dietiestatesbackend/validation/ExistingEntityValidator.java`](src/main/java/com/dieti/dietiestatesbackend/validation/ExistingEntityValidator.java:1)**

*   **Punti di Forza:** Separazione responsabilità, iniezione dipendenze, gestione `null`, messaggi personalizzati.
*   **Aree di Miglioramento e Rischi:**
    *   **Gestione Eccezioni in `exists` (Critico):** Inghiotte `Exception` generica.
        *   **Suggerimento:** Non inghiottire, loggare e propagare o gestire a livello superiore.

#### **103. [`src/main/java/com/dieti/dietiestatesbackend/validation/JpaEntityExistenceChecker.java`](src/main/java/com/dieti/dietiestatesbackend/validation/JpaEntityExistenceChecker.java:1)**

*   **Punti di Forza:** Implementazione JPA, uso Criteria API, transazionalità, `@Component`.
*   **Aree di Miglioramento e Rischi:** Nessuna.

---

### **Riepilogo delle Falle e Giudizio Finale**

L'analisi esaustiva di ogni singolo file conferma e rafforza le conclusioni iniziali. Il progetto ha una **solida base di design**, con molte scelte architetturali appropriate e l'adozione di pattern avanzati. Tuttavia, è frenato da **criticità significative** che ne compromettono la robustezza, la sicurezza e la manutenibilità a lungo termine.

Le **aree di intervento più urgenti** rimangono:

1.  **Mancanza di Test Automatici:** Questa è la **singola più grande debolezza** e il **maggiore debito tecnico**. Rende il progetto estremamente fragile e costoso da evolvere.
2.  **Problemi di Sicurezza:**
    *   **Assenza di CORS:** Espone l'applicazione a problemi di comunicazione con i frontend e potenziali vulnerabilità.
    *   **Gestione Silenziosa delle Eccezioni JWT:** Difficoltà di debugging e scarsa esperienza utente.
    *   **Password Deboli:** Validazione insufficiente per le password.
3.  **Violazioni dei Principi SOLID e DRY:**
    *   **"God Object" (`PropertiesController`):** Eccessiva responsabilità e accoppiamento.
    *   **Modello di Ruoli Rigido (`User`):** Non scalabile e difficile da estendere.
    *   **Duplicazione di Logica/Codice:** Getter/setter manuali, logica di mapping duplicata, query JPQL ripetute, validazione duplicata.
    *   **Mutabilità dei DTO/Value Objects:** Rischio di effetti collaterali.
4.  **Accoppiamento Ambientale Diretto:** Percorsi file hardcoded, fuso orario nei log.
5.  **Robustezza della Connettività DB:** Warning HikariCP.

Il progetto **non è pronto per uno sviluppo professionale a lungo termine né per un'implementazione in produzione stabile e sicura** senza un intervento significativo. La priorità assoluta dovrebbe essere l'implementazione di una robusta suite di test, seguita dalla risoluzione delle vulnerabilità di sicurezza e dei problemi di design che generano debito tecnico.