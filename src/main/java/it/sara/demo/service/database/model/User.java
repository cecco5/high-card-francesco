package it.sara.demo.service.database.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing a user in the system.
 *
 * <p>This is the service layer model used by the business logic
 * and persisted in the {@code FakeDatabase}.</p>
 */
@Getter
@Setter
public class User {
    /** Unique identifier for the user (UUID format). */
    private String guid;

    /** User's first name. */
    private String firstName;

    /** User's last name. */
    private String lastName;

    /** User's email address. */
    private String email;

    /** User's phone number in Italian format. */
    private String phoneNumber;
}
