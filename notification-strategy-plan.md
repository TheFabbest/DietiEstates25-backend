# Piano Strategico per il Sistema di Notifiche

**Stato Attuale:** `IN ATTESA DI DECISIONE`

**Nota Importante:** L'implementazione di questo task è sospesa in attesa di un allineamento interno del team di sviluppo per decidere quale approccio architetturale adottare. Questo documento riassume le opzioni valutate per facilitare tale decisione.

---

## 1. Obiettivo

Sviluppare una strategia completa e ottimizzata per le notifiche agli utenti finali (clienti e agenti) dell'applicazione DietiEstates, considerando canali, tempistiche, esperienza utente, costi, scalabilità e indipendenza da servizi esterni.

## 2. Opzioni Architetturali Valutate

Di seguito sono presentate le diverse strategie discusse, con i relativi pro, contro e una stima approssimativa dei tempi di implementazione.

### Opzione A: Polling Semplice
- **Descrizione:** Il client, solo quando l'app è aperta e in primo piano, interroga periodicamente (es. ogni 30 secondi) un endpoint del backend per sapere se ci sono nuove notifiche.
- **Pro:** Massima semplicità implementativa, nessuna dipendenza esterna.
- **Contro:** Funziona solo ad app aperta. Nessuna notifica in background o ad app chiusa.
- **Tempo Stimato:** 2 - 3 giorni.

### Opzione B: Polling con Background Fetch
- **Descrizione:** Simile all'Opzione A, ma sfrutta la funzionalità `Background Fetch` di Expo per eseguire il polling anche quando l'app è in background (ridotta a icona).
- **Pro:** Notifiche anche ad app in background, nessuna dipendenza da servizi esterni (FCM/APNs).
- **Contro:** Non affidabile al 100% quando l'app è chiusa forzatamente dall'utente. Le notifiche non sono in tempo reale (ritardo di min. 15 minuti).
- **Tempo Stimato:** 3 - 5 giorni.

### Opzione C: Notifiche Push Native (FCM/APNs)
- **Descrizione:** Il backend invia le notifiche ai server di Google (FCM) e Apple (APNs), che le consegnano istantaneamente ai dispositivi.
- **Pro:** Soluzione più robusta e affidabile. Consegna istantanea in qualsiasi stato dell'app (aperta, background, chiusa).
- **Contro:** Dipendenza obbligatoria da servizi esterni (Google/Apple). Complessità di configurazione (token, certificati, API).
- **Tempo Stimato:** 5 - 9 giorni.

### Opzione D: Notifiche via Email (SMTP)
- **Descrizione:** Il backend invia una notifica email all'indirizzo dell'utente.
- **Pro:** Canale universale e indipendente dallo stato dell'app. Implementazione rapida lato backend.
- **Contro:** Esperienza utente meno immediata. Problemi di deliverability (spam). Nessuna notifica *dentro* l'app.
- **Tempo Stimato:** 2.5 - 4 giorni.

### Opzione E: Approccio Ibrido (C + B)
- **Descrizione:** Combina le Notifiche Push Native (C) come canale primario con il Polling/Background Fetch (B) come meccanismo di fallback e per le notifiche in-app.
- **Pro:** Soluzione molto robusta che copre quasi tutti gli scenari. Ottima esperienza utente.
- **Contro:** Elevata complessità. Dipendenza da FCM/APNs.
- **Tempo Stimato:** 6 - 10 giorni.

### Opzione F: Approccio Multi-Canale Resiliente (E + D)
- **Descrizione:** L'approccio più completo. Utilizza le Notifiche Push (C), il Polling/Background Fetch (B) e le Email (D) come canali complementari.
- **Pro:** Massima resilienza e copertura. Eccellente dal punto di vista didattico e per "giustificare" ogni scelta architetturale.
- **Contro:** Massima complessità implementativa. Rischio di sovraccaricare l'utente se non gestito con attenzione.
- **Tempo Stimato:** 8 - 13 giorni.

## 3. Riepilogo Comparativo

| Approccio | Complessità | Tempo Stimato | Copertura App Chiusa | Dipendenze Esterne |
| :--- | :---: | :---: | :---: | :---: |
| A. Polling Semplice | Bassa | 2-3gg | No | Nessuna |
| B. Background Fetch | Media | 3-5gg | Parziale (Non affidabile) | Nessuna |
| C. Push Native | Alta | 5-9gg | **Sì (Affidabile)** | **Sì (FCM/APNs)** |
| D. Email (SMTP) | Bassa-Media | 2.5-4gg | Sì (via Email) | **Sì (Provider SMTP)** |
| E. Ibrido (Push+Fetch) | Alta | 6-10gg | **Sì (Affidabile)** | **Sì (FCM/APNs)** |
| F. Multi-Canale | Molto Alta | 8-13gg | **Sì (Affidabile)** | **Sì (FCM/APNs, SMTP)** |

## 4. Prossimi Passi

1.  **Discussione interna del team** per analizzare i compromessi e selezionare l'opzione architetturale più adatta agli obiettivi e ai vincoli del progetto.
2.  Comunicare la decisione per sbloccare il task.
3.  Procedere con l'aggiornamento della `TODO list` e l'inizio dell'implementazione.