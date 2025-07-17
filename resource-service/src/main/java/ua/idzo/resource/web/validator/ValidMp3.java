package ua.idzo.resource.web.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ua.idzo.resource.web.validator.impl.Mp3Validator;

@Target({ ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Mp3Validator.class)
public @interface ValidMp3 {

    String message() default "The request body is invalid MP3";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
