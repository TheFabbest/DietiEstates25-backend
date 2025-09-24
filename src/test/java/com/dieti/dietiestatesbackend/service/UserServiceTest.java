package com.dieti.dietiestatesbackend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dieti.dietiestatesbackend.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock UserService userService;
    @Mock UserManagementService userManagementService;
    @Mock UserRepository userRepository;

    @InjectMocks
    private UserQueryService userQueryService;

    @Test
    void testDoesUserExistWithNullEmail() {
        // When
        boolean result = userQueryService.doesUserExist(null);
        
        // Then
        assertFalse(result);

        // Verify repository is never called with null
        verify(userRepository, never()).existsByEmail(any());
    }

    @Test
    void testDoesUserExistWithEmptyEmail() {
        // When  
        boolean result = userQueryService.doesUserExist("");
        
        // Then
        assertFalse(result);
        verify(userRepository, never()).existsByEmail("");
    }
}