# Progettazione: Integrazione di SecurityFilterChain e JWT

Questo documento descrive l'architettura per l'integrazione di una `SecurityFilterChain` basata su JWT in un'applicazione Spring Boot, garantendo la coesistenza con il meccanismo di autenticazione Google esistente.

## 1. Analisi dello Stato Attuale

L'analisi del codice ha rivelato:
- **Doppio Sistema di Autenticazione**: Un endpoint `/login` per credenziali standard e un endpoint `/authwithgoogle` per la validazione di Google ID token.
- **Sicurezza Manuale**: La protezione degli endpoint è implementata manualmente all'interno dei metodi dei controller, validando il `Bearer` token. Questo approccio è insicuro e inefficiente.
- **Assenza di OAuth2 Standard**: Il flusso di login con Google non utilizza lo starter `spring-boot-starter-oauth2-client`, ma un'implementazione custom basata su `google-api-client`.
- **Generazione Token Base**: `AccessTokenProvider` genera token JWT contenenti solo lo `username`, senza informazioni sui ruoli. Da correggere!

## 2. Obiettivi della Progettazione

- **Centralizzare la Sicurezza**: Introdurre una `SecurityFilterChain` per gestire tutte le richieste in modo uniforme.
- **Abilitare l'Autorizzazione Basata su Ruoli**: Integrare i ruoli dell'utente nel token JWT per permettere un controllo degli accessi granulare (es. `@PreAuthorize`).
- **Mantenere la Coesistenza**: Assicurare che il flusso di login (standard e Google) e di refresh del token non vengano interrotti dalla nuova catena di sicurezza.
- **Migliorare la Manutenibilità**: Eliminare la logica di validazione del token duplicata nei controller.

## 3. Progettazione della Soluzione

### 3.1. Creazione della Classe di Configurazione (`SecurityConfig.java`)

Verrà creata una nuova classe `SecurityConfig` nel package `com.dieti.dietiestatesbackend.config`. Questa classe sarà annotata con `@Configuration`, `@EnableWebSecurity` e `@EnableMethodSecurity` per abilitare la configurazione personalizzata di Spring Security e la protezione a livello di metodo.

```java
// in src/main/java/com/dieti/dietiestatesbackend/config/SecurityConfig.java

package com.dieti.dietiestatesbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    // Definizione dei bean necessari (es. PasswordEncoder, JwtAuthenticationFilter)
}
```

### 3.2. Implementazione del `JwtAuthenticationFilter`

Questo filtro intercetterà ogni richiesta, estrarrà il token JWT, lo validerà e imposterà il contesto di sicurezza.

**Logica del Filtro (`JwtAuthenticationFilter.java`):**
1.  **Ereditarietà**: Estenderà `OncePerRequestFilter` per garantire l'esecuzione una sola volta per richiesta.
2.  **Estrazione Token**: Leggerà l'header `Authorization` e isolerà il token (rimuovendo il prefisso "Bearer ").
3.  **Validazione**: Se il token è presente, utilizzerà un `JwtTokenProvider` (versione migliorata di `AccessTokenProvider`) per validarne la firma e la scadenza.
4.  **Impostazione Contesto**: Se il token è valido, estrarrà lo `username` e i `roles` dai claims. Creerà un `UsernamePasswordAuthenticationToken` e lo imposterà nel `SecurityContextHolder`. Questo renderà l'utente autenticato per la durata della richiesta.
5.  **Gestione Richieste Pubbliche**: Se la richiesta non contiene un token (es. richieste a `/login`, `/signup`, `/authwithgoogle`), il filtro semplicemente passerà il controllo al filtro successivo nella catena (`chain.doFilter(request, response)`), senza impostare alcun contesto di sicurezza. Questo è il punto chiave per la coesistenza.

```java
// in src/main/java/com/dieti/dietiestatesbackend/security/JwtAuthenticationFilter.java

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // @Autowired
    // private JwtTokenProvider tokenProvider;
    // @Autowired
    // private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = getJwtFromRequest(request);

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            String username = tokenProvider.getUsernameFromJWT(jwt);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // Le authorities vengono create basandosi su TUTTI i ruoli presenti nel token,
            // non più su un singolo "ruolo attivo".
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

### 3.3. Definizione della `SecurityFilterChain`

All'interno di `SecurityConfig`, verrà definito il bean `SecurityFilterChain` che orchestra la sicurezza.

```java
// in src/main/java/com/dieti/dietiestatesbackend/config/SecurityConfig.java

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // 1. Disabilita CSRF, non necessario per API stateless con JWT
        .csrf(csrf -> csrf.disable())
        
        // 2. Configura la gestione della sessione come STATELESS
        // Spring Security non creerà né utilizzerà sessioni HttpSession
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        
        // 3. Configura le regole di autorizzazione per gli endpoint
        .authorizeHttpRequests(authorize -> authorize
            // Endpoints pubblici che non richiedono autenticazione
            .requestMatchers("/login", "/signup", "/authwithgoogle", "/refresh").permitAll()
            // Qualsiasi altra richiesta deve essere autenticata
            .anyRequest().authenticated()
        );
        
    // 4. Aggiunge il nostro filtro JWT prima del filtro standard di Spring
    // Questo assicura che il nostro meccanismo di autenticazione venga eseguito per primo
    http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
}

