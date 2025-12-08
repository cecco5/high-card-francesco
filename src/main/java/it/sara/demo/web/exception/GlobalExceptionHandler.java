package it.sara.demo.web.exception;

import it.sara.demo.dto.StatusDTO;
import it.sara.demo.exception.GenericException;
import it.sara.demo.web.response.GenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Global exception handler for centralized error management.
 *
 * <p>All responses, including errors, return HTTP status 200 with error details
 * in the {@link StatusDTO} object, as per application requirements.</p>
 *
 * <p>This handler catches:</p>
 * <ul>
 *   <li>Bean Validation errors ({@link MethodArgumentNotValidException})</li>
 *   <li>Business logic errors ({@link GenericException})</li>
 *   <li>Unexpected errors ({@link Exception})</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles Bean Validation errors from {@code @Valid} annotations.
     *
     * <p>Triggered when request validation fails (e.g., {@code @NotBlank}, {@code @Email}).</p>
     *
     * <p>Returns HTTP 200 with error details in StatusDTO.</p>
     *
     * @param ex The validation exception containing field errors
     * @return ResponseEntity with HTTP 200 and validation error messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String traceId = UUID.randomUUID().toString();

        // Collect all validation error messages
        String errorMessage = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining("; "));

        if (log.isWarnEnabled()) {
            log.warn("Validation error [traceId: {}]: {}", traceId, errorMessage);
        }

        GenericResponse response = new GenericResponse();
        StatusDTO status = new StatusDTO();
        status.setCode(400);
        status.setMessage(errorMessage);
        status.setTraceId(traceId);
        response.setStatus(status);

        // Return HTTP 200 with error details in body
        return ResponseEntity.ok(response);
    }

    /**
     * Handles business logic exceptions thrown by service layer.
     *
     * <p>Used for expected errors like SQL injection detection, validation failures, etc.</p>
     *
     * <p>Returns HTTP 200 with error details in StatusDTO.</p>
     *
     * @param ex The business exception with error code and message
     * @return ResponseEntity with HTTP 200 and error details
     */
    @ExceptionHandler(GenericException.class)
    public ResponseEntity<GenericResponse> handleGenericException(GenericException ex) {
        String traceId = UUID.randomUUID().toString();

        StatusDTO status = ex.getStatus();

        if (log.isWarnEnabled()) {
            log.warn("Business error [traceId: {}]: {} (code: {})",
                traceId, status.getMessage(), status.getCode());
        }

        // Update traceId if not already set
        if (status.getTraceId() == null) {
            status.setTraceId(traceId);
        }

        GenericResponse response = new GenericResponse();
        response.setStatus(status);

        // Return HTTP 200 with error details in body
        return ResponseEntity.ok(response);
    }

    /**
     * Handles unexpected exceptions not caught by other handlers.
     *
     * <p>Logs the full stack trace for debugging and returns a generic error message.</p>
     *
     * <p>Returns HTTP 200 with error details in StatusDTO.</p>
     *
     * @param ex The unexpected exception
     * @return ResponseEntity with HTTP 200 and generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse> handleGenericException(Exception ex) {
        String traceId = UUID.randomUUID().toString();

        if (log.isErrorEnabled()) {
            log.error("Unexpected error [traceId: {}]: {}", traceId, ex.getMessage(), ex);
        }

        GenericResponse response = new GenericResponse();
        StatusDTO status = new StatusDTO();
        status.setCode(500);
        status.setMessage("Internal server error. Please contact support with traceId: " + traceId);
        status.setTraceId(traceId);
        response.setStatus(status);

        // Return HTTP 200 with error details in body
        return ResponseEntity.ok(response);
    }
}

