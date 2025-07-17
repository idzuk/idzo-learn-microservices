package ua.idzo.song.web.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.PayloadApplicationEvent;

import jakarta.validation.Constraint;
import ua.idzo.song.web.validator.impl.CsvResourcesIdsValidator;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CsvResourcesIdsValidator.class)
public @interface ValidCsvResourcesIds {

    String message() default "CSV string format is invalid or exceeds length restrictions.";

    Class<?>[] groups() default {};

    Class<? extends PayloadApplicationEvent>[] payload() default {};
}