@Bean
public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter();
}
```

### 3.4. Modifiche a `AccessTokenProvider`

La classe `AccessTokenProvider` verrà modificata per includere **esclusivamente** un array di ruoli (`roles`) come claim nel token JWT. La responsabilità di gestire il "ruolo attivo" viene delegata al frontend.

**Nuova Logica di Generazione:**

```java
// Modifiche concettuali per AccessTokenProvider.java

// Il metodo di generazione accetterà solo lo username e la lista di tutti i ruoli posseduti.
public String generateAccessToken(String username, List<String> roles) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_DURATION_MS);

    SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    return Jwts.builder()
        .setSubject(username)
        // Aggiunta del claim "roles" contenente tutti i ruoli dell'utente.
        // Il frontend leggerà questo claim per gestire la UI.
        .claim("roles", roles)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(key)
        .compact();
}
```

### 3.5. Aggiornamento di `AuthController`

L'endpoint `/login` (e `/authwithgoogle`) verrà semplificato. Non accetterà più il parametro `loginAsRole`, poiché la gestione del ruolo attivo è demandata al frontend.

```java
// Modifiche concettuali per AuthController.java

@PostMapping("/login")
public ResponseEntity<Object> login(@RequestBody Map<String, String> body) {
    String email = body.get("email");
    String password = body.get("password");

    if (userService.doesUserExist(email, password)) {
        User user = userService.getUserByEmail(email); // Recupera l'utente
        
        // Genera il token includendo tutti i ruoli dell'utente.
        String accessToken = AccessTokenProvider.generateAccessToken(
            user.getUsername(),
            user.getRoles() // Lista completa dei ruoli
        );
        String refreshToken = RefreshTokenProvider.generateRefreshToken(user.getUsername());

        // La risposta non contiene più un ruolo attivo.
        // Il frontend può opzionalmente ricevere i ruoli disponibili o estrarli dal token.
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    } else {
        return new ResponseEntity<>("Credenziali non valide", HttpStatus.UNAUTHORIZED);
    }
}
```

### 3.6. Refactoring dei Controller

Tutta la logica di validazione manuale del token, come quella presente in `/agent/info/{id}`, dovrà essere rimossa.

**Prima:**
```java
@GetMapping("/agent/info/{id}")
public ResponseEntity<Object> getAgentInfo(
        @PathVariable("id") Long id,
        @RequestHeader(value = "Bearer", required = true) String accessToken) {
    if (accessToken == null || !AccessTokenProvider.validateToken(accessToken)) {
        return new ResponseEntity<>("Token non valido o scaduto", HttpStatusCode.valueOf(498));
    }
    // ...
}
```

**Dopo:**
```java
@GetMapping("/agent/info/{id}")
@PreAuthorize("hasRole('AGENT')") // o hasAuthority('ROLE_AGENT')
public ResponseEntity<Object> getAgentInfo(@PathVariable("id") Long id) {
    // La validazione è già stata fatta dal JwtAuthenticationFilter.
    // L'autorizzazione è gestita da @PreAuthorize.
    // ...
}
```

## 4. Flusso di Autenticazione e Autorizzazione

1.  **Frontend**: L'utente si logga specificando le proprie credenziali (es. email e password). Non viene inviato alcun ruolo.
2.  **Backend (`/login`)**: Verifica le credenziali e genera un JWT contenente un unico claim `roles` (un array con tutti i ruoli posseduti dall'utente).
3.  **Frontend**: Memorizza il JWT. Legge il claim `roles` per conoscere i ruoli disponibili e gestisce internamente la logica per selezionare un "ruolo attivo" (es. per visualizzare la dashboard corretta). Il token viene inviato nell'header `Authorization: Bearer <token>` per tutte le richieste successive.
4.  **Backend (`JwtAuthenticationFilter`)**: Per ogni richiesta protetta, il filtro valida il token.
5.  **Backend (`JwtAuthenticationFilter`)**: Se valido, estrae lo `username` e la lista completa dei `roles`. Imposta il `SecurityContext` con l'utente e **tutte le autorità** corrispondenti.
6.  **Backend (Controller)**: I metodi protetti con `@PreAuthorize("hasRole('AGENT')")` o `@PreAuthorize("hasAnyRole('AGENT', 'MANAGER')")` verificano se l'utente possiede almeno uno dei ruoli richiesti tra quelli presenti nel suo token.

## 5. Sfide e Soluzioni

- **Gestione `UserDetailsService`**: Sarà necessario implementare un `UserDetailsService` che carichi i dati dell'utente (incluso i ruoli) a partire dallo `username` per integrarsi con Spring Security.
- **Dipendenze Mancanti**: Sarà necessario aggiungere la dipendenza `spring-boot-starter-security` al `pom.xml` per avere tutte le funzionalità necessarie.
- **Transizione**: La rimozione della logica di validazione manuale deve essere fatta con attenzione su tutti gli endpoint per non lasciare buchi di sicurezza.