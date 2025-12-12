package it.sara.demo.exception;

/**
 * Exception thrown when attempting to add a user that already exists in the system.
 *
 * <p>A user is considered to exist if another user with the same first name, last name,
 * and email is already present in the database.</p>
 */
public class UserAlreadyExistsException extends GenericException {

  /**
   * Creates a UserAlreadyExistsException with a custom message.
   *
   * @param message The error message describing which user already exists
   */
  public UserAlreadyExistsException(String message) {
    super(409, message);
  }
}

