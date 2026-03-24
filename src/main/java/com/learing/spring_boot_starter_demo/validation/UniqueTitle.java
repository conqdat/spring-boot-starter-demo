package com.learing.spring_boot_starter_demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueTitleValidator.class)
public @interface UniqueTitle {

    String message() default "Title must be unique";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}