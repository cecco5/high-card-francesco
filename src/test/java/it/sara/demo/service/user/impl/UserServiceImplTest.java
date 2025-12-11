package it.sara.demo.service.user.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import it.sara.demo.dto.UserDTO;
import it.sara.demo.exception.GenericException;
import it.sara.demo.exception.SqlInjectionException;
import it.sara.demo.service.assembler.UserAssembler;
import it.sara.demo.service.database.UserRepository;
import it.sara.demo.service.database.model.User;
import it.sara.demo.service.user.criteria.CriteriaAddUser;
import it.sara.demo.service.user.criteria.CriteriaGetUsers;
import it.sara.demo.service.user.result.AddUserResult;
import it.sara.demo.service.user.result.GetUsersResult;
import it.sara.demo.service.util.StringUtil;
import it.sara.demo.service.util.UserFilterUtil;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link UserServiceImpl}.
 *
 * <p>Tests cover:
 *
 * <ul>
 *   <li>User creation with validation and sanitization
 *   <li>SQL injection prevention
 *   <li>User search with pagination, filtering, and sorting
 *   <li>Exception handling
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Unit Tests")
class UserServiceImplTest {

  @Mock private UserRepository userRepository;

  @Mock private StringUtil stringUtil;

  @Mock private UserAssembler userAssembler;

  @Mock private UserFilterUtil userFilterUtil;

  @InjectMocks private UserServiceImpl userService;

  private User testUser;
  private UserDTO testUserDTO;
  private CriteriaAddUser criteriaAddUser;

  @BeforeEach
  void setUp() {
    // Setup test data
    testUser = new User();
    testUser.setGuid("test-guid-123");
    testUser.setFirstName("John");
    testUser.setLastName("Doe");
    testUser.setEmail("john.doe@example.com");
    testUser.setPhoneNumber("+39 320 1234567");

    testUserDTO = new UserDTO();
    testUserDTO.setGuid("test-guid-123");
    testUserDTO.setFirstName("John");
    testUserDTO.setLastName("Doe");
    testUserDTO.setEmail("john.doe@example.com");
    testUserDTO.setPhoneNumber("+39 320 1234567");

    criteriaAddUser = new CriteriaAddUser();
    criteriaAddUser.setFirstName("John");
    criteriaAddUser.setLastName("Doe");
    criteriaAddUser.setEmail("john.doe@example.com");
    criteriaAddUser.setPhoneNumber("+39 320 1234567");
  }

  // ==================== ADD USER TESTS ====================

