package it.sara.demo.service.assembler;

import it.sara.demo.dto.UserDTO;
import it.sara.demo.service.database.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UserAssembler}.
 *
 * <p>Tests cover User entity to UserDTO conversion.</p>
 */
@DisplayName("UserAssembler Unit Tests")
class UserAssemblerTest {

  private UserAssembler userAssembler;

  @BeforeEach
  void setUp() {
    userAssembler = new UserAssembler();
  }

  @Test
  @DisplayName("toDTO - Should correctly map all User fields to UserDTO")
  void testToDTO_CompleteMapping() {
    // Arrange
    User user = new User();
    user.setGuid("test-guid-123");
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setEmail("john.doe@example.com");
    user.setPhoneNumber("+39 320 1234567");

    // Act
    UserDTO dto = userAssembler.toDTO(user);

    // Assert
    assertNotNull(dto);
    assertEquals("test-guid-123", dto.getGuid());
    assertEquals("John", dto.getFirstName());
    assertEquals("Doe", dto.getLastName());
    assertEquals("john.doe@example.com", dto.getEmail());
    assertEquals("+39 320 1234567", dto.getPhoneNumber());
  }

  @Test
  @DisplayName("toDTO - Should handle User with null fields")
  void testToDTO_NullFields() {
    // Arrange
    User user = new User();
    user.setGuid("guid-only");
    // All other fields are null

    // Act
    UserDTO dto = userAssembler.toDTO(user);

    // Assert
    assertNotNull(dto);
    assertEquals("guid-only", dto.getGuid());
    assertNull(dto.getFirstName());
    assertNull(dto.getLastName());
    assertNull(dto.getEmail());
    assertNull(dto.getPhoneNumber());
  }

  @Test
  @DisplayName("toDTO - Should handle User with empty strings")
  void testToDTO_EmptyStrings() {
    // Arrange
    User user = new User();
    user.setGuid("");
    user.setFirstName("");
    user.setLastName("");
    user.setEmail("");
    user.setPhoneNumber("");

    // Act
    UserDTO dto = userAssembler.toDTO(user);

    // Assert
    assertNotNull(dto);
    assertEquals("", dto.getGuid());
    assertEquals("", dto.getFirstName());
    assertEquals("", dto.getLastName());
    assertEquals("", dto.getEmail());
    assertEquals("", dto.getPhoneNumber());
  }

  @Test
  @DisplayName("toDTO - Should preserve special characters in fields")
  void testToDTO_SpecialCharacters() {
    // Arrange
    User user = new User();
    user.setGuid("guid-123-abc");
    user.setFirstName("María");
    user.setLastName("O'Connor");
    user.setEmail("maria.oconnor+test@example.com");
    user.setPhoneNumber("+39 320 123-4567");

    // Act
    UserDTO dto = userAssembler.toDTO(user);

    // Assert
    assertNotNull(dto);
    assertEquals("María", dto.getFirstName());
    assertEquals("O'Connor", dto.getLastName());
    assertEquals("maria.oconnor+test@example.com", dto.getEmail());
    assertEquals("+39 320 123-4567", dto.getPhoneNumber());
  }

  @Test
  @DisplayName("toDTO - Should create independent DTO instance")
  void testToDTO_Independence() {
    // Arrange
    User user = new User();
    user.setGuid("guid-123");
    user.setFirstName("Original");
    user.setLastName("Name");

    // Act
    UserDTO dto = userAssembler.toDTO(user);

    // Modify original user
    user.setFirstName("Modified");
    user.setLastName("Changed");

    // Assert - DTO should not be affected
    assertEquals("Original", dto.getFirstName());
    assertEquals("Name", dto.getLastName());
  }
}

