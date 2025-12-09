package it.sara.demo.web.login.response;

import it.sara.demo.dto.LoginResponseDTO;
import it.sara.demo.web.response.GenericResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * Response DTO for login operations.
 *
 * <p>Contains the JWT token along with the standard status information.</p>
 */
@Getter
@Setter
public class LoginWebResponse extends GenericResponse {

    /**
     * The login data containing the JWT token.
     */
    private LoginResponseDTO data;
}

