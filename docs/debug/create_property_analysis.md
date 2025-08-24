Analisi rapido — errore JSON malformato durante POST /properties

Osservazione principale
- Dal log: com.fasterxml.jackson.core.JsonParseException: Unexpected character ('p'): was expecting double-quote to start field name.
- Questo indica che il corpo arrivato al server iniziava con una chiave non racchiusa tra doppie virgolette, es. { propertyType: RESIDENTIAL, ... } invece di { "propertyType": "RESIDENTIAL", ... }.

Probabile causa
- Problema di quoting/escaping lato client quando si è usato heredoc o quando si è passato il JSON inline a curl.
- Anche se abbiamo usato heredoc con <<'JSON', qualcosa nell'invocazione (bash -lc + quoting) ha alterato l'input o rimosso le virgolette, quindi il body è stato inviato come testo non-JSON valido.

Conseguenze
- Jackson rifiuta la richiesta con 400 e messaggio "Il corpo della richiesta non è leggibile o è malformato".
- Nessuna chiamata di business logic è stata eseguita (PropertyService non è stato eseguito oltre il parsing).

Azioni raccomandate (rapide)
1) Evitare heredoc/escaping complessi: salvare il JSON in file e inviare con curl -d @file.json.
2) Verificare Content-Type: application/json.
3) Se preferisci inviare inline, assicurarsi di usare correttamente le virgolette esterne in shell (meglio usare single-quote esterne e doppie interne o file).

Esempio: creare file di test `residential.json` (salvalo nella root del progetto)
{
  "propertyType":"RESIDENTIAL",
  "agentUsername":"testuser",
  "description":"Appartamento di prova",
  "price":120000,
  "area":80,
  "yearBuilt":1998,
  "contractType":"SALE",
  "propertyCategoryName":"Appartamento",
  "addressRequest":{
    "country":"IT",
    "province":"RM",
    "city":"Rome",
    "street":"Via Test",
    "streetNumber":"1"
  },
  "images":["/img/1.jpg","/img/2.jpg"],
  "numeroLocali":3,
  "numeroBagni":2,
  "postiAuto":1,
  "heatingType":"GAS",
  "giardino":"NO",
  "numeroPianiTotali":3,
  "haAscensore":false
}

Comando sicuro (usa il file)
curl -i -X POST http://localhost:8080/properties \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -d @residential.json

Ulteriori verifiche se l'errore persiste
- Log del server: cerca la riga con JSON parse error (già presente: line:1, column:4).
- Stampare esattamente il body che arriva al server (aggiungere un logging temporaneo nel controller o usare tcpdump/proxy).
- Controllare che nessun filtro/proxy modifichi il body (Content-Encoding, middleware).

Prossimi passi proposti (scegli uno)
- Vuoi che crei io il file `residential.json` nel repo e riprovi la richiesta dal terminale del progetto (posso eseguire il curl e riportare output + log)? 
- Preferisci eseguire tu il comando con il file; incollami il risultato. 
- Vuoi che aggiunga un logging temporaneo nel controller per dumpare il raw request body (posso creare la patch)?

Riferimenti:
- DTO: src/main/java/com/dieti/dietiestatesbackend/dto/request/CreatePropertyRequest.java:31
- Service orchestration: src/main/java/com/dieti/dietiestatesbackend/service/PropertyService.java:111