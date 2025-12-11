package it.sara.demo.web.login;

import it.sara.demo.exception.InvalidCredentialsException;
import it.sara.demo.security.util.JwtUtil;
import it.sara.demo.web.login.request.LoginRequest;
import it.sara.demo.web.login.response.LoginWebResponse;
import jakarta.validation.Valid;
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
    // TODO: Hardcoded credentials are for demo purposes only.
    //  In production, retrieve user credentials from database (e.g., via UserRepository/UserService)
    //  and validate using BCryptPasswordEncoder or similar hashing algorithm.
    if ("admin".equals(request.getUsername()) && "password".equals(request.getPassword())) {
      String token = jwtUtil.generateToken("admin", java.util.List.of("ROLE_ADMIN"));

      LoginWebResponse response = new LoginWebResponse();
      response.setToken(token);
      response.setTokenType("Bearer");

      return ResponseEntity.ok(response);
    }

    throw new InvalidCredentialsException("Invalid username or password");
  }
}
