package it.sara.demo.service.database;

import it.sara.demo.service.database.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UserRepository}.
 *
 * <p>Tests cover:
 * <ul>
 *   <li>Saving new users with GUID generation</li>
 *   <li>Retrieving users by GUID</li>
 *   <li>Retrieving all users</li>
 *   <li>Interaction with FakeDatabase</li>
 * </ul>
 */
@DisplayName("UserRepository Unit Tests")
class UserRepositoryTest {

  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository = new UserRepository();
    // Store initial count of users in FakeDatabase
    int initialUserCount = FakeDatabase.TABLE_USER.size();
  }

  // ==================== SAVE TESTS ====================

  @Test
  @DisplayName("save - Should save user and generate GUID")
  void testSave_GeneratesGuid() {
    // Arrange
    User user = new User();
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setEmail("john.doe@example.com");
    user.setPhoneNumber("+39 320 1234567");

    // Act
    boolean result = userRepository.save(user);

    // Assert
    assertTrue(result);
    assertNotNull(user.getGuid());
    assertFalse(user.getGuid().isEmpty());

    // Verify GUID format (UUID)
    assertTrue(user.getGuid().matches("[0-9a-f-]{36}"));
  }

  @Test
  @DisplayName("save - Should add user to FakeDatabase")
  void testSave_AddsToDatabase() {
    // Arrange
    User user = new User();
    user.setFirstName("Jane");
    user.setLastName("Smith");
    user.setEmail("jane.smith@example.com");
    user.setPhoneNumber("+39 320 9876543");

    int beforeSize = FakeDatabase.TABLE_USER.size();

    // Act
    userRepository.save(user);

    // Assert
    assertEquals(beforeSize + 1, FakeDatabase.TABLE_USER.size());
    assertTrue(FakeDatabase.TABLE_USER.contains(user));
  }

  @Test
  @DisplayName("save - Should generate unique GUIDs for multiple users")
  void testSave_UniqueGuids() {
    // Arrange
    User user1 = new User();
    user1.setFirstName("User1");
    user1.setLastName("Test");
    user1.setEmail("user1@example.com");
    user1.setPhoneNumber("+39 320 1111111");

    User user2 = new User();
    user2.setFirstName("User2");
    user2.setLastName("Test");
    user2.setEmail("user2@example.com");
    user2.setPhoneNumber("+39 320 2222222");

    // Act
    userRepository.save(user1);
    userRepository.save(user2);

    // Assert
    assertNotNull(user1.getGuid());
    assertNotNull(user2.getGuid());
    assertNotEquals(user1.getGuid(), user2.getGuid());
  }

  @Test
  @DisplayName("save - Should preserve all user fields")
  void testSave_PreservesFields() {
    // Arrange
    User user = new User();
    user.setFirstName("Alice");
    user.setLastName("Johnson");
    user.setEmail("alice.johnson@example.com");
    user.setPhoneNumber("+39 320 5555555");

    // Act
    userRepository.save(user);

    // Assert
    assertEquals("Alice", user.getFirstName());
    assertEquals("Johnson", user.getLastName());
    assertEquals("alice.johnson@example.com", user.getEmail());
    assertEquals("+39 320 5555555", user.getPhoneNumber());
  }

  @Test
  @DisplayName("save - Should handle user with null fields")
  void testSave_NullFields() {
    // Arrange
    User user = new User();
    // Only set firstName, rest are null
    user.setFirstName("Partial");

    // Act
    boolean result = userRepository.save(user);

    // Assert
    assertTrue(result);
    assertNotNull(user.getGuid());
    assertEquals("Partial", user.getFirstName());
    assertNull(user.getLastName());
    assertNull(user.getEmail());
    assertNull(user.getPhoneNumber());
  }

  // ==================== GET BY GUID TESTS ====================

  @Test
  @DisplayName("getByGuid - Should find user by existing GUID")
  void testGetByGuid_ExistingUser() {
    // Arrange
    User user = new User();
    user.setFirstName("Find");
    user.setLastName("Me");
    user.setEmail("find.me@example.com");
    user.setPhoneNumber("+39 320 7777777");
    userRepository.save(user);
    String guid = user.getGuid();

    // Act
    Optional<User> result = userRepository.getByGuid(guid);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(guid, result.get().getGuid());
    assertEquals("Find", result.get().getFirstName());
    assertEquals("Me", result.get().getLastName());
  }

  @Test
  @DisplayName("getByGuid - Should return empty for non-existing GUID")
  void testGetByGuid_NonExistingUser() {
    // Arrange
    String nonExistingGuid = "00000000-0000-0000-0000-000000000000";

    // Act
    Optional<User> result = userRepository.getByGuid(nonExistingGuid);

    // Assert
    assertFalse(result.isPresent());
  }

  @Test
  @DisplayName("getByGuid - Should return empty for null GUID")
  void testGetByGuid_NullGuid() {
    // Act
    Optional<User> result = userRepository.getByGuid(null);

    // Assert
    assertFalse(result.isPresent());
  }

  @Test
  @DisplayName("getByGuid - Should find correct user among multiple")
  void testGetByGuid_MultipleUsers() {
    // Arrange
    User user1 = new User();
    user1.setFirstName("User1");
    user1.setEmail("user1@test.com");
    userRepository.save(user1);

    User user2 = new User();
    user2.setFirstName("User2");
    user2.setEmail("user2@test.com");
    userRepository.save(user2);

    User user3 = new User();
    user3.setFirstName("User3");
    user3.setEmail("user3@test.com");
    userRepository.save(user3);

    // Act
    Optional<User> result = userRepository.getByGuid(user2.getGuid());

    // Assert
    assertTrue(result.isPresent());
    assertEquals(user2.getGuid(), result.get().getGuid());
    assertEquals("User2", result.get().getFirstName());
    assertEquals("user2@test.com", result.get().getEmail());
  }

  // ==================== GET ALL TESTS ====================

  @Test
  @DisplayName("getAll - Should return all users from FakeDatabase")
  void testGetAll_ReturnsAllUsers() {
    // Act
    List<User> users = userRepository.getAll();

    // Assert
    assertNotNull(users);
    assertEquals(FakeDatabase.TABLE_USER.size(), users.size());

    // Verify it's the same list reference (not a copy)
    assertSame(FakeDatabase.TABLE_USER, users);
  }

  @Test
  @DisplayName("getAll - Should include initially seeded users")
  void testGetAll_IncludesInitialUsers() {
    // Act
    List<User> users = userRepository.getAll();

    // Assert
    assertTrue(users.size() >= 10); // FakeDatabase seeds 10 users initially

    // Verify some expected patterns from FakeDatabase initialization
    boolean hasUser0 = users.stream()
        .anyMatch(u -> u.getEmail() != null && u.getEmail().equals("user0@example.com"));
    assertTrue(hasUser0);
  }

  @Test
  @DisplayName("getAll - Should reflect newly added users")
  void testGetAll_ReflectsNewUsers() {
    // Arrange
    int beforeSize = userRepository.getAll().size();

    User newUser = new User();
    newUser.setFirstName("New");
    newUser.setLastName("User");
    newUser.setEmail("new.user@example.com");
    newUser.setPhoneNumber("+39 320 9999999");

    // Act
    userRepository.save(newUser);
    List<User> usersAfter = userRepository.getAll();

    // Assert
    assertEquals(beforeSize + 1, usersAfter.size());
    assertTrue(usersAfter.contains(newUser));
  }

  @Test
  @DisplayName("getAll - Should return mutable list")
  void testGetAll_MutableList() {
    // Act
    List<User> users = userRepository.getAll();

    // Assert - verify list is mutable (since it's FakeDatabase.TABLE_USER)
    assertNotNull(users);
    // This should not throw UnsupportedOperationException
    assertDoesNotThrow(() -> {
      int originalSize = users.size();
      User tempUser = new User();
      tempUser.setGuid("temp-guid");
      users.add(tempUser);
      users.remove(tempUser);
      assertEquals(originalSize, users.size());
    });
  }

  // ==================== INTEGRATION TESTS ====================

  @Test
  @DisplayName("Integration - Save and retrieve user by GUID")
  void testIntegration_SaveAndRetrieve() {
    // Arrange
    User user = new User();
    user.setFirstName("Integration");
    user.setLastName("Test");
    user.setEmail("integration@test.com");
    user.setPhoneNumber("+39 320 1010101");

    // Act
    userRepository.save(user);
    String guid = user.getGuid();
    Optional<User> retrieved = userRepository.getByGuid(guid);

    // Assert
    assertTrue(retrieved.isPresent());
    assertEquals(guid, retrieved.get().getGuid());
    assertEquals("Integration", retrieved.get().getFirstName());
    assertEquals("Test", retrieved.get().getLastName());
    assertEquals("integration@test.com", retrieved.get().getEmail());
    assertEquals("+39 320 1010101", retrieved.get().getPhoneNumber());
  }

  @Test
  @DisplayName("Integration - Save multiple users and retrieve all")
  void testIntegration_SaveMultipleAndGetAll() {
    // Arrange
    int beforeCount = userRepository.getAll().size();

    User user1 = new User();
    user1.setFirstName("Multi1");
    user1.setEmail("multi1@test.com");

    User user2 = new User();
    user2.setFirstName("Multi2");
    user2.setEmail("multi2@test.com");

    User user3 = new User();
    user3.setFirstName("Multi3");
    user3.setEmail("multi3@test.com");

    // Act
    userRepository.save(user1);
    userRepository.save(user2);
    userRepository.save(user3);
    List<User> allUsers = userRepository.getAll();

    // Assert
    assertEquals(beforeCount + 3, allUsers.size());
    assertTrue(allUsers.contains(user1));
    assertTrue(allUsers.contains(user2));
    assertTrue(allUsers.contains(user3));
  }
}

