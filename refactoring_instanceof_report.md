# Report di Analisi e Refactoring: Uso di `instanceof`

Questo documento analizza l'uso dell'operatore `instanceof` in specifiche classi di sicurezza del progetto DietiEstatesBackend. Per ogni occorrenza, viene valutata la sua aderenza ai principi SOLID e di Clean Code, proponendo soluzioni di refactoring dove necessario.

## 2. Analisi di `SecurityUtil.java`

**Contesto:** La classe `SecurityUtil` viene utilizzata nelle espressioni SpEL di `@PreAuthorize` per centralizzare la logica di autorizzazione a livello di risorsa.

**Occorrenza di `instanceof`:**
```java
// src/main/java/com/dieti/dietiestatesbackend/security/SecurityUtil.java:122
private AppPrincipal asAppPrincipal(Object principalObj) {
    if (principalObj instanceof AppPrincipal) {
        return (AppPrincipal) principalObj;
    }
    // ...
    return null;
}
```

### 2.1. Valutazione (Code Smell)

L'uso di `instanceof` in questo contesto è un chiaro **code smell** e una violazione del **Principio Open/Closed (OCP)** e del **Dependency Inversion Principle (DIP)**, come correttamente identificato nella "Fase 1, Problematica 2.1" del `strategic-plan.md`.

*   **Violazione OCP:** Se in futuro si volesse introdurre un nuovo tipo di `principal` che non implementa `AppPrincipal`, si sarebbe costretti a modificare il metodo `asAppPrincipal`, aggiungendo un nuovo blocco `else if`. Questo rende la classe "aperta alla modifica".
*   **Violazione DIP:** Il metodo dipende da una possibile implementazione concreta (`instanceof X`), anche se cerca di astrarre tramite `AppPrincipal`. La logica di Spring Security è già basata su interfacce (`Authentication`, `UserDetails`), e il codice dovrebbe allinearsi a questo pattern, dipendendo solo da astrazioni.

In questo caso specifico, il codice è stato parzialmente refattorizzato per usare l'interfaccia `AppPrincipal`, ma il controllo `instanceof` rimane come un residuo del design precedente. La soluzione corretta è garantire che l'oggetto `principal` inserito nel `SecurityContext` da `JwtAuthenticationFilter` sia *sempre* un'istanza di `AppPrincipal`.

### 2.2. Proposta di Refactoring

La soluzione, già delineata nel piano strategico, consiste nel garantire che il `principal` nel contesto di sicurezza sia sempre del tipo `AppPrincipal`. Questo elimina la necessità di qualsiasi controllo di tipo o cast in `SecurityUtil`.

**Passaggi:**

1.  **Garantire il Tipo in `JwtAuthenticationFilter`:** Il filtro, dopo aver validato il token, deve costruire un DTO `AuthenticatedUser` (che implementa `AppPrincipal`) e usarlo come `principal` nel `UsernamePasswordAuthenticationToken`. Questo è già stato implementato, rendendo il controllo in `SecurityUtil` ridondante e sicuro da rimuovere.

2.  **Semplificare `SecurityUtil`:** Il metodo `asAppPrincipal` può essere eliminato. I metodi pubblici accetteranno direttamente un `AppPrincipal` che Spring SpEL estrarrà dall'oggetto `Authentication`.

**Codice Rifattorizzato:**

```java
// SecurityUtil.java semplificato
@Component("securityUtil")
public class SecurityUtil {

    // ... costruttore e dipendenze

    // L'argomento principal viene iniettato direttamente da SpEL
    // con #authentication.principal
    public boolean canAccessProperty(AppPrincipal principal, Long propertyId) {
        if (principal == null) {
            return false;
        }
        if (principal.isManager()) {
            return true;
        }
        // ... resto della logica
    }

    // ... altri metodi che ricevono AppPrincipal direttamente
}
```

**Aggiornamento dell'espressione SpEL:**

Le chiamate a `@PreAuthorize` andranno modificate per passare direttamente il `principal`.

*   **Prima:** `@PreAuthorize("@securityUtil.canAccessProperty(principal, #propertyId)")`
*   **Dopo:** `@PreAuthorize("@securityUtil.canAccessProperty(#authentication.principal, #propertyId)")`

### 2.3. Vantaggi del Refactoring

