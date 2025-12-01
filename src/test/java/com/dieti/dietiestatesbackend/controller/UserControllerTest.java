package com.dieti.dietiestatesbackend.controller;

import com.dieti.dietiestatesbackend.dto.request.ChangePasswordRequest;
import com.dieti.dietiestatesbackend.dto.request.CreateUserRequest;
import com.dieti.dietiestatesbackend.dto.response.UserResponse;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.security.AuthenticatedUser;
import com.dieti.dietiestatesbackend.service.UserService;
import com.dieti.dietiestatesbackend.service.emails.EmailService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserController userController;

    // =================== GET AGENT INFO ========================

    @Test
    void getAgentInfo_shouldReturnAgentInfo_whenAgentExists() {
        Long agentId = 2L;
        User agentUser = new User();
        agentUser.setId(agentId);
        agentUser.setEmail("agent@example.com");
        agentUser.setFirstName("Agent");
        agentUser.setLastName("Test");
        agentUser.setAgent(true);

        when(userService.getUser(agentId)).thenReturn(agentUser);

        ResponseEntity<Object> response = userController.getAgentInfo(agentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("agent@example.com", ((UserResponse) response.getBody()).getEmail());
    }

    @Test
    void getAgentInfo_shouldReturnForbidden_whenUserNotAgent() {
        Long agentId = 2L;
        User nonAgentUser = new User();
        nonAgentUser.setId(agentId);
        nonAgentUser.setAgent(false);

        when(userService.getUser(agentId)).thenReturn(nonAgentUser);

        ResponseEntity<Object> response = userController.getAgentInfo(agentId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("L'utente non è un agente", response.getBody());
    }

    @Test
    void getAgentInfo_shouldReturnNotFound_whenUserNotFound() {
        when(userService.getUser(anyLong())).thenReturn(null);

        ResponseEntity<Object> response = userController.getAgentInfo(2L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Utente non trovato", response.getBody());
    }

    // =================== CREATE AGENT ========================

    @Test
    void createAgent_shouldCreateAgent_whenUserIsManager() {
        User manager = new User();
        manager.setId(1L);
        manager.setManager(true);

        when(userService.getUser(1L)).thenReturn(manager);
        when(userService.doesUserExist(anyString())).thenReturn(false);

        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("to_be_created@example.com");
        req.setUsername("to_be_created");

        ResponseEntity<Object> response =
                userController.createAgent(req, new AuthenticatedUser(manager.getId(), "manager", true, null));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userService).createAgent(any(), eq(manager));
        verify(emailService).sendAgentAccountCreatedEmail(anyString(), anyString());
    }

    @Test
    void createAgent_shouldAddAgentRole_whenUserExists() {
        User manager = new User();
        manager.setId(1L);
        manager.setManager(true);

        when(userService.getUser(1L)).thenReturn(manager);
        when(userService.doesUserExist(anyString())).thenReturn(true);

        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("future_agent@example.com");
        req.setUsername("future_agent");

        ResponseEntity<Object> response =
                userController.createAgent(req, new AuthenticatedUser(manager.getId(), "manager", true, null));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).addAgentRole("future_agent", manager);
    }

    @Test
    void createAgent_shouldReturnError_whenAddRoleFails() {
        User manager = new User();
        manager.setId(1L);
        manager.setManager(true);

        when(userService.getUser(1L)).thenReturn(manager);
        when(userService.doesUserExist(anyString())).thenReturn(true);
        doThrow(new IllegalStateException()).when(userService).addAgentRole(anyString(), any());

        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("future_agent@example.com");
        req.setUsername("future_agent");

        ResponseEntity<Object> response =
                userController.createAgent(req, new AuthenticatedUser(manager.getId(), "manager", true, null));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // =================== CHANGE PASSWORD ========================

    @Test
    void changeUserPassword_shouldChangePassword() {
        String email = "user@example.com";
        String username = "user";

        User manager = new User();
        manager.setUsername(username);
        manager.setManager(true);

        when(userService.getUsernameFromEmail(email)).thenReturn(username);
        when(userService.getUserByUsername(username)).thenReturn(manager);

        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setEmail(email);
        req.setOldPassword("oldPass123@");
        req.setNewPassword("newPass123@");

        ResponseEntity<Object> response = userController.changeUserPassword(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService).changePassword(email, "newPass123@");
    }

    @Test
    void changeUserPassword_shouldReturnNotFound_whenUserMissing() {
        when(userService.getUsernameFromEmail(anyString())).thenReturn(null);

        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setEmail("notfound@example.com");

        ResponseEntity<Object> response = userController.changeUserPassword(req);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void changeUserPassword_shouldReturnForbidden_whenUserNotManager() {
        String email = "user@example.com";

        when(userService.getUsernameFromEmail(email)).thenReturn("user");
        when(userService.getUserByUsername("user")).thenReturn(new User()); // isManager = false

        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setEmail(email);

        ResponseEntity<Object> response = userController.changeUserPassword(req);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
