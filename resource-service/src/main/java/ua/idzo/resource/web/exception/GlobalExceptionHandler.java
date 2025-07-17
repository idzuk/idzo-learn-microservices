package ua.idzo.resource.web.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import ua.idzo.resource.core.exception.NotFoundRuntimeException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        String supportedTypes = ex.getSupportedMediaTypes().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        String errorMessage = String.format("Unsupported media type. Supported types are: %s", supportedTypes);
        Map<String, String> body = getErrorResponseBody(errorMessage, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundRuntimeException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NotFoundRuntimeException ex) {
        Map<String, String> body = getErrorResponseBody(ex.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        Map<String, Object> body = getDetailedErrorResponseBody("Validation error", HttpStatus.BAD_REQUEST,
                details);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();

        Map<String, String> details = violations.stream()
                .collect(Collectors.toMap(
                        v -> getParameterName(v.getPropertyPath().toString()),
                        ConstraintViolation::getMessage
                ));

        String errorMessage;
        if (violations.size() == 1) {
            errorMessage = violations.iterator().next().getMessage();
        } else {
            errorMessage = "Validation error";
        }

        Map<String, Object> body = getDetailedErrorResponseBody(errorMessage, HttpStatus.BAD_REQUEST, details);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String paramName = ex.getName();
        Object invalidValue = ex.getValue();
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";

        String errorMessage = String.format(
                "Invalid value '%s' for parameter '%s'. Expected a value of type '%s'.",
                invalidValue, paramName, requiredType);

        Map<String, String> body = getErrorResponseBody(errorMessage, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    private static Map<String, String> getErrorResponseBody(String errorMessage, HttpStatus errorCode) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("errorMessage", errorMessage);
        body.put("errorCode", String.valueOf(errorCode.value()));
        return body;
    }

    private static Map<String, Object> getDetailedErrorResponseBody(
            String errorMessage, HttpStatus errorCode, Map<String, String> details
    ) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("errorMessage", errorMessage);
        body.put("details", details);
        body.put("errorCode", String.valueOf(errorCode.value()));
        return body;
    }

    private String getParameterName(String propertyPath) {
        String[] parts = propertyPath.split("\\.");
        return parts.length > 1 ? parts[parts.length - 1] : propertyPath;
    }
}
