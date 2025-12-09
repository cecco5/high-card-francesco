package it.sara.demo.security.config;

import it.sara.demo.security.filters.JwtAuthenticationFilter;
import it.sara.demo.web.exception.ErrorResponseHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for JWT-based authentication.
 *
 * <p>Configures endpoint access rules, disables CSRF for stateless JWT,
 * and integrates JWT authentication filter.</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  /**
   * Constructor-based dependency injection.
   *
   * @param jwtAuthenticationFilter Filter for JWT token validation
   */
  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

  /**
   * Configures the security filter chain for HTTP requests.
   *
   * <p>Access rules:</p>
   * <ul>
   *   <li>/api/auth/** - Public (login endpoint)</li>
   *   <li>/api/users/** - Requires ADMIN role</li>
   *   <li>All other endpoints - Requires authentication</li>
   * </ul>
   *
   * @param http HttpSecurity configuration
   * @return Configured SecurityFilterChain
   * @throws Exception if configuration fails
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/api/auth/**")
                    .permitAll()
                    .requestMatchers("/api/users/**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(
            ex ->
                ex.authenticationEntryPoint(
                    (req, res, authEx) -> ErrorResponseHelper.writeErrorResponse(res, 401, "Unauthorized")));

    return http.build();
  }

  /**
   * Provides a no-op AuthenticationManager to disable default password generation.
   *
   * <p>Since we use JWT-based authentication, we don't need Spring Security's
   * default authentication mechanism with auto-generated passwords.</p>
   *
   * @return AuthenticationManager that does nothing
   */
  @Bean
  public AuthenticationManager authenticationManager() {
    return authentication -> {
      throw new UnsupportedOperationException("JWT authentication only - no password authentication");
    };
  }
}
