package com.dieti.dietiestatesbackend.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Test
    void agentCanCancelOwnVisit() {
        AppPrincipal principal = mock(AppPrincipal.class);
        when(permissionFacade.canCancelVisit(principal, 1L)).thenReturn(true);

        assertTrue(securityUtil.canCancelVisit(principal, 1L));
    }

    @Test
    void ownerCanCancelPendingVisit() {
        AppPrincipal principal = mock(AppPrincipal.class);
        when(permissionFacade.canCancelVisit(principal, 2L)).thenReturn(true);

        assertTrue(securityUtil.canCancelVisit(principal, 2L));
    }

    @Test
    void ownerCannotCancelConfirmedWithin24Hours() {
        AppPrincipal principal = mock(AppPrincipal.class);
        when(permissionFacade.canCancelVisit(principal, 3L)).thenReturn(false);

        assertFalse(securityUtil.canCancelVisit(principal, 3L));
    }

    @Test
    void ownerCanCancelConfirmedMoreThan24Hours() {
        AppPrincipal principal = mock(AppPrincipal.class);
        when(permissionFacade.canCancelVisit(principal, 4L)).thenReturn(true);

        assertTrue(securityUtil.canCancelVisit(principal, 4L));
    }

    @Test
    void nonAuthorizedCannotCancel() {
        AppPrincipal principal = mock(AppPrincipal.class);
        when(permissionFacade.canCancelVisit(principal, 5L)).thenReturn(false);

        assertFalse(securityUtil.canCancelVisit(principal, 5L));
    }

}