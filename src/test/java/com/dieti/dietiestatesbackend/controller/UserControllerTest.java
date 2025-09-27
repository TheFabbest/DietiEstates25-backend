package com.dieti.dietiestatesbackend.controller;

import com.dieti.dietiestatesbackend.dto.response.UserResponse;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.security.SecurityUtil;
import com.dieti.dietiestatesbackend.service.UserService;

import org.springframework.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private SecurityUtil securityUtil;

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
}