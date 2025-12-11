package it.sara.demo.web.user;

import it.sara.demo.dto.StatusDTO;
import it.sara.demo.dto.UserDTO;
import it.sara.demo.exception.GenericException;
import it.sara.demo.service.database.model.User;
import it.sara.demo.service.user.UserService;
import it.sara.demo.service.user.criteria.CriteriaAddUser;
import it.sara.demo.service.user.criteria.CriteriaGetUsers;
import it.sara.demo.service.user.result.AddUserResult;
import it.sara.demo.service.user.result.GetUsersResult;
import it.sara.demo.web.assembler.GetUsersAssembler;
import it.sara.demo.web.user.request.AddUserRequest;
import it.sara.demo.web.user.request.GetUsersRequest;
import it.sara.demo.web.user.response.AddUserResponse;
import it.sara.demo.web.user.response.GetUsersResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserController}.
 *
 * <p>These tests focus on the web layer responsibilities:
 * <ul>
 *   <li>Mapping between web requests and service criteria</li>
 *   <li>Delegation to {@link UserService}</li>
 *   <li>Mapping between service results and web responses via assemblers</li>
 *   <li>Ensuring HTTP 200 status for successful calls</li>
 * </ul>
 *
 * <p>Business rules and exception behavior are tested at service level
 * (see {@code UserServiceImplTest}).</p>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("UserController Unit Tests")
class UserControllerTest {

  @Mock private UserService userService;

  @Mock private it.sara.demo.web.assembler.AddUserAssembler addUserAssembler;

  @Mock private GetUsersAssembler getUsersAssembler;

  @InjectMocks private UserController userController;

  private AddUserRequest addUserRequest;
  private GetUsersRequest getUsersRequest;
  private User testUser;
  private UserDTO testUserDTO;

  @BeforeEach
  void setUp() {
    // Setup AddUserRequest
    addUserRequest = new AddUserRequest();
    addUserRequest.setFirstName("John");
    addUserRequest.setLastName("Doe");
    addUserRequest.setEmail("john.doe@example.com");
    addUserRequest.setPhoneNumber("+39 320 1234567");

    // Setup GetUsersRequest
    getUsersRequest = new GetUsersRequest();
    getUsersRequest.setQuery("john");
    getUsersRequest.setOffset(0);
    getUsersRequest.setLimit(10);
    getUsersRequest.setOrder(CriteriaGetUsers.OrderType.BY_FIRSTNAME);

    // Setup test User
    testUser = new User();
    testUser.setGuid("test-guid-123");
    testUser.setFirstName("John");
    testUser.setLastName("Doe");
    testUser.setEmail("john.doe@example.com");
    testUser.setPhoneNumber("+39 320 1234567");

    // Setup test UserDTO
    testUserDTO = new UserDTO();
    testUserDTO.setGuid("test-guid-123");
    testUserDTO.setFirstName("John");
    testUserDTO.setLastName("Doe");
    testUserDTO.setEmail("john.doe@example.com");
    testUserDTO.setPhoneNumber("+39 320 1234567");
  }

  // Helper method to setup AddUserAssembler mocks
  private void setupAddUserAssemblerMocks(AddUserResult addUserResult) {
    when(addUserAssembler.toCriteria(any(AddUserRequest.class))).thenAnswer(invocation -> {
      AddUserRequest req = invocation.getArgument(0);
      CriteriaAddUser criteria = new CriteriaAddUser();
      criteria.setFirstName(req.getFirstName());
      criteria.setLastName(req.getLastName());
      criteria.setEmail(req.getEmail());
      criteria.setPhoneNumber(req.getPhoneNumber());
      return criteria;
    });

    when(addUserAssembler.toResponse(addUserResult)).thenAnswer(invocation -> {
      AddUserResponse response = new AddUserResponse();

      StatusDTO status = new StatusDTO();
      status.setCode(200);
      status.setMessage("User added.");
      status.setTraceId(java.util.UUID.randomUUID().toString());
      response.setStatus(status);

      if (addUserResult.getUser() != null) {
        UserDTO dto = new UserDTO();
        dto.setGuid(addUserResult.getUser().getGuid());
        dto.setFirstName(addUserResult.getUser().getFirstName());
        dto.setLastName(addUserResult.getUser().getLastName());
        dto.setEmail(addUserResult.getUser().getEmail());
        dto.setPhoneNumber(addUserResult.getUser().getPhoneNumber());
        response.setUser(dto);
      }
      return response;
    });
  }

