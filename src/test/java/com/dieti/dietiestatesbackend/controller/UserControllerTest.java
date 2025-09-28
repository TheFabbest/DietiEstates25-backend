package com.dieti.dietiestatesbackend.controller;

import com.dieti.dietiestatesbackend.dto.request.ChangePasswordRequest;
import com.dieti.dietiestatesbackend.dto.request.SignupRequest;
import com.dieti.dietiestatesbackend.dto.response.UserResponse;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.security.AuthenticatedUser;
import com.dieti.dietiestatesbackend.security.SecurityUtil;
import com.dieti.dietiestatesbackend.service.UserService;

import org.springframework.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserController userController;

    @Test
    void getAgentInfo_shouldReturnAgentInfo_whenAgentExistsAndUserIsAuthorized() throws Exception {
        // Given
        Long agentId = 2L;
        User agentUser = new User();
        agentUser.setId(agentId);
        agentUser.setEmail("agent@example.com");
        agentUser.setFirstName("Agent");
        agentUser.setLastName("Test");
        agentUser.setAgent(true);

        when(userService.getUser(anyLong())).thenReturn(agentUser);
        
        // When
        ResponseEntity <Object> responseEntity = userController.getAgentInfo(agentId);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(agentUser.getEmail(), ((UserResponse) responseEntity.getBody()).getEmail());
    }

    @Test
    void getAgentInfo_shouldBlock_whenUserNotAgent() throws Exception {
        // Given
        Long agentId = 2L;
        User nonAgentUser = new User();
        nonAgentUser.setId(agentId);
        nonAgentUser.setEmail("non-agent@example.com");
        nonAgentUser.setFirstName("NonAgent");
        nonAgentUser.setLastName("Test");
        nonAgentUser.setAgent(false);

        when(userService.getUser(agentId)).thenReturn(nonAgentUser);

        // When
        ResponseEntity<Object> responseEntity = userController.getAgentInfo(agentId);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    void getAgentInfo_shouldBlock_whenUserNotFound() throws Exception {
        // Given
        Long agentId = 2L;

        when(userService.getUser(agentId)).thenReturn(null);

        // When
        ResponseEntity<Object> responseEntity = userController.getAgentInfo(agentId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void createAgent_shouldCreateAgent_whenUserIsManager() {
        User creator = new User();
        creator.setId(1L);
        creator.setManager(true);

        when(userService.doesUserExist(anyString())).thenReturn(false);
        when(userService.createAgent(any(SignupRequest.class), eq(creator))).thenReturn(new User());
        when(userService.getUser(creator.getId())).thenReturn(creator);

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("to_be_created@example.com");
        signupRequest.setUsername("to_be_created");
        ResponseEntity<Object> response = userController.createAgent(signupRequest, new AuthenticatedUser(creator.getId(), "manager", true, null));

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userService).createAgent(signupRequest, creator);
    }

    @Test
    void createAgent_shouldAddAgentRole_whenUserExistsAndIsManager() {
        User creator = new User();
        creator.setId(1L);
        creator.setManager(true);   

        when(userService.doesUserExist(anyString())).thenReturn(true);
        when(userService.getUser(creator.getId())).thenReturn(creator);

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("future_agent@example.com");
        signupRequest.setUsername("future_agent");
        ResponseEntity<Object> response = userController.createAgent(signupRequest, new AuthenticatedUser(creator.getId(), "manager", true, null));
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).addAgentRole(eq("future_agent"), eq(creator));
    }

    @Test
    void createAgent_shouldCreateAgent_whenUserIsManager_onError() {
        User creator = new User();
        creator.setId(1L);
        creator.setManager(true);

        when(userService.doesUserExist(anyString())).thenReturn(false);
        when(userService.getUser(creator.getId())).thenReturn(creator);
        doThrow(new IllegalStateException()).when(userService).createAgent(any(SignupRequest.class), any(User.class));

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("to_be_created@example.com");
        signupRequest.setUsername("to_be_created");
        ResponseEntity<Object> response = userController.createAgent(signupRequest, new AuthenticatedUser(creator.getId(), "manager", true, null));

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(userService).createAgent(signupRequest, creator);
    }

    @Test
    void createAgent_shouldAddAgentRole_whenUserExistsAndIsManager_onError() {
        User creator = new User();
        creator.setId(1L);
        creator.setManager(true);   

        when(userService.doesUserExist(anyString())).thenReturn(true);
        when(userService.getUser(creator.getId())).thenReturn(creator);
        doThrow(new IllegalStateException()).when(userService).addAgentRole(anyString(), any(User.class));

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("future_agent@example.com");
        signupRequest.setUsername("future_agent");
        ResponseEntity<Object> response = userController.createAgent(signupRequest, new AuthenticatedUser(creator.getId(), "manager", true, null));
        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(userService).addAgentRole(eq("future_agent"), eq(creator));
    }

    // same for manager
    @Test
    void createManager_shouldCreateAgent_whenUserIsManager() {
        User creator = new User();
        creator.setId(1L);
        creator.setManager(true);

        when(userService.doesUserExist(anyString())).thenReturn(false);
        when(userService.createManager(any(SignupRequest.class), eq(creator))).thenReturn(new User());
        when(userService.getUser(creator.getId())).thenReturn(creator);

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("to_be_created@example.com");
        signupRequest.setUsername("to_be_created");
        ResponseEntity<Object> response = userController.createManager(signupRequest, new AuthenticatedUser(creator.getId(), "manager", true, null));

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userService).createManager(signupRequest, creator);
    }

    @Test
    void createManager_shouldAddAgentRole_whenUserExistsAndIsManager() {
        User creator = new User();
        creator.setId(1L);
        creator.setManager(true);   

        when(userService.doesUserExist(anyString())).thenReturn(true);
        when(userService.getUser(creator.getId())).thenReturn(creator);

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("future_agent@example.com");
        signupRequest.setUsername("future_agent");
        ResponseEntity<Object> response = userController.createManager(signupRequest, new AuthenticatedUser(creator.getId(), "manager", true, null));
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).addManagerRole(eq("future_agent"), eq(creator));
    }

    @Test
    void createManager_shouldCreateAgent_whenUserIsManager_onError() {
        User creator = new User();
        creator.setId(1L);
        creator.setManager(true);

        when(userService.doesUserExist(anyString())).thenReturn(false);
        when(userService.getUser(creator.getId())).thenReturn(creator);
        doThrow(new IllegalStateException()).when(userService).createManager(any(SignupRequest.class), any(User.class));

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("to_be_created@example.com");
        signupRequest.setUsername("to_be_created");
        ResponseEntity<Object> response = userController.createManager(signupRequest, new AuthenticatedUser(creator.getId(), "manager", true, null));

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(userService).createManager(signupRequest, creator);
    }

    @Test
    void createManager_shouldAddAgentRole_whenUserExistsAndIsManager_onError() {
        User creator = new User();
        creator.setId(1L);
        creator.setManager(true);   

        when(userService.doesUserExist(anyString())).thenReturn(true);
        when(userService.getUser(creator.getId())).thenReturn(creator);
        doThrow(new IllegalStateException()).when(userService).addManagerRole(anyString(), any(User.class));

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("future_agent@example.com");
        signupRequest.setUsername("future_agent");
        ResponseEntity<Object> response = userController.createManager(signupRequest, new AuthenticatedUser(creator.getId(), "manager", true, null));
        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(userService).addManagerRole(eq("future_agent"), eq(creator));
    }

    @Test
    void changeUserPassword_shouldChangePassword() {
        // Given
        String email = "user@example.com";
        String newPassword = "newPassword123@";
        String oldPassword = "oldPassword123@";
        User user = new User();
        user.setEmail(email);
        user.setPassword(oldPassword);
        user.setUsername("user");
        user.setManager(true);
        ChangePasswordRequest passRequest = new ChangePasswordRequest();
        passRequest.setEmail(email);
        passRequest.setNewPassword(newPassword);
        passRequest.setOldPassword(oldPassword);
        // When
        when(userService.getUsernameFromEmail(email)).thenReturn("user");
        when(userService.getUserByUsername("user")).thenReturn(user);

        ResponseEntity<Object> response = userController.changeUserPassword(passRequest);
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).changePassword(email, newPassword);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void changeUserPassword_shouldBlock_whenUserNotFound() {
        // Given
        String email = "user@example.com";
        String newPassword = "newPassword123@";
        String oldPassword = "oldPassword123@";
        ChangePasswordRequest passRequest = new ChangePasswordRequest();
        passRequest.setEmail(email);
        passRequest.setNewPassword(newPassword);
        passRequest.setOldPassword(oldPassword);
        // When
        when(userService.getUsernameFromEmail(email)).thenReturn(null);

        ResponseEntity<Object> response = userController.changeUserPassword(passRequest);
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService).getUsernameFromEmail(email);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void changeUserPassword_shouldBlock_whenUserNotManager() {
        // Given
        String email = "user@example.com";
        String newPassword = "newPassword123@";
        String oldPassword = "oldPassword123@";
        ChangePasswordRequest passRequest = new ChangePasswordRequest();
        passRequest.setEmail(email);
        passRequest.setNewPassword(newPassword);
        passRequest.setOldPassword(oldPassword);
        // When
        when(userService.getUsernameFromEmail(email)).thenReturn("user");
        when(userService.getUserByUsername("user")).thenReturn(null);

        ResponseEntity<Object> response = userController.changeUserPassword(passRequest);
        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(userService).getUsernameFromEmail(email);
        verifyNoMoreInteractions(userService);
    }
}