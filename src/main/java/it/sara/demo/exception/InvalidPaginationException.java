package it.sara.demo.exception;

/**
 * Exception thrown when pagination parameters are invalid.
 *
 * <p>This is a domain/business exception that extends {@link GenericException} so it can be
 * handled uniformly by the {@code GlobalExceptionHandler}. Typical causes include negative offset
 * or non-positive limit values.
 */
public class InvalidPaginationException extends GenericException {

  /**
   * Creates an InvalidPaginationException with a default error code 400 and the provided message.
   *
   * @param message Human-readable description of the pagination error
   */
  public InvalidPaginationException(String message) {
    super(400, message);
  }
}

