package ua.idzo.resource.web.validator.impl;

import java.util.Arrays;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ua.idzo.resource.web.validator.ValidCsvResourcesIds;

public class CsvResourcesIdsValidator implements ConstraintValidator<ValidCsvResourcesIds, String> {

    private static final int MAX_LENGTH = 200;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        if (value.length() >= MAX_LENGTH) {
            String message = String.format(
                    "CSV string exceeds the maximum length of %d. Actual length: '%d' ",
                    MAX_LENGTH, value.length());

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        boolean allPartsAreNumeric = Arrays.stream(value.split(","))
                .map(String::trim)
                .allMatch(s -> {
                    if (s.isEmpty()) return false;
                    try {
                        Integer.parseInt(s);
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                });

        if (!allPartsAreNumeric) {
            String message = String.format(
                    "The provided CSV string '%s' contains invalid, non-numeric, or empty values.", value);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        return true;
    }
}
