package com.dieti.dietiestatesbackend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dieti.dietiestatesbackend.dto.request.SignupRequest;
import com.dieti.dietiestatesbackend.entities.Agency;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    // Mock the dependencies
    @Mock 
    private UserQueryService userQueryService;
    
    @Mock 
    private UserManagementService userManagementService;
    
    @Mock 
    private UserRepository userRepository;

    // Inject mocks into the service we want to test
    @InjectMocks
    private UserService userService;

    @Test
    void testDoesUserExistWithNullEmail() {
        // When
        boolean result = userService.doesUserExist((String) null);
        
        // Then
        assertFalse(result);

        // Verify userQueryService is called (since UserService delegates to it)
        verify(userQueryService).doesUserExist((String) null);
    }

    @Test
    void testDoesUserExistWithEmptyEmail() {
        // Given
        when(userQueryService.doesUserExist("")).thenReturn(false);
        
        // When  
        boolean result = userService.doesUserExist("");
        
        // Then
        assertFalse(result);

        // Verify userQueryService is called
        verify(userQueryService).doesUserExist("");
    }

    @Test
    void testUserCreationWithWeakPassword() {
        // Given
        String email = "test@example.com";
        String password = "123Password";
        
        when(userManagementService.createUser(email, password, "testuser", "test", "user"))
            .thenReturn(null);

        // When
        userService.createUser(email, password, "testuser", "test", "user");

        // Then
        verify(userManagementService).createUser(email, password, "testuser", "test", "user");
    }

    @Test
    void managerCreationWithValidData() {
        // Given
        String email = "newmanager@example.com";
        String password = "StrongPassword@123";
        String username = "newmanager";
        String name = "Manager";
        String surname = "New";

        String creatorEmail = "creator@example.com";
        String creatorUsername = "creatorManager";
        String creatorPassword = "CreatorPass@123";
        String creatorName = "Creator";
        String creatorSurname = "Manager";

        SignupRequest req = new SignupRequest();
        req.setEmail(email);
        req.setPassword(password);
        req.setUsername(username);
        req.setName(name);
        req.setSurname(surname);

        // Create expected user that userManagementService will return
        User expectedCreated = new User();
        expectedCreated.setEmail(email);
        expectedCreated.setUsername(username);
        expectedCreated.setFirstName(name);
        expectedCreated.setLastName(surname);
        expectedCreated.setManager(false); // Initially false
        expectedCreated.setAgent(false);
        expectedCreated.setPassword(password);

        // Create creator user
        User creator = new User();
        Agency agency = new Agency();
        agency.setName("Best Agency");
        creator.setManager(true);
        creator.setAgency(agency);
        creator.setUsername(creatorUsername);
        creator.setEmail(creatorEmail);
        creator.setPassword(creatorPassword);
        creator.setFirstName(creatorName);
        creator.setLastName(creatorSurname);

        // Mock userManagementService.createUser to return the base user
        when(userManagementService.createUser(
            email, password, username, name, surname
        )).thenReturn(expectedCreated);

        // When
        User createdUser = userService.createManager(req, creator);

        // Then
        assertNotNull(createdUser);
        assertEquals(email, createdUser.getEmail());
        assertEquals(username, createdUser.getUsername());
        assertEquals(name, createdUser.getFirstName());
        assertEquals(surname, createdUser.getLastName());
        assertTrue(createdUser.isManager()); // Should be true after createManager sets it
        assertFalse(createdUser.isAgent());
        assertEquals(creator.getAgency(), createdUser.getAgency());
        assertEquals(creator.getAgency().getName(), createdUser.getAgency().getName());

        // Verify userManagementService.createUser was called with correct parameters
        verify(userManagementService).createUser(email, password, username, name, surname);
    }

    @Test
    void managerCreationWithNonManagerCreator() {
        // Given
        SignupRequest req = new SignupRequest();
        req.setEmail("test@example.com");
        req.setPassword("password");
        req.setUsername("testuser");
        req.setName("Test");
        req.setSurname("User");

        User creator = new User();
        creator.setManager(false); // Not a manager

        // When & Then
        assertThrows(
            IllegalStateException.class,
            () -> userService.createManager(req, creator)
        );
        
        
        verify(userManagementService, never()).createUser(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void agentCreationWithValidData() {
        // Given
        String email = "newagent@example.com";
        String password = "StrongPassword@123";
        String username = "newagent";
        String name = "Agent";
        String surname = "New";

        SignupRequest req = new SignupRequest();
        req.setEmail(email);
        req.setPassword(password);
        req.setUsername(username);
        req.setName(name);
        req.setSurname(surname);

        User expectedCreated = new User();
        expectedCreated.setEmail(email);
        expectedCreated.setUsername(username);
        expectedCreated.setFirstName(name);
        expectedCreated.setLastName(surname);
        expectedCreated.setManager(false);
        expectedCreated.setAgent(false); // Initially false
        expectedCreated.setPassword(password);

        User creator = new User();
        Agency agency = new Agency();
        agency.setName("Test Agency");
        creator.setManager(true);
        creator.setAgency(agency);

        // Mock userManagementService.createUser
        when(userManagementService.createUser(
            email, password, username, name, surname
        )).thenReturn(expectedCreated);

        // When
        userService.createAgent(req, creator);

        // Then
        assertTrue(expectedCreated.isAgent()); // Should be true after createAgent sets it
        assertFalse(expectedCreated.isManager());
        assertEquals(creator.getAgency(), expectedCreated.getAgency());
        
        // Verify userManagementService.createUser was called
        verify(userManagementService).createUser(email, password, username, name, surname);
    }
}