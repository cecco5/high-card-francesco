package it.sara.demo.security.util;

import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link JwtUtil}. */
@DisplayName("JwtUtil Unit Tests")
class JwtUtilTest {

  private JwtUtil jwtUtil;

  private static final String TEST_SECRET =
      "testSecretKeyForJwtTokenGenerationAndValidationMustBeLongEnough12345";
  private static final String TEST_ISSUER = "test-issuer";
  private static final long TEST_EXPIRATION = 3600000L; // 1 hour

  @BeforeEach
  void setUp() {
    jwtUtil = new JwtUtil(TEST_SECRET, TEST_ISSUER, TEST_EXPIRATION);
  }

  @Test
  @DisplayName("generateToken - Should generate non-empty JWT token")
  void testGenerateToken_NonEmpty() {
    String token = jwtUtil.generateToken("testuser", List.of("ROLE_USER"));
    assertNotNull(token);
    assertFalse(token.isBlank());
    assertTrue(token.contains(".")); // header.payload.signature
  }

  @Test
  @DisplayName("validateToken - Should parse valid token and return claims")
  void testValidateToken_Valid() {
    String username = "testuser";
    List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");

    String token = jwtUtil.generateToken(username, roles);

    Claims claims = jwtUtil.validateToken(token);

    assertNotNull(claims);
    assertEquals(username, claims.getSubject());
    assertEquals(TEST_ISSUER, claims.getIssuer());
    assertEquals(roles, claims.get("roles", List.class));
    assertNotNull(claims.getIssuedAt());
    assertNotNull(claims.getExpiration());
  }

  @Test
  @DisplayName("validateToken - Should fail for wrong issuer")
  void testValidateToken_WrongIssuer() {
    // Create a token with different issuer by using a separate JwtUtil instance
    JwtUtil otherIssuerUtil = new JwtUtil(TEST_SECRET, "other-issuer", TEST_EXPIRATION);
    String token = otherIssuerUtil.generateToken("testuser", List.of("ROLE_USER"));

    assertThrows(JwtException.class, () -> jwtUtil.validateToken(token));
  }

  @Test
  @DisplayName("validateToken - Should fail for expired token")
  void testValidateToken_Expired() throws InterruptedException {
    // JwtUtil with very short expiration
    JwtUtil shortLivedUtil = new JwtUtil(TEST_SECRET, TEST_ISSUER, 1L); // 1 ms
    String token = shortLivedUtil.generateToken("testuser", List.of("ROLE_USER"));

    // wait to ensure expiration
    Thread.sleep(10L);

    assertThrows(ExpiredJwtException.class, () -> jwtUtil.validateToken(token));
  }

  @Test
  @DisplayName("validateToken - Should fail for malformed token")
  void testValidateToken_Malformed() {
    String invalidToken = "not-a-jwt-token";

    assertThrows(JwtException.class, () -> jwtUtil.validateToken(invalidToken));
  }
}
