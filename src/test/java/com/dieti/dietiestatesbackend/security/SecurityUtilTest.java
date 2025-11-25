package com.dieti.dietiestatesbackend.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dieti.dietiestatesbackend.security.permissions.PermissionFacade;

@ExtendWith(MockitoExtension.class)
class SecurityUtilTest {

    @Mock
    PermissionFacade permissionFacade;

    private SecurityUtil securityUtil;

    @BeforeEach
    void setUp() {
        securityUtil = new SecurityUtil(permissionFacade);
    }

    @Test
    void managerCanAlwaysCancel() {
        AppPrincipal principal = mock(AppPrincipal.class);
        when(permissionFacade.canCancelVisit(principal, 1L)).thenReturn(true);

        boolean allowed = securityUtil.canCancelVisit(principal, 1L);

        assertTrue(allowed);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 4L})
    void usersWithPermissionShouldBeAbleToCancel(long visitId) {
        AppPrincipal principal = mock(AppPrincipal.class);
        when(permissionFacade.canCancelVisit(principal, visitId)).thenReturn(true);

        assertTrue(securityUtil.canCancelVisit(principal, visitId));
    }

    @Test
    void ownerCannotCancelConfirmedWithin24Hours() {
        AppPrincipal principal = mock(AppPrincipal.class);
        when(permissionFacade.canCancelVisit(principal, 3L)).thenReturn(false);

        assertFalse(securityUtil.canCancelVisit(principal, 3L));
    }

    @Test
    void nonAuthorizedCannotCancel() {
        AppPrincipal principal = mock(AppPrincipal.class);
        when(permissionFacade.canCancelVisit(principal, 5L)).thenReturn(false);

        assertFalse(securityUtil.canCancelVisit(principal, 5L));
    }
}