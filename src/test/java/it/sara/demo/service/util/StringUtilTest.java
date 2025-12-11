package it.sara.demo.service.util;

import it.sara.demo.exception.SqlInjectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StringUtil Focused Unit Tests")
class StringUtilTest {

  private StringUtil stringUtil;

  @BeforeEach
  void setUp() {
    stringUtil = new StringUtil();
  }

  @Test
  @DisplayName("isNullOrEmpty - null or empty should be true, others false")
  void testIsNullOrEmpty() {
    assertTrue(stringUtil.isNullOrEmpty(null));
    assertTrue(stringUtil.isNullOrEmpty(""));
    assertFalse(stringUtil.isNullOrEmpty("test"));
  }

  @Test
  @DisplayName("sanitizeInput - basic dangerous chars removal and trim")
  void testSanitizeInput_Basic() {
    assertNull(stringUtil.sanitizeInput(null));

    // trim
    assertEquals("test", stringUtil.sanitizeInput("  test  "));

    // rimozione di caratteri tipici injection
    assertEquals("DROP TABLE users", stringUtil.sanitizeInput("DROP TABLE users;"));
    assertEquals("ORMalicious", stringUtil.sanitizeInput("OR'Malicious'"));
  }

  @Test
  @DisplayName("validateAgainstSqlInjection - should detect main SQL keywords")
  void testValidateAgainstSqlInjection_DetectsKeywords() {
    assertThrows(
        SqlInjectionException.class,
        () -> stringUtil.validateAgainstSqlInjection("DROP TABLE users"));
    assertThrows(
        SqlInjectionException.class,
        () -> stringUtil.validateAgainstSqlInjection("DELETE FROM users"));
    assertThrows(
        SqlInjectionException.class,
        () -> stringUtil.validateAgainstSqlInjection("SELECT * FROM users"));
  }

  @Test
  @DisplayName("validateAgainstSqlInjection - should not throw for clean input")
  void testValidateAgainstSqlInjection_Clean() {
    assertDoesNotThrow(
        () ->
            stringUtil.validateAgainstSqlInjection(
                "John", "Doe", "john.doe@example.com", "+39 320 1234567"));
  }
}
