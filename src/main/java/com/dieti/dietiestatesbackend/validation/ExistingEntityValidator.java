package com.dieti.dietiestatesbackend.validation;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator che si occupa solo della logica di validazione e dei messaggi.
 * La verifica dell'esistenza è delegata a EntityExistenceChecker per rispettare SRP.
 */
public class ExistingEntityValidator implements ConstraintValidator<ExistingEntity, Object> {

    @Autowired
    private EntityExistenceChecker existenceChecker;

    private Class<?> entityClass;
    private String fieldName;
    private String message;

    @Override
    public void initialize(ExistingEntity constraintAnnotation) {
        this.entityClass = constraintAnnotation.entityClass();
        this.fieldName = constraintAnnotation.fieldName();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // delegare @NotNull se necessario
        }

        boolean exists;
        try {
            exists = existenceChecker.exists(entityClass, fieldName, value);
        } catch (Exception ex) {
            // In caso di errore del checker consideriamo la validazione fallita
            exists = false;
        }

        if (!exists) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        return true;
    }
}