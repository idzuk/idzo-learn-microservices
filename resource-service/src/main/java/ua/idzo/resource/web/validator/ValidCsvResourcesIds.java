package ua.idzo.resource.web.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ua.idzo.resource.web.validator.impl.CsvResourcesIdsValidator;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CsvResourcesIdsValidator.class)
public @interface ValidCsvResourcesIds {

    String message() default "CSV string format is invalid or exceeds length restrictions.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
