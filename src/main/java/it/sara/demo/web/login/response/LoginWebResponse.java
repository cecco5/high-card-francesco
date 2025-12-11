package it.sara.demo.web.login.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Response DTO for login operations.
 *
 * <p>Contains the JWT token and its type.</p>
 */
@Getter
@Setter
public class LoginWebResponse {

    /**
     * The JWT token returned by the authentication process.
     */
    private String token;

    /**
     * The token type, e.g., "Bearer".
     */
    private String tokenType;
}
