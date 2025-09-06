package com.dieti.dietiestatesbackend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import com.dieti.dietiestatesbackend.validation.ValidPropertyCategoryValidator;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPropertyCategoryValidator.class)
@Documented
public @interface ValidPropertyCategory {
    String message() default "La categoria non corrisponde al tipo di proprietà.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
