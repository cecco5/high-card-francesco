package it.sara.demo.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object representing user information.
 *
 * <p>Used to transfer user data between service and web layers,
 * maintaining separation of concerns as per the application architecture.</p>
 */
@Getter
@Setter
public class UserDTO {
    /** Unique identifier for the user (UUID format). */
    private String guid;

    /** User's first name. */
    private String firstName;

    /** User's last name. */
    private String lastName;

    /** User's email address. */
    private String email;

    /** User's phone number. */
    private String phoneNumber;
}
