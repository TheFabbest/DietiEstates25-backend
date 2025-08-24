# Piano di Configurazione del Logging in Spring Boot

Questo documento descrive i passaggi necessari per configurare un sistema di logging robusto per l'applicazione Spring Boot, in base ai requisiti specificati.

## 1. Modifiche a `src/main/resources/application.properties`

Aggiungere le seguenti proprietà per configurare il logging su file. Questo abiliterà un file di log chiamato `app.log` nella directory `./logs` (relativa alla posizione di esecuzione del JAR).

```properties
# ===================================================================
# LOGGING CONFIGURATION
# ===================================================================
# Livello di logging per il root logger
logging.level.root=INFO

# Percorso del file di log
logging.file.name=logs/app.log

# Pattern per il logging su file con fuso orario Europe/Rome (UTC+2)
# Nota: Spring Boot usa il pattern di Logback. La conversione del fuso orario è gestita qui.
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss.SSS, Europe/Rome} [%thread] %-5level %logger{36} - %msg%n

# Pattern per il logging su console (mantenuto per l'output di sviluppo)
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS, Europe/Rome} [%thread] %-5level %logger{36} - %msg%n
```

**Spiegazione:**
*   `logging.file.name`: Specifica il percorso e il nome del file di log. La directory `logs` verrà creata automaticamente.
*   `logging.pattern.file`: Definisce il formato dei messaggi di log. `%d{..., Europe/Rome}` garantisce che i timestamp siano nel fuso orario di Roma.
*   `logging.level.root=INFO`: Imposta il livello di log predefinito a INFO.

## 2. Modifiche a `runLocally.sh` per la Persistenza dei Log

Per garantire che i log generati all'interno del container Docker siano salvati sulla macchina host, è necessario mappare un volume. Modificare il comando `docker run` nel file `runLocally.sh` aggiungendo il flag `-v`.

**Modifica da apportare a `runLocally.sh`:**

```bash
# Aggiungere questa riga al comando 'docker run'

-v "$(pwd)/logs":/app/logs \
```

**Esempio del comando `docker run` aggiornato:**
```bash
#!/bin/bash
docker build --no-cache --tag 'dietibackend' .

docker run --rm -p 8080:8080 \
    -v "$(pwd)/logs":/app/logs \
    -e DB_URL="jdbc:postgresql://dieti-estates.postgres.database.azure.com:5432/dieti_estates" \
    -e DB_USERNAME="lucabarrella" \
    # ... (altre variabili d'ambiente) ...
    dietibackend
```

**Spiegazione:**
*   `-v "$(pwd)/logs":/app/logs`: Questo comando mappa la directory `logs` nella cartella corrente dell'host (`$(pwd)/logs`) alla directory `/app/logs` all'interno del container, dove Spring Boot scriverà il file `app.log`.

## 3. Sostituzione di `java.util.logging.Logger`

È necessario sostituire l'uso di `java.util.logging.Logger` con l'interfaccia `org.slf4j.Logger` e la factory `org.slf4j.LoggerFactory`. Questa modifica deve essere applicata ai seguenti 10 file:

1.  `src/main/java/com/dieti/dietiestatesbackend/controller/AuthController.java`
2.  `src/main/java/com/dieti/dietiestatesbackend/service/AuthService.java`
3.  `src/main/java/com/dieti/dietiestatesbackend/service/VisitService.java`
4.  `src/main/java/com/dieti/dietiestatesbackend/service/OfferService.java`
5.  `src/main/java/com/dieti/dietiestatesbackend/service/AddressService.java`
6.  `src/main/java/com/dieti/dietiestatesbackend/service/PropertyService.java`
7.  `src/main/java/com/dieti/dietiestatesbackend/controller/VisitController.java`
8.  `src/main/java/com/dieti/dietiestatesbackend/controller/AddressController.java`
9.  `src/main/java/com/dieti/dietiestatesbackend/controller/PropertiesController.java`
10. `src/main/java/com/dieti/dietiestatesbackend/controller/OfferController.java`

## 4. Esempio di Utilizzo Corretto di SLF4J

Ecco come dovrebbe apparire una classe dopo la modifica. Prendiamo come esempio `AuthController`.

**Prima (con `java.util.logging.Logger`):**
```java
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthController {
    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    public void someMethod() {
        logger.log(Level.INFO, "Questo è un messaggio di log.");
    }
}
```

**Dopo (con `org.slf4j.Logger`):**
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public void someMethod() {
        // Esempi di logging con SLF4J
        logger.info("Questo è un messaggio di log informativo.");
        logger.error("Questo è un messaggio di errore con parametri: {}", "valoreParametro");
    }
}
```
**Nota:** L'uso di `{}` per i parametri è più performante rispetto alla concatenazione di stringhe.

## 5. Dipendenze Maven (`pom.xml`)

Non sono necessarie modifiche al file `pom.xml`. Il `spring-boot-starter-web` include già la dipendenza `spring-boot-starter-logging`, che a sua volta configura SLF4J con Logback come implementazione predefinita.

---

Questo piano è pronto per essere eseguito.