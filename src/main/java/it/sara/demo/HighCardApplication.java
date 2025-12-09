package it.sara.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

/**
 * Main application class for High Card demo.
 *
 * <p>Excludes {@link UserDetailsServiceAutoConfiguration} since we use JWT-based
 * authentication instead of default Spring Security password authentication.</p>
 */
@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class HighCardApplication {

  public static void main(String[] args) {
    SpringApplication.run(HighCardApplication.class, args);
  }
}
