package it.sara.demo.exception;

import it.sara.demo.dto.StatusDTO;

/**
 * Exception thrown when SQL injection patterns are detected in user input.
 *
 * <p>This exception is thrown during input validation when dangerous SQL keywords
 * or patterns are found, indicating a potential SQL injection attack.</p>
 */
public class SqlInjectionException extends GenericException {

    /**
     * Creates a SqlInjectionException with a custom message.
     *
     * @param message The error message describing the detected pattern
     */
    public SqlInjectionException(String message) {
        super(400, message);
    }

    /**
     * Creates a SqlInjectionException with a custom StatusDTO.
     *
     * @param status The status containing error details
     */
    public SqlInjectionException(StatusDTO status) {
        super(status);
    }
}


