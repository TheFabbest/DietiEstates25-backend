# Roadmap Progetto DietiEstates

Questo documento delinea la roadmap di sviluppo del progetto, organizzata in Sprint 1 e Product Backlog.

**Team:** Luca, Fabrizio e Mari

---

## Assegnazione Task e Stato Lavori

Per evitare sovrapposizioni, ogni membro del team può assegnarsi una task dal backlog o dallo sprint corrente riportandola nella propria tabella qui sotto.

### Luca

| Task                                                        | Stato        |
| :---                                                        | :---         |
| Copiare backend e configurare database su Azure              | Completato   |

### Fabrizio

| Task | Stato |
| :--- | :--- |
|      | Da Iniziare / In Corso / Completato |

### Mari

| Task | Stato |
| :--- | :--- |
|      | Da Iniziare / In Corso / Completato |

---

### Sprint 1: Fondamenta del Progetto

- [ ] Sistemare diagramma dei casi d’uso (aggiungere visite, controllare admin e agenti)
- [ ] Sistemare diagramma UML (aggiungere visite, controllare filtri)
- [x] Copiare backend e configurare database su Azure
- [ ] Modificare il backend in base all’UML
- [ ] Modificare i mockup in base ai nuovi diagrammi
- [ ] Modificare il frontend in base ai mockup
- [ ] Sistemare interfaccia tra frontend e backend
- [ ] Implementare e ottimizzare i filtri di ricerca
- [ ] Implementare scorrimento infinito per i risultati di ricerca
- [ ] Refactor del frontend per manutenibilità
- [ ] Testare le funzionalità principali del backend
- [ ] Testare le funzionalità principali del frontend

---

### Product Backlog

#### Documentazione e Progettazione
- [ ] Aggiornare la documentazione (Casi d'Uso, Diagramma delle classi (normale e ristrutturato)) per includere la gestione delle "Visite"
- [ ] Definire le specifiche per la creazione di nuovi utenti (Admin/Agenti)

#### Backend
- [ ] Implementare la gestione delle "Visite" (creazione, modifica, conferma/rifiuto)
- [ ] Implementare la gestione delle "Offerte" (creazione, accettazione, rifiuto)
- [ ] Implementare la funzionalità per inserire offerte ricevute esternamente
- [ ] (Opzionale) Rifattorizzare la ricerca per consentire un raggio arbitrario
- [ ] Scrivere test unitari e di integrazione completi per il backend

#### Frontend
- [ ] Integrare i filtri di ricerca con le nuove API del backend
- [ ] Implementare l'interfaccia per la cronologia delle ricerche
- [ ] Sviluppare la UI per la visualizzazione dei dati meteo da OpenMeteo
- [ ] Creare una mappa interattiva con "pin" per gli immobili nei risultati di ricerca
- [ ] Migliorare la UI della sezione "Visite"
- [ ] Creare la schermata per accettare/rifiutare le offerte
- [ ] Integrare Geoapify per la geolocalizzazione
- [ ] Scrivere test per il frontend (e.g., con Jest/Cypress)

#### Funzionalità Cross-Cutting e Autenticazione
- [ ] Separare chiaramente le interfacce e le funzionalità tra Agente e Admin
- [ ] Implementare l'autenticazione tramite Google
- [ ] Implementare l'autenticazione tramite Facebook o GitHub
- [ ] Sviluppare la funzionalità per cui un Admin può creare nuovi Admin/Agenti
- [ ] Inviare email di notifica all'agente per nuove proposte di visita
- [ ] Inviare email di notifica all'utente per conferma/rifiuto della visita
- [ ] Integrare un calendario (es. link .ics) nell'email di notifica per l'agente