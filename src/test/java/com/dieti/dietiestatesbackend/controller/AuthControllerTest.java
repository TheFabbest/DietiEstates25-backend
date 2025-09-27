package com.dieti.dietiestatesbackend.controller;

import com.dieti.dietiestatesbackend.dto.request.AuthRequest;
import com.dieti.dietiestatesbackend.dto.response.AuthResponse;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.security.AccessTokenProvider;
import com.dieti.dietiestatesbackend.security.GoogleTokenValidator;
import com.dieti.dietiestatesbackend.security.RefreshTokenProvider;
import com.dieti.dietiestatesbackend.service.AuthenticationService;
import com.dieti.dietiestatesbackend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationService authService;

    @Mock
    private ScheduledExecutorService scheduler;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenProvider refreshTokenProvider;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private GoogleTokenValidator googleTokenValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Manually inject the mocks instead of using @InjectMocks
        authController = new AuthController(
            userService, 
            authService, 
            scheduler, 
            authenticationManager, 
            refreshTokenProvider, 
            accessTokenProvider, 
            googleTokenValidator
        );
        // NOTE: You'll need to adjust the constructor parameters above 
        // to match your actual AuthController constructor
    }
    @Test
    void login_shouldReturnAuthResponse_whenCredentialsAreValid() {
        // Given
        AuthRequest authRequest = new AuthRequest("test@example.com", "123Pass@");
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setPassword("123Pass@");
        user.setAgent(false);
        user.setManager(false);

        AuthResponse expectedAuthResponse = new AuthResponse("accessToken", "refreshToken", List.of("ROLE_USER"));

        when(userService.getUsernameFromEmail(eq(authRequest.getEmail()))).thenReturn("testuser");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mock(Authentication.class));
        when(userService.getUserByUsername(eq("testuser"))).thenReturn(user);
        when(accessTokenProvider.generateAccessToken(eq(user))).thenReturn("accessToken");
        when(refreshTokenProvider.generateRefreshToken(eq("testuser"))).thenReturn("refreshToken");

        // When
        ResponseEntity<Object> responseEntity = authController.login(authRequest);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedAuthResponse.getAccessToken(), ((AuthResponse)responseEntity.getBody()).getAccessToken());
    }

    @Test
    void login_shouldReturnUnauthorized_whenUserNotFound() {
        // Given
        AuthRequest authRequest = new AuthRequest("nonexistent@example.com", "password");

        when(userService.getUsernameFromEmail(authRequest.getEmail())).thenReturn(null);

        // When
        ResponseEntity<Object> responseEntity = authController.login(authRequest);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    }
}