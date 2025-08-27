package com.dieti.dietiestatesbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.repositories.UserRepository;

@Service
@Transactional
public class UserService {
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean doesUserExist(String email, String password) {
        return userRepository.findByEmail(email)
            .map(user -> passwordEncoder.matches(password, user.getPassword()))
            .orElse(false);
    }

    public String getUsernameFromEmail(String email) {
        return userRepository.findByEmail(email)
            .map(User::getUsername)
            .orElse("");
    }

    public boolean doesUserExist(String email) {
        return userRepository.existsByEmail(email.toLowerCase());
    }

    // Metodo getErrorMessageUserCreation rimosso come da piano di refactoring
    // Le eccezioni verranno gestite con eccezioni più specifiche di Spring

    public void createUser(String email, String password, String username, String nome, String cognome) {
        if (userRepository.existsByEmail(email.toLowerCase()) || userRepository.existsByUsername(username)) {
            throw new IllegalStateException("Credenziali già in uso");
        }
        
        User user = new User();
        user.setEmail(email.toLowerCase());
        user.setPassword(passwordEncoder.encode(password));
        user.setUsername(username);
        user.setFirstName(nome);
        user.setLastName(cognome);
        user.setAgent(false);
        user.setManager(false);
        
        userRepository.save(user);
    }

    /**
     * Crea un utente tramite autenticazione Google.
     * Per gli utenti Google, la password è vuota e non viene codificata.
     *
     * @param email L'email dell'utente
     * @param username Lo username dell'utente
     * @param name Il nome dell'utente
     * @param surname Il cognome dell'utente
     * @throws IllegalStateException se le credenziali sono già in uso o se i dati obbligatori sono mancanti
     */
    public void createGoogleUser(String email, String username, String name, String surname) {
        // Validazione dei dati obbligatori
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalStateException("L'email è obbligatoria per la creazione dell'utente Google");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalStateException("Lo username è obbligatorio per la creazione dell'utente Google");
        }
        
        // Verifica se le credenziali sono già in uso
        if (userRepository.existsByEmail(email.toLowerCase())) {
            throw new IllegalStateException("Email già in uso");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalStateException("Username già in uso");
        }
        
        User user = new User();
        user.setEmail(email.toLowerCase());
        // Per gli utenti Google, non impostiamo la password (rimane null)
        user.setUsername(username);
        user.setFirstName(name);
        user.setLastName(surname);
        user.setAgent(false);
        user.setManager(false);
        
        userRepository.save(user);
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Utente non trovato con id: " + id));
    }

    /**
     * Recupera l'utente dato lo username.
     * Restituisce null se non trovato.
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Utente non trovato con username: " + username));
    }
}