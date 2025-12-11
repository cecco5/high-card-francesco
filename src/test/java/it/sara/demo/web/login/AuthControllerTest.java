package it.sara.demo.web.login;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import it.sara.demo.exception.InvalidCredentialsException;
import it.sara.demo.security.util.JwtUtil;
import it.sara.demo.web.login.request.LoginRequest;
import it.sara.demo.web.login.response.LoginWebResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for {@link AuthController}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Unit Tests")
class AuthControllerTest {

  @Mock private JwtUtil jwtUtil;

  @InjectMocks private AuthController authController;

  @Test
  @DisplayName("login - returns JWT when credentials are valid")
  void login_ShouldReturnToken_WhenCredentialsValid() throws InvalidCredentialsException {
    LoginRequest request = new LoginRequest();
    request.setUsername("admin");
    request.setPassword("password");

    when(jwtUtil.generateToken(eq("admin"), eq(List.of("ROLE_ADMIN")))).thenReturn("mock-token");

    ResponseEntity<LoginWebResponse> response = authController.login(request);

    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertEquals("mock-token", response.getBody().getToken());
    assertEquals("Bearer", response.getBody().getTokenType());

    verify(jwtUtil, times(1)).generateToken("admin", List.of("ROLE_ADMIN"));
  }

  @Test
  @DisplayName("login - throws InvalidCredentialsException when credentials invalid")
  void login_ShouldThrow_WhenCredentialsInvalid() {
    LoginRequest request = new LoginRequest();
    request.setUsername("admin");
    request.setPassword("wrong");

    assertThrows(InvalidCredentialsException.class, () -> authController.login(request));

    verify(jwtUtil, never()).generateToken(anyString(), anyList());
  }
}
