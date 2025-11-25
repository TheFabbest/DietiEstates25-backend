package com.dieti.dietiestatesbackend.service;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.repositories.UserRepository;

@Service
@Transactional
public class UserManagementService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final Random random = new Random();

    @Autowired
    public UserManagementService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(String email, String password, String username, String nome, String cognome) {
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
        return user;
    }

    public void changePassword(String email, String newPassword) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        });
    }

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
        user.setUsername(username);
        user.setFirstName(name);
        user.setLastName(surname);
        user.setAgent(false);
        user.setManager(false);
        
        byte[] array = new byte[32]; // length is bounded by 7
        random.nextBytes(array);
        String generatedString = new String(array, StandardCharsets.UTF_8);
        user.setPassword(generatedString);
        
        userRepository.save(user);
    }
}