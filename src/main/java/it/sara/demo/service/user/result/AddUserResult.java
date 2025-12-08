package it.sara.demo.service.user.result;

import it.sara.demo.service.database.model.User;
import it.sara.demo.service.result.GenericResult;
import lombok.Getter;
import lombok.Setter;

/**
 * Result object for user creation operations in the service layer.
 *
 * <p>Contains the created user entity to be returned to the web layer.</p>
 */
@Getter
@Setter
public class AddUserResult extends GenericResult {

    /**
     * The user entity that was created, including the generated GUID.
     */
    private User user;
}
