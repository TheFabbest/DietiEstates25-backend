package com.dieti.dietiestatesbackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Optional;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;


@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserManagementService service;

    @Test
    void createUser_success_savesLowercasedEmailAndEncodedPassword() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("username")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = service.createUser("Test@Example.com", "password", "username", "Nome", "Cognome");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertEquals("test@example.com", saved.getEmail());
        assertEquals("encodedPassword", saved.getPassword());
        assertEquals("username", saved.getUsername());
        assertEquals("Nome", saved.getFirstName());
        assertEquals("Cognome", saved.getLastName());
        assertEquals(saved, created);
    }

    @Test
    void createUser_whenEmailOrUsernameAlreadyExists_throws() {
        when(userRepository.existsByEmail("exists@example.com")).thenReturn(true);
        assertThrows(IllegalStateException.class,
                () -> service.createUser("Exists@Example.com", "p", "u", "n", "s"));

        reset(userRepository);
        when(userRepository.existsByEmail("ok@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("taken")).thenReturn(true);
        assertThrows(IllegalStateException.class,
                () -> service.createUser("ok@example.com", "p", "taken", "n", "s"));
    }

    @Test
    void changePassword_existingUser_updatesPassword() {
        User user = new User();
        user.setEmail("a@b.com");
        user.setPassword("old");
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpass")).thenReturn("encodedNew");

        service.changePassword("a@b.com", "newpass");

        verify(userRepository).save(user);
        assertEquals("encodedNew", user.getPassword());
    }

    @Test
    void changePassword_nonExistingUser_noSaveCalled() {
        when(userRepository.findByEmail("missing@x.com")).thenReturn(Optional.empty());

        service.changePassword("missing@x.com", "whatever");

        verify(userRepository, never()).save(any());
    }

    @Test
    void createGoogleUser_success_savesUserWithoutPasswordAndLowercasesEmail() {
        when(userRepository.existsByEmail("google@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("guser")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.createGoogleUser("Google@Example.com", "guser", "GoogleName", "GoogleSurname");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertEquals("google@example.com", saved.getEmail());
        assertEquals("guser", saved.getUsername());
        assertEquals("GoogleName", saved.getFirstName());
        assertEquals("GoogleSurname", saved.getLastName());
        assertNotNull(saved.getPassword());
    }

    @Test
    void createGoogleUser_missingEmail_throws() {
        assertThrows(IllegalStateException.class,
                () -> service.createGoogleUser(null, "u", "n", "s"));
        assertThrows(IllegalStateException.class,
                () -> service.createGoogleUser("   ", "u", "n", "s"));
    }

    @Test
    void createGoogleUser_missingUsername_throws() {
        assertThrows(IllegalStateException.class,
                () -> service.createGoogleUser("e@example.com", null, "n", "s"));
        assertThrows(IllegalStateException.class,
                () -> service.createGoogleUser("e@example.com", "   ", "n", "s"));
    }

    @Test
    void createGoogleUser_whenEmailOrUsernameInUse_throws() {
        when(userRepository.existsByEmail("inuse@example.com")).thenReturn(true);
        assertThrows(IllegalStateException.class,
                () -> service.createGoogleUser("InUse@Example.com", "u", "n", "s"));

        reset(userRepository);
        when(userRepository.existsByEmail("ok@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("taken")).thenReturn(true);
        assertThrows(IllegalStateException.class,
                () -> service.createGoogleUser("ok@example.com", "taken", "n", "s"));
    }
}