*   **Aderenza a OCP e DIP:** `SecurityUtil` ora dipende esclusivamente dall'astrazione `AppPrincipal`, senza conoscere le implementazioni concrete.
*   **Codice più Pulito e Sicuro:** Si elimina un controllo di tipo e un cast, riducendo la complessità e il rischio di `ClassCastException` se il contesto di sicurezza venisse configurato in modo errato.
*   **Maggiore Chiarezza:** Il "contratto" dei metodi di `SecurityUtil` diventa esplicito: richiedono un `AppPrincipal`.

## 3. Analisi di `AccessTokenProvider.java`

**Contesto:** Questa classe è responsabile della generazione e della validazione dei token JWT. I metodi analizzati estraggono i *claims* dal token dopo la validazione.

**Occorrenze di `instanceof`:**
```java
// src/main/java/com/dieti/dietiestatesbackend/security/AccessTokenProvider.java:73
if (v instanceof Number) { ... }

// src/main/java/com/dieti/dietiestatesbackend/security/AccessTokenProvider.java:88
if (v instanceof Boolean) { ... }
if (v instanceof String) { ... }

// src/main/java/com/dieti/dietiestatesbackend/security/AccessTokenProvider.java:107
if (v instanceof List) { ... }
```

### 3.1. Valutazione (Uso Giustificato ma Migliorabile)

A differenza del caso di `SecurityUtil`, qui l'uso di `instanceof` non è un *grave* code smell, ma piuttosto un **uso difensivo giustificato ai confini del sistema**. Un token JWT è un dato esterno, potenzialmente manipolato, e la libreria `jjwt` (correttamente) restituisce i claims come `Object`. È quindi responsabilità del nostro codice validare e convertire i tipi in modo sicuro.

Tuttavia, questa implementazione manuale presenta alcuni svantaggi:
*   **Verbosa e Ripetitiva:** La logica di controllo del tipo e cast è ripetuta per ogni claim.
*   **Fragile:** Se la libreria JWT cambiasse il modo in cui rappresenta un tipo (es. un numero da `Integer` a `Long`), il codice potrebbe fallire.
*   **Accoppiamento Forte:** La classe `AccessTokenProvider` è strettamente accoppiata alla struttura interna dei claims.

### 3.2. Proposta di Miglioramento

Una soluzione più robusta, elegante e manutenibile consiste nel delegare la deserializzazione dei claims a un **DTO (Data Transfer Object)**, sfruttando la potenza della libreria Jackson (già presente nel progetto).

**Passaggi:**

1.  **Creare un DTO per i Claims:** Creare una classe `JwtClaims` che mappa la struttura del payload del nostro JWT.

    ```java
    import com.fasterxml.jackson.annotation.JsonProperty;
    import java.util.List;

    // Classe immutabile e pulita per rappresentare i claims
    public class JwtClaims {

        @JsonProperty("id")
        private final Long id;

        @JsonProperty("isManager")
        private final boolean isManager;

        @JsonProperty("roles")
        private final List<String> roles;
        
        // Aggiungere il nome utente che è il "subject" del token
        @JsonProperty("sub")
        private final String username;


        // Costruttore, getter, equals, hashCode, toString
        // ... generati dall'IDE o scritti a mano
    }
    ```

2.  **Rifattorizzare `AccessTokenProvider`:** Modificare i metodi di estrazione per deserializzare l'intero payload in un oggetto `JwtClaims` in un solo passaggio.

    ```java
    import com.fasterxml.jackson.databind.ObjectMapper;
    import io.jsonwebtoken.Claims;

    public class AccessTokenProvider {
        
        private static final ObjectMapper objectMapper = new ObjectMapper();

        // ...

        private static JwtClaims getClaims(String token) {
            TokenHelper th = new TokenHelper(SECRET_KEY);
            Claims claims = th.getAllClaimsFromToken(token); // Assumendo che TokenHelper esponga questo metodo
            return objectMapper.convertValue(claims, JwtClaims.class);
        }

        public static Long getIdFromToken(String token) {
            try {
                return getClaims(token).getId();
            } catch (Exception e) {
                return null;
            }
        }

        public static Boolean getIsManagerFromToken(String token) {
            try {
                return getClaims(token).isManager();
            } catch (Exception e) {
                return null;
            }
        }

        // ... e così via per gli altri metodi
    }
    ```

### 3.3. Vantaggi del Refactoring

