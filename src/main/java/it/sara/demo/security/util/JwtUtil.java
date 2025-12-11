package it.sara.demo.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utility class for JWT token generation and validation.
 *
 * <p>Handles creation of JWT tokens with username, roles, issuer, and expiration. Validates tokens
 * by verifying signature, issuer, and expiration time.
 */
@Component
public class JwtUtil {

  private final String secretKey;
  private final String issuer;
  private final long expiration;

  /**
   * Constructor-based dependency injection for JWT configuration.
   *
   * @param secretKey Secret key for signing tokens (from application.properties)
   * @param issuer Token issuer identifier (from application.properties)
   * @param expiration Token expiration time in milliseconds (from application.properties)
   */
  public JwtUtil(
      @Value("${jwt.secret}") String secretKey,
      @Value("${jwt.issuer}") String issuer,
      @Value("${jwt.expiration}") long expiration) {
    this.secretKey = secretKey;
    this.issuer = issuer;
    this.expiration = expiration;
  }

  /**
   * Generates a JWT token for the specified user.
   *
   * @param username The user's username
   * @param roles List of user roles for authorization
   * @return Signed JWT token string
   */
  public String generateToken(String username, List<String> roles) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expiration);

    return Jwts.builder()
        .subject(username)
        .claim("roles", roles)
        .issuer(issuer)
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(getSigningKey())
        .compact();
  }

  /**
   * Validates a JWT token by verifying signature, issuer, and expiration.
   *
   * @param token JWT token to validate
   * @return Claims contained in the token
   * @throws io.jsonwebtoken.JwtException if token is invalid, expired, or has wrong issuer
   */
  public Claims validateToken(String token) {
    return Jwts.parser()
        .verifyWith((SecretKey) getSigningKey())
        .requireIssuer(issuer)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  /**
   * Generates the signing key from the configured secret.
   *
   * @return HMAC-SHA key for signing and verifying JWT tokens
   */
  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
  }
}
