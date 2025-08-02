# Piano di Refactoring: Architettura Service-Repository

Questo documento descrive la nuova architettura per il backend di DietiEstates, basata sul pattern `Service-Repository-Controller`.

## 1. Struttura dei Package

La nuova struttura dei package sarà organizzata come segue per separare le responsabilità:

```
com.dieti.dietiestatesbackend
├── controller
│   ├── AuthController.java
│   └── ListingController.java
├── service
│   ├── impl
│   │   ├── UserServiceImpl.java
│   │   └── ListingServiceImpl.java
│   ├── UserService.java
│   └── ListingService.java
├── repository
│   ├── UserRepository.java
│   └── ListingRepository.java
├── entity
│   ├── Utente.java
│   └── Immobile.java
└── dto
    ├── request
    └── response
```

## 2. Interfacce dei Repository

Verranno create le seguenti interfacce Spring Data JPA per l'accesso ai dati.

### UserRepository

Gestirà le operazioni CRUD per l'entità `Utente`.

```java
package com.dieti.dietiestatesbackend.repository;

import com.dieti.dietiestatesbackend.entity.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Utente, Long> {
    Optional<Utente> findByEmail(String email);
    Optional<Utente> findByUsername(String username);
}
```

### ListingRepository

Gestirà le operazioni CRUD per l'entità `Immobile`.

```java
package com.dieti.dietiestatesbackend.repository;

import com.dieti.dietiestatesbackend.entity.Immobile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Immobile, Long> {
    List<Immobile> findByTitoloContainingIgnoreCase(String keyword);
}
```

## 3. Interfacce dei Servizi

Le interfacce dei servizi definiranno la logica di business.

### UserService

```java
package com.dieti.dietiestatesbackend.service;

import com.dieti.dietiestatesbackend.entity.Utente;
import java.util.Optional;

public interface UserService {
    Optional<Utente> findByEmail(String email);
    Optional<Utente> findByUsername(String username);
    Utente save(Utente utente);
    boolean isPasswordStrong(String password);
}
```

### ListingService

```java
package com.dieti.dietiestatesbackend.service;

import com.dieti.dietiestatesbackend.entity.Immobile;
import java.util.List;

public interface ListingService {
    List<Immobile> searchListings(String keyword);
    Optional<Immobile> getListingById(Long id);
}
```

## 4. Diagramma di Classe (Mermaid)

```mermaid
classDiagram
    class ListingController {
        +getListings(String, String)
        +getThumbnails(long)
    }
    class AuthController {
        +authenticate(LoginRequest)
        +register(RegisterRequest)
    }
    class UserService {
        <<Interface>>
        +findByEmail(String)
        +findByUsername(String)
        +save(Utente)
        +isPasswordStrong(String)
    }
    class UserServiceImpl {
        -UserRepository userRepository
        +findByEmail(String)
        +findByUsername(String)
        +save(Utente)
        +isPasswordStrong(String)
    }
    class ListingService {
        <<Interface>>
        +searchListings(String)
        +getListingById(Long)
    }
    class ListingServiceImpl {
        -ListingRepository listingRepository
        +searchListings(String)
        +getListingById(Long)
    }
    class UserRepository {
        <<Interface>>
        +findByEmail(String)
        +findByUsername(String)
    }
    class ListingRepository {
        <<Interface>>
        +findByTitoloContainingIgnoreCase(String)
    }
    class Utente {
        -String email
        -String password
        -String username
    }
    class Immobile {
        -String titolo
        -String descrizione
        -BigDecimal prezzo
    }

    ListingController ..> ListingService
    AuthController ..> UserService
    UserServiceImpl ..> UserRepository
    UserServiceImpl ..|> UserService
    ListingServiceImpl ..> ListingRepository
    ListingServiceImpl ..|> ListingService
    UserRepository ..> Utente
    ListingRepository ..> Immobile