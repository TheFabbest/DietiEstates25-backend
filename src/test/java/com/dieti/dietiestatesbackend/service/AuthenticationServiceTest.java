package com.dieti.dietiestatesbackend.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;

import com.dieti.dietiestatesbackend.dto.request.SignupRequest;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.security.RefreshTokenProvider;
import com.dieti.dietiestatesbackend.security.RefreshTokenProvider.TokenValidationResult;
import com.dieti.dietiestatesbackend.service.AuthenticationService.LogoutResult;



class AuthenticationServiceTest {

    private UserQueryService userQueryService;
    private UserManagementService userManagementService;
    private PasswordValidator passwordValidator;
    private RefreshTokenProvider refreshTokenProvider;
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        userQueryService = mock(UserQueryService.class);
        userManagementService = mock(UserManagementService.class);
        passwordValidator = new PasswordValidator();
        refreshTokenProvider = mock(RefreshTokenProvider.class);
        authenticationService = new AuthenticationService(
                userQueryService,
                userManagementService,
                passwordValidator,
                refreshTokenProvider
        );
    }

    @Test
    void registerNewUser_shouldThrowIfUserExists() {
        SignupRequest request = new SignupRequest("test@example.com", "Password1!", "testuser", "Test", "User");
        when(userQueryService.doesUserExist("test@example.com")).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                authenticationService.registerNewUser(request)
        );
        assertEquals("Utente gia' registrato", ex.getMessage());
    }

    @Test
    void registerNewUser_shouldThrowIfPasswordIsWeak() {
        SignupRequest request = new SignupRequest("test@example.com", "weak", "testuser", "Test", "User");
        when(userQueryService.doesUserExist("test@example.com")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                authenticationService.registerNewUser(request)
        );
        assertTrue(ex.getMessage().contains("Password debole"));
    }

    @Test
    void registerNewUser_shouldCreateUserAndReturnUser() {
        SignupRequest request = new SignupRequest("test@example.com", "testuser", "Password1@", "Test", "User");
        when(userQueryService.doesUserExist("test@example.com")).thenReturn(false);

        User expectedUser = new User();
        when(userQueryService.getUserByUsername("testuser")).thenReturn(expectedUser);

        User result = authenticationService.registerNewUser(request);

        verify(userManagementService).createUser(
                "test@example.com",
                "Password1@",
                "testuser",
                "Test",
                "User"
        );
        assertEquals(expectedUser, result);
    }

    @Test
    void successfulLogout_shouldInvalidateRefreshToken() {
        String refreshToken = "validRefreshToken";
        when(refreshTokenProvider.validateTokenForLogout(refreshToken)).thenReturn(new TokenValidationResult(true, refreshToken, refreshToken));
        doNothing().when(refreshTokenProvider).deleteByTokenValue(refreshToken);
        LogoutResult result = authenticationService.logout(refreshToken);
        assertEquals(HttpStatus.OK, result.status());
        assertTrue(result.success());
        verify(refreshTokenProvider).deleteByTokenValue(refreshToken);
    }
}