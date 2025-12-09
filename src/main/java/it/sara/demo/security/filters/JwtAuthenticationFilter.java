package it.sara.demo.security.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import it.sara.demo.security.util.JwtUtil;
import it.sara.demo.web.exception.ErrorResponseHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter for JWT authentication on incoming HTTP requests.
 *
 * <p>Intercepts requests, extracts JWT token from Authorization header, validates it, and sets the
 * authentication context for Spring Security.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  /**
   * Constructor-based dependency injection.
   *
   * @param jwtUtil Utility for JWT token operations
   */
  public JwtAuthenticationFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  /**
   * Filters incoming requests to validate JWT tokens.
   *
   * <p>Extracts token from Authorization header, validates issuer, expiration, and signature. Sets
   * authentication context if valid, returns error response if invalid.
   *
   * @param request HTTP request
   * @param response HTTP response
   * @param filterChain Filter chain for request processing
   * @throws ServletException if servlet error occurs
   * @throws IOException if I/O error occurs
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);

      try {
        Claims claims = jwtUtil.validateToken(token);
        String username = claims.getSubject();
        List<String> roles = claims.get("roles", List.class);

        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(
                username,
                null,
                roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        SecurityContextHolder.getContext().setAuthentication(auth);

      } catch (JwtException e) {
        // Use helper to write consistent error response (same format as GlobalExceptionHandler)
        ErrorResponseHelper.writeErrorResponse(response, 401, "Invalid or expired token");
        return;
      }
    }

    filterChain.doFilter(request, response);
  }
}
