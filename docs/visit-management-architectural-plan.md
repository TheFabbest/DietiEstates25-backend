# Piano Architetturale per la Gestione delle Autorizzazioni delle Visite

## 1. Introduzione

Questo documento delinea il piano architetturale per il refactoring del sistema di autorizzazioni relativo alla gestione delle visite. L'obiettivo è risolvere le incoerenze, le vulnerabilità e la duplicazione della logica emerse dalla code review, creando un sistema sicuro, manutenibile e basato sul principio del minimo privilegio.

## 2. Principi Architetturali

La nuova architettura si baserà sui seguenti principi:

1.  **Single Source of Truth (SSOT)**: La logica di autorizzazione basata su dati risiederà esclusivamente nella classe `SecurityUtil`.
2.  **Autorizzazione Dichiarativa**: I controller utilizzeranno solo annotazioni `@PreAuthorize` per la sicurezza, mantenendo i service agnostici.
3.  **Principio del Minimo Privilegio**: Ogni endpoint esporrà solo le informazioni necessarie agli utenti strettamente autorizzati.
4.  **Centralizzazione dell'Identità**: L'accesso all'identità dell'utente avverrà in modo standard e sicuro solo dove necessario.
5.  **Gestione degli Errori Coerente**: Le violazioni di accesso risulteranno in una risposta HTTP `403 Forbidden` standard, gestita da Spring Security.

## 3. Strategia di Implementazione

L'implementazione seguirà tre direttrici:

1.  **Refactoring del `VisitService`**: Verrà rimossa tutta la logica di autorizzazione manuale, le dipendenze da `SecurityContextHolder` e `AppPrincipal`, e i metodi di utilità per l'accesso all'utente autenticato.
2.  **Potenziamento del `SecurityUtil`**: Diventerà il centro della logica di autorizzazione con la creazione/modifica dei seguenti metodi:
    *   `canCreateVisit(AppPrincipal principal, VisitCreationRequestDTO request)`
    *   `canUpdateVisitStatus(AppPrincipal principal, Long visitId, VisitStatus newStatus)`
    *   `canViewPropertyVisits(AppPrincipal principal, Long propertyId)`
3.  **Aggiornamento del `VisitController`**: Le annotazioni `@PreAuthorize` saranno aggiornate per delegare tutti i controlli complessi a `SecurityUtil`.

## 4. Diagramma di Flusso delle Autorizzazioni

```mermaid
sequenceDiagram
    participant User
    participant VisitController
    participant SpringSecurity as @PreAuthorize
    participant SecurityUtil
    participant VisitService

    User->>+VisitController: Request (e.g., PUT /visits/{id}/status)
    VisitController->>+SpringSecurity: Check @PreAuthorize
    SpringSecurity->>+SecurityUtil: Call authorization method (e.g., canUpdateVisitStatus)
    SecurityUtil-->>-SpringSecurity: Return true/false
    alt Access Granted
        SpringSecurity-->>-VisitController: Proceed
        VisitController->>+VisitService: Call business method
        VisitService-->>-VisitController: Return result
        VisitController-->>-User: HTTP 200 OK
    else Access Denied
        SpringSecurity-->>-VisitController: Throw AccessDeniedException
        VisitController-->>-User: HTTP 403 Forbidden
    end
```

## 5. Matrice delle Autorizzazioni

| Endpoint | Metodo HTTP | Ruoli Ammessi | Regola `@PreAuthorize` Proposta |
| :--- | :--- | :--- | :--- |
| `/visits` | `POST` | `USER` | `hasRole('USER') and @securityUtil.canCreateVisit(authentication.principal, #visitRequest)` |
| `/visits/{id}/status` | `PUT` | `USER`, `AGENT`, `MANAGER` | `@securityUtil.canUpdateVisitStatus(authentication.principal, #visitId, #statusRequest.status)` |
| `/visits/agent/{agentID}` | `GET` | `AGENT`, `MANAGER` | `@securityUtil.canViewAgentRelatedEntities(#agentID)` |
| `/visits/me/` | `GET` | `USER`, `AGENT`, `MANAGER` | `isAuthenticated()` |
| `/properties/{propertyId}/visits` | `GET` | `AGENT`, `MANAGER` | `@securityUtil.canViewPropertyVisits(authentication.principal, #propertyId)` |

## 6. Gestione degli Errori e Testabilità

*   **Errori**: Le eccezioni di autorizzazione saranno gestite da Spring Security, risultando in risposte `403 Forbidden`. Il logging dettagliato in `SecurityUtil` supporterà il debug.
*   **Test**:
    *   **Unit Test**: Verrà creata la classe `SecurityUtilTest` per testare ogni scenario di autorizzazione in isolamento, usando Mockito.
    *   **Integration Test**: I test di integrazione sul `VisitController` useranno `MockMvc` e `@WithMockUser` per verificare che gli endpoint rispondano correttamente (200 OK o 403 Forbidden) in base ai permessi dell'utente.