*   **Robustezza e Sicurezza dei Tipi:** La deserializzazione è gestita da Jackson, una libreria matura e testata. Eventuali problemi di tipo o campi mancanti vengono gestiti in un unico punto, lanciando un'eccezione controllata.
*   **Codice Semplice e Dichiarativo:** La struttura dei claims è definita in modo dichiarativo nel DTO. I metodi di `AccessTokenProvider` diventano semplici getter, eliminando la logica di controllo manuale.
*   **Disaccoppiamento:** La conoscenza della struttura dei claims è incapsulata nel DTO `JwtClaims`, non sparsa nei metodi di `AccessTokenProvider`.
*   **Manutenibilità:** Se si aggiunge un nuovo claim al token, basta aggiungere un campo al DTO. Non è necessario modificare la logica di parsing.

Questo approccio rappresenta un trade-off positivo: introduce una nuova classe (`JwtClaims`), ma semplifica drasticamente la logica esistente, rendendola più sicura e facile da mantenere.

## 4. Analisi di `AuthenticatedUser.java`

**Contesto:** Questa classe è un DTO che implementa `AppPrincipal` e rappresenta l'utente autenticato nel `SecurityContext` di Spring.

**Occorrenza di `instanceof`:**
```java
// src/main/java/com/dieti/dietiestatesbackend/security/AuthenticatedUser.java:43
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AuthenticatedUser)) return false;
    AuthenticatedUser that = (AuthenticatedUser) o;
    return Objects.equals(id, that.id);
}
```

### 4.1. Valutazione (Pratica Standard e Corretta)

In questo contesto, l'uso di `instanceof` all'interno del metodo `equals(Object o)` **non è un code smell**, ma al contrario, è la **pratica standard e universalmente accettata** per implementare correttamente il contratto del metodo `equals` definito nella classe `java.lang.Object`.

Il contratto di `equals` richiede che:
1.  Sia riflessivo: `x.equals(x)` deve restituire `true`.
2.  Sia simmetrico: `x.equals(y)` deve restituire `true` se e solo se `y.equals(x)` restituisce `true`.
3.  Sia transitivo: se `x.equals(y)` e `y.equals(z)` sono `true`, allora `x.equals(z)` deve essere `true`.
4.  Sia consistente: chiamate multiple devono restituire lo stesso risultato.
5.  Per qualsiasi riferimento non nullo `x`, `x.equals(null)` deve restituire `false`.

Il pattern implementato in `AuthenticatedUser` soddisfa tutti questi requisiti:
*   Il controllo `if (this == o)` gestisce l'identità referenziale.
*   Il controllo `if (!(o instanceof AuthenticatedUser))` (o una sua variante come `if (o == null || getClass() != o.getClass())`) è essenziale per garantire la simmetria e per gestire il caso `null`, evitando una `ClassCastException`. Senza questo controllo, il metodo violerebbe il contratto di base di `equals`.

### 4.2. Proposta di Refactoring

**Nessun refactoring è necessario o raccomandato.**

L'implementazione attuale è corretta, sicura e aderente alle best practice di Java per la sovrascrittura del metodo `equals`. Qualsiasi tentativo di rimuovere `instanceof` da questo specifico contesto porterebbe probabilmente a un'implementazione errata o meno robusta.

## 5. Conclusioni e Riepilogo

L'analisi ha evidenziato tre contesti d'uso distinti per `instanceof`:

1.  **`SecurityUtil.java` (Code Smell):** L'uso violava i principi SOLID. Il refactoring proposto, basato sulla garanzia di tipo a monte nel filtro di autenticazione, elimina la necessità del controllo e migliora design e manutenibilità.
2.  **`AccessTokenProvider.java` (Migliorabile):** L'uso era difensivo e giustificato ai confini del sistema, ma la logica manuale di parsing dei claims è stata identificata come verbosa e fragile. Il refactoring verso un DTO (`JwtClaims`) per la deserializzazione automatica con Jackson è raccomandato per aumentare robustezza e pulizia del codice.
3.  **`AuthenticatedUser.java` (Corretto):** L'uso all'interno del metodo `equals` è una pratica standard e necessaria per rispettare il contratto di `Object.equals`. Non rappresenta un code smell e non richiede alcuna modifica.

Questa analisi dimostra come la valutazione di un pattern di codice come `instanceof` dipenda fortemente dal contesto in cui viene applicato.
