package it.sara.demo.web.login;

import it.sara.demo.dto.LoginResponseDTO;
import it.sara.demo.dto.StatusDTO;
import it.sara.demo.exception.InvalidCredentialsException;
import it.sara.demo.security.util.JwtUtil;
import it.sara.demo.web.login.request.LoginRequest;
import it.sara.demo.web.login.response.LoginWebResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for user authentication operations.
 *
 * <p>Handles login requests and JWT token generation.</p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final JwtUtil jwtUtil;

  /**
   * Constructor-based dependency injection.
   *
   * @param jwtUtil Utility for JWT token operations
   */
  public AuthController(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  /**
   * Authenticates user and generates JWT token.
   *
   * <p>Validates credentials (hardcoded for demo) and returns JWT token on success.
   * In production, validate against database with hashed passwords.</p>
   *
   * @param request Login credentials
   * @return LoginWebResponse with JWT token
   * @throws InvalidCredentialsException if credentials are invalid
   */
  @PostMapping("/login")
  public ResponseEntity<LoginWebResponse> login(@Valid @RequestBody LoginRequest request)
      throws InvalidCredentialsException {
    if ("admin".equals(request.getUsername()) && "password".equals(request.getPassword())) {
      String token = jwtUtil.generateToken("admin", List.of("ROLE_ADMIN"));

      LoginResponseDTO loginData = new LoginResponseDTO(token, "Bearer");

      LoginWebResponse response = new LoginWebResponse();
      StatusDTO status = new StatusDTO();
      status.setCode(200);
      status.setMessage("Login successful");
      status.setTraceId(UUID.randomUUID().toString());
      response.setStatus(status);
      response.setData(loginData);

      return ResponseEntity.ok(response);
    }

    throw new InvalidCredentialsException("Invalid username or password");
  }
}