  @Test
  @DisplayName("addUser - Should successfully add a valid user")
  void testAddUser_Success() throws GenericException {
    // Arrange
    doNothing()
        .when(stringUtil)
        .validateAgainstSqlInjection(anyString(), anyString(), anyString(), anyString());
    when(stringUtil.sanitizeInput(anyString())).thenAnswer(i -> i.getArgument(0));
    when(userRepository.save(any(User.class))).thenReturn(true);

    // Act
    AddUserResult result = userService.addUser(criteriaAddUser);

    // Assert
    assertNotNull(result);
    assertNotNull(result.getUser());
    assertEquals("John", result.getUser().getFirstName());
    assertEquals("Doe", result.getUser().getLastName());
    assertEquals("john.doe@example.com", result.getUser().getEmail());
    assertEquals("+39 320 1234567", result.getUser().getPhoneNumber());

    // Verify interactions
    verify(stringUtil, times(1))
        .validateAgainstSqlInjection("John", "Doe", "john.doe@example.com", "+39 320 1234567");
    verify(stringUtil, times(4)).sanitizeInput(anyString());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  @DisplayName("addUser - Should throw SqlInjectionException for SQL keywords")
  void testAddUser_SqlInjectionDetected() throws SqlInjectionException {
    // Arrange
    criteriaAddUser.setFirstName("DROP TABLE users");

    doThrow(new SqlInjectionException("Invalid input: SQL keywords detected"))
        .when(stringUtil)
        .validateAgainstSqlInjection(anyString(), anyString(), anyString(), anyString());

    // Act & Assert
    SqlInjectionException exception =
        assertThrows(SqlInjectionException.class, () -> userService.addUser(criteriaAddUser));

    assertEquals("Invalid input: SQL keywords detected", exception.getStatus().getMessage());

    // Verify no save was attempted
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("addUser - Should throw GenericException when save fails")
  void testAddUser_SaveFails() throws GenericException {
    // Arrange
    doNothing()
        .when(stringUtil)
        .validateAgainstSqlInjection(anyString(), anyString(), anyString(), anyString());
    when(stringUtil.sanitizeInput(anyString())).thenAnswer(i -> i.getArgument(0));
    when(userRepository.save(any(User.class))).thenReturn(false);

    // Act & Assert
    GenericException exception =
        assertThrows(GenericException.class, () -> userService.addUser(criteriaAddUser));

    assertEquals(500, exception.getStatus().getCode());
    assertEquals("Error saving user", exception.getStatus().getMessage());

    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  @DisplayName("addUser - Should sanitize all input fields")
  void testAddUser_SanitizesInput() throws GenericException {
    // Arrange
    criteriaAddUser.setFirstName("John<script>");
    criteriaAddUser.setLastName("Doe';DROP TABLE");
    criteriaAddUser.setEmail("test@example.com\"");
    criteriaAddUser.setPhoneNumber("+39 320 1234567;");

    doNothing()
        .when(stringUtil)
        .validateAgainstSqlInjection(anyString(), anyString(), anyString(), anyString());
    when(stringUtil.sanitizeInput(anyString()))
        .thenReturn("John", "Doe", "test@example.com", "+39 320 1234567");
    when(userRepository.save(any(User.class))).thenReturn(true);

    // Act
    AddUserResult result = userService.addUser(criteriaAddUser);

    // Assert
    assertNotNull(result);
    verify(stringUtil, times(4)).sanitizeInput(anyString());
  }

  // ==================== GET USERS TESTS ====================

  @Test
  @DisplayName("getUsers - Should return paginated and sorted users")
  void testGetUsers_Success() throws GenericException {
    // Arrange
    User user1 = new User();
    user1.setGuid("guid-1");
    user1.setFirstName("Alice");
    user1.setLastName("Smith");
    user1.setEmail("alice@example.com");
    user1.setPhoneNumber("+39 320 1111111");

    User user2 = new User();
    user2.setGuid("guid-2");
    user2.setFirstName("Bob");
    user2.setLastName("Johnson");
    user2.setEmail("bob@example.com");
    user2.setPhoneNumber("+39 320 2222222");

    List<User> allUsers = Arrays.asList(user1, user2);

    CriteriaGetUsers criteria = new CriteriaGetUsers();
    criteria.setQuery("alice");
    criteria.setOffset(0);
    criteria.setLimit(10);
    criteria.setOrder(CriteriaGetUsers.OrderType.BY_FIRSTNAME);

    when(userRepository.getAll()).thenReturn(allUsers);
    when(userFilterUtil.matchesSearchQuery(user1, "alice")).thenReturn(true);
    when(userFilterUtil.matchesSearchQuery(user2, "alice")).thenReturn(false);
    when(userFilterUtil.applySorting(anyList(), any())).thenAnswer(i -> i.getArgument(0));
    when(userAssembler.toDTO(any(User.class))).thenReturn(testUserDTO);

    // Act
    GetUsersResult result = userService.getUsers(criteria);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getTotal());
    assertEquals(1, result.getUsers().size());

    verify(userRepository, times(1)).getAll();
    verify(userFilterUtil, times(2)).matchesSearchQuery(any(User.class), eq("alice"));
    verify(userFilterUtil, times(1))
        .applySorting(anyList(), eq(CriteriaGetUsers.OrderType.BY_FIRSTNAME));
    verify(userAssembler, times(1)).toDTO(any(User.class));
  }

  @Test
  @DisplayName("getUsers - Should return all users when query is null")
  void testGetUsers_NoQuery() throws GenericException {
    // Arrange
    List<User> allUsers = Arrays.asList(testUser, testUser);

    CriteriaGetUsers criteria = new CriteriaGetUsers();
    criteria.setQuery(null);
    criteria.setOffset(0);
    criteria.setLimit(10);
    criteria.setOrder(CriteriaGetUsers.OrderType.BY_LASTNAME);

    when(userRepository.getAll()).thenReturn(allUsers);
    when(userFilterUtil.matchesSearchQuery(any(User.class), isNull())).thenReturn(true);
    when(userFilterUtil.applySorting(anyList(), any())).thenAnswer(i -> i.getArgument(0));
    when(userAssembler.toDTO(any(User.class))).thenReturn(testUserDTO);

    // Act
    GetUsersResult result = userService.getUsers(criteria);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.getTotal());
    assertEquals(2, result.getUsers().size());

    verify(userRepository, times(1)).getAll();
  }

  @Test
  @DisplayName("getUsers - Should apply pagination correctly")
  void testGetUsers_Pagination() throws GenericException {
    // Arrange
    List<User> manyUsers =
        Arrays.asList(testUser, testUser, testUser, testUser, testUser); // 5 users

    CriteriaGetUsers criteria = new CriteriaGetUsers();
    criteria.setQuery(null);
    criteria.setOffset(2); // Skip first 2
    criteria.setLimit(2); // Take 2
    criteria.setOrder(CriteriaGetUsers.OrderType.BY_FIRSTNAME);

    when(userRepository.getAll()).thenReturn(manyUsers);
    when(userFilterUtil.matchesSearchQuery(any(User.class), isNull())).thenReturn(true);
    when(userFilterUtil.applySorting(anyList(), any())).thenAnswer(i -> i.getArgument(0));
    when(userAssembler.toDTO(any(User.class))).thenReturn(testUserDTO);

    // Act
    GetUsersResult result = userService.getUsers(criteria);

    // Assert
    assertNotNull(result);
    assertEquals(5, result.getTotal()); // Total before pagination
    assertEquals(2, result.getUsers().size()); // Limited to 2

    verify(userAssembler, times(2)).toDTO(any(User.class)); // Only 2 converted
  }

  @Test
  @DisplayName("getUsers - Should handle empty results")
  void testGetUsers_EmptyResult() throws GenericException {
    // Arrange
    CriteriaGetUsers criteria = new CriteriaGetUsers();
    criteria.setQuery("nonexistent");
    criteria.setOffset(0);
    criteria.setLimit(10);
    criteria.setOrder(CriteriaGetUsers.OrderType.BY_FIRSTNAME);

    when(userRepository.getAll()).thenReturn(Arrays.asList(testUser));
    when(userFilterUtil.matchesSearchQuery(any(User.class), eq("nonexistent"))).thenReturn(false);

    // Act
    GetUsersResult result = userService.getUsers(criteria);

    // Assert
    assertNotNull(result);
    assertEquals(0, result.getTotal());
    assertTrue(result.getUsers().isEmpty());

    verify(userAssembler, never()).toDTO(any(User.class));
  }

  @Test
  @DisplayName("getUsers - Should handle exceptions")
  void testGetUsers_Exception() {
    // Arrange
    CriteriaGetUsers criteria = new CriteriaGetUsers();
    criteria.setQuery(null);
    criteria.setOffset(0);
    criteria.setLimit(10);
    criteria.setOrder(CriteriaGetUsers.OrderType.BY_FIRSTNAME);

    when(userRepository.getAll()).thenThrow(new RuntimeException("Database error"));

    // Act & Assert
    GenericException exception =
        assertThrows(GenericException.class, () -> userService.getUsers(criteria));

    assertEquals(500, exception.getStatus().getCode());
  }
}
