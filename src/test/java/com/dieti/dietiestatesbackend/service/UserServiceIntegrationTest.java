package com.dieti.dietiestatesbackend.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.dieti.dietiestatesbackend.entities.User;

@SpringBootTest
@ActiveProfiles("test")
@Transactional // This ensures each test is rolled back
public class UserServiceIntegrationTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserQueryService userQueryService;

    @Test 
    void testUserCreationAndRetrieval() {
        // Given
        String email = "integration-test@example.com";
        String password = "TestPassword123!";
        String username = "integrationuser";
        String name = "Integration";
        String surname = "Test";
        
        // Verify user doesn't exist initially
        assertFalse(userQueryService.doesUserExist(email), 
            "User should not exist before creation");
        
        // When - Create the user
        userService.createUser(email, password, username, name, surname);
        User createdUser = userQueryService.getUserByUsername(username);
        
        // Then - Verify user was created and can be found
        assertNotNull(createdUser, "Created user should not be null");
        assertEquals(email, createdUser.getEmail());
        assertEquals(username, createdUser.getUsername());
        assertEquals(name, createdUser.getFirstName());
        assertEquals(surname, createdUser.getLastName());

        // Verify user now exists in the system
        assertTrue(userQueryService.doesUserExist(email), 
            "User should exist after creation");
    }
    
    @Test
    void testUserDoesNotExistForNonExistentEmail() {
        // Given
        String nonExistentEmail = "does-not-exist@example.com";
        
        // When & Then
        assertFalse(userQueryService.doesUserExist(nonExistentEmail),
            "Non-existent user should not be found");
    }
    
    @Test
    void testCreateDuplicateUserThrowsException() {
        // Given
        String email = "duplicate@example.com";
        String password = "TestPassword123!";
        String username = "duplicateuser";
        String name = "Duplicate";
        String surname = "Test";
        
        // Create first user
        userService.createUser(email, password, username, name, surname);
        
        // When & Then - Try to create duplicate
        assertThrows(RuntimeException.class, // Replace with your actual exception type
            () -> userService.createUser(email, password, "different_username", name, surname),
            "Should throw exception when creating user with duplicate email");
    }
}