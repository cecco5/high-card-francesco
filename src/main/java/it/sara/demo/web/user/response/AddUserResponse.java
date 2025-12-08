package it.sara.demo.web.user.response;

import it.sara.demo.dto.UserDTO;
import it.sara.demo.web.response.GenericResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * Response DTO for user creation operations.
 *
 * <p>Contains the created user data along with the standard status information.</p>
 */
@Getter
@Setter
public class AddUserResponse extends GenericResponse {

    /**
     * The user that was created, including the generated GUID.
     */
    private UserDTO user;
}
