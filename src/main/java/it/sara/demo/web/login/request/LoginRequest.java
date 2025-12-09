package it.sara.demo.web.login.request;

import it.sara.demo.web.request.GenericRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Web layer DTO for login credentials.
 */
@Getter
@Setter
public class LoginRequest extends GenericRequest {

  @NotBlank(message = "Username is required")
  private String username;

  @NotBlank(message = "Password is required")
  private String password;
}

