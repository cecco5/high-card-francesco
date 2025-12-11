package it.sara.demo.web.login.request;

import it.sara.demo.web.request.GenericRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Web layer DTO for login credentials.
 *
 * <p>Contains user credentials validated at the web layer
 * before being processed for authentication.</p>
 */
@Getter
@Setter
public class LoginRequest extends GenericRequest {

  /** Username for authentication (required, not blank). */
  @NotBlank(message = "Username is required")
  private String username;

  /** Password for authentication (required, not blank). */
  @NotBlank(message = "Password is required")
  private String password;
}