  // Helper method to setup GetUsersAssembler mocks
  private void setupGetUsersAssemblerMocks(GetUsersResult getUsersResult) {
    when(getUsersAssembler.toCriteria(any(GetUsersRequest.class))).thenAnswer(invocation -> {
      GetUsersRequest req = invocation.getArgument(0);
      CriteriaGetUsers criteria = new CriteriaGetUsers();
      criteria.setQuery(req.getQuery());
      criteria.setOffset(req.getOffset());
      criteria.setLimit(req.getLimit());
      criteria.setOrder(req.getOrder() != null ? req.getOrder() : CriteriaGetUsers.OrderType.BY_FIRSTNAME);
      return criteria;
    });

    when(getUsersAssembler.toResponse(getUsersResult)).thenAnswer(invocation -> {
      GetUsersResponse response = new GetUsersResponse();
      response.setUsers(getUsersResult.getUsers());
      response.setTotal(getUsersResult.getTotal());

      StatusDTO status = new StatusDTO();
      status.setCode(200);
      status.setMessage("Users retrieved successfully.");
      status.setTraceId(java.util.UUID.randomUUID().toString());
      response.setStatus(status);

      return response;
    });
  }

  // ==================== ADD USER TESTS ====================

  @Test
  @DisplayName("addUser - Should successfully create user and return 200 with mapped DTO")
  void testAddUser_Success() throws GenericException {
    // Arrange
    AddUserResult addUserResult = new AddUserResult();
    addUserResult.setUser(testUser);
    setupAddUserAssemblerMocks(addUserResult);

    when(userService.addUser(any(CriteriaAddUser.class))).thenReturn(addUserResult);

    // Act
    ResponseEntity<AddUserResponse> response = userController.addUser(addUserRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());

    AddUserResponse body = response.getBody();
    assertNotNull(body.getStatus());
    assertEquals(200, body.getStatus().getCode());
    assertEquals("User added.", body.getStatus().getMessage());
    assertNotNull(body.getStatus().getTraceId());

    assertNotNull(body.getUser());
    assertEquals("test-guid-123", body.getUser().getGuid());
    assertEquals("John", body.getUser().getFirstName());
    assertEquals("Doe", body.getUser().getLastName());

    verify(addUserAssembler, times(1)).toCriteria(addUserRequest);
    verify(userService, times(1)).addUser(any(CriteriaAddUser.class));
    verify(addUserAssembler, times(1)).toResponse(addUserResult);
  }

  @Test
  @DisplayName("addUser - Should map AddUserRequest to CriteriaAddUser")
  void testAddUser_MapsRequestToCriteria() throws GenericException {
    // Arrange
    AddUserResult addUserResult = new AddUserResult();
    addUserResult.setUser(testUser);
    setupAddUserAssemblerMocks(addUserResult);

    when(userService.addUser(any(CriteriaAddUser.class))).thenReturn(addUserResult);

    // Act
    userController.addUser(addUserRequest);

    // Assert - verify that assembler is used to build criteria
    verify(addUserAssembler, times(1)).toCriteria(addUserRequest);
    verify(userService, times(1)).addUser(any(CriteriaAddUser.class));
  }

  // ==================== GET USERS TESTS ====================

