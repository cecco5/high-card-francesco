package it.sara.demo.service.util;

import it.sara.demo.exception.SqlInjectionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Utility class for string operations, validation, and sanitization.
 *
 * <p>Provides reusable methods for:
 * <ul>
 *   <li>Null/empty checks</li>
 *   <li>Input sanitization (removing dangerous characters)</li>
 *   <li>SQL injection detection</li>
 * </ul>
 */
@Slf4j
@Component
public class StringUtil {

  /**
   * Checks if a string is null or empty.
   *
   * @param str String to check
   * @return true if string is null or empty
   */
  public boolean isNullOrEmpty(String str) {
    return str == null || str.isEmpty();
  }

  /**
   * Sanitizes user input by removing potentially dangerous SQL and XSS characters.
   * Applied as defense in depth, even after Bean Validation.
   *
   * <p><strong>Removed characters:</strong>
   * <ul>
   *   <li>{@code ;} - SQL statement terminator</li>
   *   <li>{@code <} {@code >} - XML/XSS attack vectors</li>
   *   <li>{@code "} {@code '} - SQL string delimiters</li>
   *   <li>{@code \} - Escape character</li>
   * </ul>
   *
   * @param input User input string to sanitize
   * @return Sanitized string with dangerous characters removed, or null if input is null
   */
  public String sanitizeInput(String input) {
    if (input == null) {
      return null;
    }
    // Remove SQL dangerous characters and trim whitespace
    return input.trim().replaceAll("[;<>\"'\\\\]", "");
  }

  /**
   * Validates user input against common SQL injection patterns.
   * Detects SQL keywords and comment markers in the provided text.
   *
   * <p><strong>Detected patterns:</strong>
   * <ul>
   *   <li>SQL DML keywords: DROP, DELETE, INSERT, UPDATE, SELECT</li>
   *   <li>SQL comment markers: {@code --}, {@code /*}, {@code * /}</li>
   * </ul>
   *
   * @param inputs Variable number of strings to validate
   * @throws SqlInjectionException if SQL patterns are detected
   */
  public void validateAgainstSqlInjection(String... inputs) throws SqlInjectionException {
    // SQL keywords commonly used in injection attacks
    String[] sqlKeywords = {"DROP", "DELETE", "INSERT", "UPDATE", "SELECT", "--", "/*", "*/"};

    // Combine all input fields for comprehensive check
    String fullInput = String.join(" ", inputs).toUpperCase();

    // Check for SQL keywords
    for (String keyword : sqlKeywords) {
      if (fullInput.contains(keyword)) {
        if (log.isWarnEnabled()) {
          log.warn("SQL injection attempt detected. Keyword found: {}", keyword);
        }
        throw new SqlInjectionException("Invalid input: SQL keywords detected");
      }
    }
  }

  /**
   * Checks if a user field matches the search query (case-insensitive).
   *
   * @param text Text to search in
   * @param query Search query (can be null or empty)
   * @return true if text contains query (case-insensitive), or if query is null/blank
   */
  public boolean containsIgnoreCase(String text, String query) {
    if (query == null || query.isBlank()) {
      return true;
    }
    if (text == null) {
      return false;
    }
    return text.toLowerCase().contains(query.toLowerCase().trim());
  }
}
