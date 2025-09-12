package com.dieti.dietiestatesbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.repositories.UserRepository;

@Service
@Transactional
public class UserService {
    private final UserQueryService userQueryService;
    private final UserManagementService userManagementService;

    @Autowired
    public UserService(UserQueryService userQueryService, UserManagementService userManagementService) {
        this.userQueryService = userQueryService;
        this.userManagementService = userManagementService;
    }

    public boolean doesUserExist(String email, String password) {
        return userQueryService.doesUserExist(email, password);
    }

    public String getUsernameFromEmail(String email) {
        return userQueryService.getUsernameFromEmail(email);
    }

    public boolean doesUserExist(String email) {
        return userQueryService.doesUserExist(email);
    }

    // Metodo getErrorMessageUserCreation rimosso come da piano di refactoring
    // Le eccezioni verranno gestite con eccezioni più specifiche di Spring

    public void createUser(String email, String password, String username, String nome, String cognome) {
        userManagementService.createUser(email, password, username, nome, cognome);
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
        userManagementService.createGoogleUser(email, username, name, surname);
    }

    public User getUser(Long id) {
        return userQueryService.getUser(id);
    }

    /**
     * Recupera l'utente dato lo username.
     * Restituisce null se non trovato.
     */
    public User getUserByUsername(String username) {
        return userQueryService.getUserByUsername(username);
    }

    public void changePassword(String email, String newPassword) {
        userManagementService.changePassword(email, newPassword);
    }
}