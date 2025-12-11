package it.sara.demo.web.user;

import it.sara.demo.exception.GenericException;
import it.sara.demo.service.user.UserService;
import it.sara.demo.service.user.criteria.CriteriaAddUser;
import it.sara.demo.service.user.criteria.CriteriaGetUsers;
import it.sara.demo.service.user.result.AddUserResult;
import it.sara.demo.service.user.result.GetUsersResult;
import it.sara.demo.web.assembler.AddUserAssembler;
import it.sara.demo.web.assembler.GetUsersAssembler;
import it.sara.demo.web.user.request.AddUserRequest;
import it.sara.demo.web.user.request.GetUsersRequest;
import it.sara.demo.web.user.response.AddUserResponse;
import it.sara.demo.web.user.response.GetUsersResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for user-related operations.
 *
 * <p>This controller handles HTTP requests for user management, including creation and retrieval of
 * users. All responses follow the standard {@code StatusDTO} format with HTTP 200 status codes.
 *
 * <p><strong>Security:</strong> All endpoints require ADMIN role (configured in SecurityConfig).
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final AddUserAssembler addUserAssembler;
  private final GetUsersAssembler getUsersAssembler;

  /**
   * Constructor-based dependency injection (best practice).
   *
   * @param userService Service layer for user operations
   * @param addUserAssembler Assembler to convert between web and service layer DTOs
   * @param getUsersAssembler Assembler for user search operations
   */
  public UserController(
      UserService userService,
      AddUserAssembler addUserAssembler,
      GetUsersAssembler getUsersAssembler) {
    this.userService = userService;
    this.addUserAssembler = addUserAssembler;
    this.getUsersAssembler = getUsersAssembler;
  }

  /**
   * Creates a new user in the system.
   *
   * <p>This endpoint validates the input using Bean Validation annotations and applies multiple
   * layers of security checks including SQL injection prevention and input sanitization.
   *
   * <p><strong>Security Measures:</strong>
   *
   * <ul>
   *   <li>Bean Validation with {@code @Valid}
   *   <li>Email format validation with custom regex
   *   <li>Italian phone number validation (mobile only)
   *   <li>SQL injection pattern detection
   *   <li>Input sanitization (removal of dangerous characters)
   * </ul>
   *
   * @param request The user creation request containing validated user data
   * @return ResponseEntity with AddUserResponse containing the created user data (HTTP 200)
   * @throws GenericException if validation fails or SQL injection is detected
   */
  @PostMapping("/v1/user")
  public ResponseEntity<AddUserResponse> addUser(@Valid @RequestBody AddUserRequest request)
      throws GenericException {
    CriteriaAddUser criteria = addUserAssembler.toCriteria(request);
    AddUserResult result = userService.addUser(criteria);
    AddUserResponse response = addUserAssembler.toResponse(result);

    return ResponseEntity.ok(response);
  }

  /**
   * Retrieves a paginated and filtered list of users.
   *
   * <p>This endpoint supports:
   *
   * <ul>
   *   <li>Case-insensitive search filtering by name or email
   *   <li>Pagination with offset and limit
   *   <li>Sorting by various fields (firstName, lastName) in ASC/DESC order
   * </ul>
   *
   * <p><strong>Note:</strong> Uses POST method to allow complex query parameters in the request
   * body, as per application design.
   *
   * @param request The search request containing filter, pagination, and sorting criteria
   * @return ResponseEntity with GetUsersResponse containing paginated user list (HTTP 200)
   * @throws GenericException if an error occurs during user retrieval
   */
  @PostMapping("/v1/users/search")
  public ResponseEntity<GetUsersResponse> getUsers(@Valid @RequestBody GetUsersRequest request)
      throws GenericException {
    CriteriaGetUsers criteria = getUsersAssembler.toCriteria(request);
    GetUsersResult result = userService.getUsers(criteria);
    GetUsersResponse response = getUsersAssembler.toResponse(result);

    return ResponseEntity.ok(response);
  }
}
