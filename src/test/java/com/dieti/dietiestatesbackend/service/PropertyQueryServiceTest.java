package com.dieti.dietiestatesbackend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.dieti.dietiestatesbackend.entities.CommercialProperty;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.repositories.PropertyRepository;
import com.dieti.dietiestatesbackend.util.BoundingBoxUtility;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class PropertyQueryServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private BoundingBoxUtility boundingBoxUtility;

    @InjectMocks
    private PropertyQueryService service;

    @Test
    void getPropertiesByIds_null_throwsNpe() {
        assertThrows(NullPointerException.class, () -> service.getPropertiesByIds(null));
    }

    @Test
    void getPropertiesByIds_empty_returnsEmptyAndDoesNotCallRepo() {
        List<String> input = List.of();

        List<Property> result = service.getPropertiesByIds(input);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(propertyRepository);
    }

    @Test
    void getPropertiesByIds_allValid_callsRepoWithConvertedIdsAndReturns() {
        List<String> input = Arrays.asList("1", "2", "42");
        List<Property> repoReturn = Arrays.asList(new CommercialProperty(), new CommercialProperty(), new CommercialProperty());
        when(propertyRepository.findAllDetailedByIdIn(anyList())).thenReturn(repoReturn);

        List<Property> result = service.getPropertiesByIds(input);

        assertSame(repoReturn, result);

        verify(propertyRepository).findAllDetailedByIdIn(eq(Arrays.asList(1L, 2L, 42L)));
    }

    @Test
    void getPropertiesByIds_ignoresInvalidIds_andCallsRepoWithOnlyValidLongs() {
        List<String> input = Arrays.asList("not-a-number", "3", "", "7", "abc", null);
        List<Property> repoReturn = Arrays.asList(new CommercialProperty(), new CommercialProperty());
        when(propertyRepository.findAllDetailedByIdIn(anyList())).thenReturn(repoReturn);

        List<Property> result = service.getPropertiesByIds(input);

        assertSame(repoReturn, result);

        verify(propertyRepository).findAllDetailedByIdIn(eq(Arrays.asList(3L, 7L)));
    }
}