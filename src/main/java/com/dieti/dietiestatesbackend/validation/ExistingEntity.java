package com.dieti.dietiestatesbackend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * Annotazione di validazione che verifica l'esistenza di un'entità nel database.
 * Usata sui campi DTO che fanno riferimento ad entità esistenti (es. agentUsername, addressId, propertyCategoryName).
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistingEntityValidator.class)
public @interface ExistingEntity {

    Class<?> entityClass();

    String fieldName();

    String message() default "L'entità specificata non esiste.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}