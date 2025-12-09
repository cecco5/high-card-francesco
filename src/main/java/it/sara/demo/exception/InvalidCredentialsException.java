package it.sara.demo.exception;

import it.sara.demo.dto.StatusDTO;

/**
 * Exception thrown when user credentials are invalid during authentication.
 */
public class InvalidCredentialsException extends GenericException {

    /**
     * Creates an InvalidCredentialsException with a custom message.
     *
     * @param message The error message
     */
    public InvalidCredentialsException(String message) {
        super(401, message);
    }

    /**
     * Creates an InvalidCredentialsException with a custom StatusDTO.
     *
     * @param status The status containing error details
     */
    public InvalidCredentialsException(StatusDTO status) {
        super(status);
    }
}

