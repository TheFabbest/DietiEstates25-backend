package com.dieti.dietiestatesbackend.validation;

import com.dieti.dietiestatesbackend.entities.PropertyCategory;
import com.dieti.dietiestatesbackend.dto.request.CreatePropertyRequest;
import com.dieti.dietiestatesbackend.service.lookup.CategoryLookupService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Validator class-level per @ValidPropertyCategory.
 * - Si occupa solo della coerenza tra propertyType della request e propertyType registrato nella PropertyCategory.
 * - Se la categoria non esiste, lascia che altre validazioni (@ExistingEntity) la gestiscano.
 */
@Component
public class ValidPropertyCategoryValidator implements ConstraintValidator<ValidPropertyCategory, CreatePropertyRequest> {

    private final CategoryLookupService categoryLookupService;

    @Autowired
    public ValidPropertyCategoryValidator(CategoryLookupService categoryLookupService) {
        this.categoryLookupService = categoryLookupService;
    }

    @Override
    public void initialize(ValidPropertyCategory constraintAnnotation) {
        // No initialization required
    }

    @Override
    public boolean isValid(CreatePropertyRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }

        String categoryName = request.getPropertyCategoryName();
        if (categoryName == null || request.getPropertyType() == null) {
            // Lasciare che altre annotazioni (@NotNull, @ExistingEntity) segnalino errori quando necessario
            return true;
        }

        Optional<PropertyCategory> categoryOpt = categoryLookupService.findByName(categoryName);
        if (categoryOpt.isEmpty()) {
            // Esistenza della categoria è verificata da @ExistingEntity sul campo; evita duplicare l'errore qui
            return true;
        }

        PropertyCategory category = categoryOpt.get();
        boolean matches = category.getPropertyType().equals(request.getPropertyType().name());
        if (!matches) {
            // Costruisci una violazione associata al campo propertyCategoryName per fornire feedback utile al client
            context.disableDefaultConstraintViolation();
            String message = "La categoria '" + categoryName + "' non corrisponde al tipo di proprietà '" + request.getPropertyType().name() + "'.";
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode("propertyCategoryName")
                    .addConstraintViolation();
        }

        return matches;
    }
}