  @Test
  @DisplayName("getUsers - Should successfully retrieve users and return 200")
  void testGetUsers_Success() throws GenericException {
    // Arrange
    UserDTO userDTO1 = new UserDTO();
    userDTO1.setGuid("guid-1");
    userDTO1.setFirstName("Alice");
    userDTO1.setLastName("Smith");

    UserDTO userDTO2 = new UserDTO();
    userDTO2.setGuid("guid-2");
    userDTO2.setFirstName("Bob");
    userDTO2.setLastName("Johnson");

    GetUsersResult getUsersResult = new GetUsersResult();
    getUsersResult.setUsers(Arrays.asList(userDTO1, userDTO2));
    getUsersResult.setTotal(2);
    setupGetUsersAssemblerMocks(getUsersResult);

    when(userService.getUsers(any(CriteriaGetUsers.class))).thenReturn(getUsersResult);

    // Act
    ResponseEntity<GetUsersResponse> response = userController.getUsers(getUsersRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());

    GetUsersResponse body = response.getBody();
    assertEquals(2, body.getUsers().size());
    assertEquals(2, body.getTotal());

    assertEquals("Alice", body.getUsers().get(0).getFirstName());
    assertEquals("Bob", body.getUsers().get(1).getFirstName());

    verify(getUsersAssembler, times(1)).toCriteria(getUsersRequest);
    verify(userService, times(1)).getUsers(any(CriteriaGetUsers.class));
    verify(getUsersAssembler, times(1)).toResponse(getUsersResult);
  }

  @Test
  @DisplayName("getUsers - Should map GetUsersRequest to CriteriaGetUsers via assembler")
  void testGetUsers_UsesAssembler() throws GenericException {
    // Arrange
    GetUsersResult getUsersResult = new GetUsersResult();
    getUsersResult.setUsers(Arrays.asList());
    getUsersResult.setTotal(0);
    setupGetUsersAssemblerMocks(getUsersResult);

    when(userService.getUsers(any(CriteriaGetUsers.class))).thenReturn(getUsersResult);

    // Act
    userController.getUsers(getUsersRequest);

    // Assert
    verify(getUsersAssembler, times(1)).toCriteria(getUsersRequest);
    verify(userService, times(1)).getUsers(any(CriteriaGetUsers.class));
    verify(getUsersAssembler, times(1)).toResponse(getUsersResult);
  }

  @Test
  @DisplayName("getUsers - Should return empty list when no users match")
  void testGetUsers_EmptyResult() throws GenericException {
    // Arrange
    GetUsersResult getUsersResult = new GetUsersResult();
    getUsersResult.setUsers(Arrays.asList());
    getUsersResult.setTotal(0);
    setupGetUsersAssemblerMocks(getUsersResult);

    when(userService.getUsers(any(CriteriaGetUsers.class))).thenReturn(getUsersResult);

    // Act
    ResponseEntity<GetUsersResponse> response = userController.getUsers(getUsersRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());

    assertTrue(response.getBody().getUsers().isEmpty());
    assertEquals(0, response.getBody().getTotal());
  }

  @Test
  @DisplayName("getUsers - Should handle pagination metadata correctly")
  void testGetUsers_Pagination() throws GenericException {
    // Arrange
    List<UserDTO> users = Arrays.asList(testUserDTO);

    GetUsersResult getUsersResult = new GetUsersResult();
    getUsersResult.setUsers(users);
    getUsersResult.setTotal(100); // Total 100, but only 1 in current page
    setupGetUsersAssemblerMocks(getUsersResult);

    when(userService.getUsers(any(CriteriaGetUsers.class))).thenReturn(getUsersResult);

    // Act
    ResponseEntity<GetUsersResponse> response = userController.getUsers(getUsersRequest);

    // Assert
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().getUsers().size()); // Current page size
    assertEquals(100, response.getBody().getTotal()); // Total count
  }

  @Test
  @DisplayName("getUsers - Should pass sort order from request to criteria")
  void testGetUsers_SortOrders() throws GenericException {
    // Arrange
    getUsersRequest.setOrder(CriteriaGetUsers.OrderType.BY_LASTNAME_DESC);

    GetUsersResult getUsersResult = new GetUsersResult();
    getUsersResult.setUsers(Arrays.asList());
    getUsersResult.setTotal(0);
    setupGetUsersAssemblerMocks(getUsersResult);

    when(userService.getUsers(any(CriteriaGetUsers.class))).thenReturn(getUsersResult);

    // Act
    userController.getUsers(getUsersRequest);

    // Assert
    verify(getUsersAssembler, times(1)).toCriteria(getUsersRequest);
    verify(userService, times(1)).getUsers(any(CriteriaGetUsers.class));
  }
}
