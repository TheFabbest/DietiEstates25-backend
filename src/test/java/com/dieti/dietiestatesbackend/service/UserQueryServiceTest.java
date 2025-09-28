package com.dieti.dietiestatesbackend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.repositories.UserRepository;


@ExtendWith(MockitoExtension.class)
class UserQueryServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserQueryService service;

    @Test
    void doesUserExist_withPassword_userFoundAndPasswordMatches_returnsTrue() {
        String email = "test@example.com";
        String rawPassword = "secret";
        User user = mock(User.class);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(user.getPassword()).thenReturn("encoded");
        when(passwordEncoder.matches(rawPassword, "encoded")).thenReturn(true);

        boolean result = service.doesUserExist(email, rawPassword);

        assertTrue(result);
    }

    @Test
    void doesUserExist_withPassword_userFoundAndPasswordDoesNotMatch_returnsFalse() {
        String email = "test@example.com";
        String rawPassword = "bad";
        User user = mock(User.class);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(user.getPassword()).thenReturn("encoded");
        when(passwordEncoder.matches(rawPassword, "encoded")).thenReturn(false);

        boolean result = service.doesUserExist(email, rawPassword);

        assertFalse(result);
    }

    @Test
    void doesUserExist_withPassword_userNotFound_returnsFalse() {
        when(userRepository.findByEmail("noone@example.com")).thenReturn(Optional.empty());

        boolean result = service.doesUserExist("noone@example.com", "any");

        assertFalse(result);
    }

    @Test
    void getUsernameFromEmail_userFound_returnsUsername() {
        String email = "u@example.com";
        User user = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(user.getUsername()).thenReturn("theUser");

        String username = service.getUsernameFromEmail(email);

        assertEquals("theUser", username);
    }

    @Test
    void getUsernameFromEmail_userNotFound_returnsEmptyString() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        String username = service.getUsernameFromEmail("missing@example.com");

        assertEquals("", username);
    }

    @Test
    void doesUserExist_byEmail_nullOrEmpty_returnsFalseAndDoesNotCallRepository() {
        assertFalse(service.doesUserExist((String) null));
        assertFalse(service.doesUserExist(""));

        verify(userRepository, never()).existsByEmail(anyString());
    }

    @Test
    void doesUserExist_byEmail_convertsToLowercaseAndDelegatesToRepository() {
        String input = "User@Example.COM";
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        boolean exists = service.doesUserExist(input);

        assertTrue(exists);
        verify(userRepository).existsByEmail("user@example.com");
    }

    @Test
    void getUser_existingId_returnsUser() {
        Long id = 42L;
        User user = mock(User.class);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User result = service.getUser(id);

        assertSame(user, result);
    }

    @Test
    void getUser_nonExistingId_throwsIllegalArgumentException() {
        Long id = 99L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.getUser(id));
        assertTrue(ex.getMessage().contains(String.valueOf(id)));
    }

    @Test
    void getUserByUsername_existingUser_returnsUser() {
        String username = "bob";
        User user = mock(User.class);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        User result = service.getUserByUsername(username);

        assertSame(user, result);
    }

    @Test
    void getUserByUsername_nonExistingUser_throwsIllegalArgumentException() {
        String username = "nobody";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.getUserByUsername(username));
        assertTrue(ex.getMessage().contains(username));
    }
}