package it.sara.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for login response containing JWT token.
 *
 * <p>Used to transfer authentication token information from service
 * to web layer after successful login.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

  /** The JWT authentication token. */
  private String token;

  /** The type of token (typically "Bearer"). */
  private String tokenType;
}
