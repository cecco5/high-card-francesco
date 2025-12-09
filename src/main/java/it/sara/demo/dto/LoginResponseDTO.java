package it.sara.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for login response containing JWT token.
 *
 * <p>Follows the naming convention of other DTOs in this package
 * (StatusDTO, UserDTO) for consistency.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    /**
     * The JWT token to be used for authenticated requests.
     */
    private String token;

    /**
     * The token type (always "Bearer" for JWT).
     */
    private String tokenType;
}


