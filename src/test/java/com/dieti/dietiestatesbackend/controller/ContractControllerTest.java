package com.dieti.dietiestatesbackend.controller;

import com.dieti.dietiestatesbackend.entities.Contract;
import com.dieti.dietiestatesbackend.repositories.ContractRepository;
import com.dieti.dietiestatesbackend.security.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ContractControllerTest {

    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private ContractController contractController;

    @BeforeEach
    void setUp() {
        // Mock authentication for @PreAuthorize
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(1L, "manager@example.com", true, List.of(new SimpleGrantedAuthority("ROLE_MANAGER")));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(authenticatedUser, null, authenticatedUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void getAllContracts_shouldReturnContracts_whenManagerIsAuthorized() throws Exception {
        // Given
        Contract contract = new Contract();
        contract.setId(1L);
        // Set other contract properties as needed for a meaningful test
        when(contractRepository.findAll()).thenReturn(Collections.singletonList(contract));

        // When
        List<Contract> responseEntity = contractController.getAllContracts();

        // Then
        assertEquals(1, responseEntity.size());
        assertEquals(1L, responseEntity.get(0).getId());
    }
}