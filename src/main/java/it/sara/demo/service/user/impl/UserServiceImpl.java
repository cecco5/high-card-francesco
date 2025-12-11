package it.sara.demo.service.user.impl;

import it.sara.demo.dto.UserDTO;
import it.sara.demo.exception.GenericException;
import it.sara.demo.exception.SqlInjectionException;
import it.sara.demo.service.assembler.UserAssembler;
import it.sara.demo.service.database.UserRepository;
import it.sara.demo.service.database.model.User;
import it.sara.demo.service.user.UserService;
import it.sara.demo.service.user.criteria.CriteriaAddUser;
import it.sara.demo.service.user.criteria.CriteriaGetUsers;
import it.sara.demo.service.user.result.AddUserResult;
import it.sara.demo.service.user.result.GetUsersResult;
import it.sara.demo.service.util.StringUtil;
import it.sara.demo.service.util.UserFilterUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final StringUtil stringUtil;
  private final UserAssembler userAssembler;
  private final UserFilterUtil userFilterUtil;

  /**
   * Constructor-based dependency injection (best practice). Allows for immutable fields, easier
   * testing, and explicit dependencies.
   *
   * @param userRepository Repository for user data access
   * @param stringUtil Utility class for string operations and validation
   * @param userAssembler Assembler for User entity to DTO conversion
   * @param userFilterUtil Utility for filtering and sorting users
   */
  public UserServiceImpl(
      UserRepository userRepository,
      StringUtil stringUtil,
      UserAssembler userAssembler,
      UserFilterUtil userFilterUtil) {
    this.userRepository = userRepository;
    this.stringUtil = stringUtil;
    this.userAssembler = userAssembler;
    this.userFilterUtil = userFilterUtil;
  }

  /**
   * Adds a new user to the system with SQL injection prevention.
   *
   * <p><strong>Security Strategy - Defense in Depth:</strong>
   *
   * <ul>
   *   <li>Layer 1: Bean Validation via {@code @Valid} in Controller
   *   <li>Layer 2: Regex validation in {@link it.sara.demo.web.user.request.AddUserRequest}
   *   <li>Layer 3: Input sanitization in Service Layer (removes SQL dangerous characters)
   *   <li>Layer 4: SQL keyword detection to prevent injection attempts
   * </ul>
   *
   * <p>Even though the current implementation uses {@code FakeDatabase} (in-memory list), this
   * sanitization prepares the code for migration to a real database with prepared statements.
   *
   * <p><strong>Exception Handling:</strong> This method throws specific exceptions ({@link
   * SqlInjectionException}) which are caught by the {@code GlobalExceptionHandler} for centralized
   * error management.
   *
   * @param criteria Validated user data from the web layer
   * @return AddUserResult confirmation of user creation
   * @throws SqlInjectionException if SQL injection patterns are detected in input
   * @throws GenericException if save operation fails or unexpected errors occur
   */
  @Override
  public AddUserResult addUser(CriteriaAddUser criteria) throws GenericException {

    AddUserResult returnValue;
    User user;

    try {

      // Additional validation: detect SQL injection patterns (delegated to StringUtil)
      // This throws SqlInjectionException which extends GenericException
      stringUtil.validateAgainstSqlInjection(
          criteria.getFirstName(),
          criteria.getLastName(),
          criteria.getEmail(),
          criteria.getPhoneNumber());

      returnValue = new AddUserResult();

      user = new User();
      // Apply sanitization to all user inputs (delegated to StringUtil)
      user.setFirstName(stringUtil.sanitizeInput(criteria.getFirstName()));
      user.setLastName(stringUtil.sanitizeInput(criteria.getLastName()));
      user.setEmail(stringUtil.sanitizeInput(criteria.getEmail()));
      user.setPhoneNumber(stringUtil.sanitizeInput(criteria.getPhoneNumber()));

      if (!userRepository.save(user)) {
        throw new GenericException(500, "Error saving user");
      }

      // Set the created user in the result
      returnValue.setUser(user);

    } catch (SqlInjectionException e) {
      // Re-throw specific SQL injection exception (caught by GlobalExceptionHandler)
      throw e;
    } catch (GenericException e) {
      // Re-throw other business exceptions (caught by GlobalExceptionHandler)
      throw e;
    } catch (Exception e) {
      // Wrap unexpected exceptions in GenericException
      if (log.isErrorEnabled()) {
        log.error(e.getMessage(), e);
      }
      throw new GenericException(GenericException.GENERIC_ERROR);
    }
    return returnValue;
  }



  /**
   * Retrieves a paginated and sorted list of users with optional filtering.
   *
   * <p><strong>Features:</strong>
   *
   * <ul>
   *   <li>Case-insensitive search on firstName, lastName, and email
   *   <li>Sorting by OrderType enum (firstName, lastName, ASC/DESC)
   *   <li>Pagination with offset and limit
   * </ul>
   *
   * <p><strong>Implementation Details:</strong>
   *
   * <ol>
   *   <li>Filter users by search query (if provided)
   *   <li>Count total matches (for pagination metadata)
   *   <li>Apply sorting based on OrderType
   *   <li>Apply pagination (skip offset, take limit)
   *   <li>Convert User entities to UserDTOs
   * </ol>
   *
   * <p><strong>Note on Implementation:</strong>
   *
   * <p>The current implementation uses Java Streams for filtering, sorting, and pagination because
   * this application uses {@code FakeDatabase} (in-memory list). In a real-world scenario with a
   * database (e.g., PostgreSQL, MySQL), this logic would leverage <strong>Spring Data JPA</strong>
   * with {@code Pageable} and {@code @Query} annotations to push filtering, sorting, and pagination
   * to the database layer, significantly improving performance and scalability by:
   *
   * <ul>
   *   <li>Generating optimized SQL queries (WHERE, ORDER BY, LIMIT, OFFSET)
   *   <li>Reducing memory footprint (loading only requested records)
   *   <li>Utilizing database indexes for faster searches
   * </ul>
   *
   * @param criteria Search criteria with query, pagination, and sorting parameters
   * @return GetUsersResult with filtered, sorted, paginated list and total count
   * @throws GenericException if an unexpected error occurs
   */
  @Override
  public GetUsersResult getUsers(CriteriaGetUsers criteria) throws GenericException {
    try {
      // Step 1: Get all users from repository
      List<User> allUsers = userRepository.getAll();

      // Step 2: Apply filtering (delegated to UserFilterUtil)
      String searchQuery = criteria.getQuery();
      List<User> filteredUsers =
          allUsers.stream()
              .filter(user -> userFilterUtil.matchesSearchQuery(user, searchQuery))
              .toList();

      // Step 3: Count total matches (BEFORE pagination)
      int total = filteredUsers.size();

      // Step 4: Apply sorting (delegated to UserFilterUtil)
      List<User> sortedUsers = userFilterUtil.applySorting(filteredUsers, criteria.getOrder());

      // Step 5: Apply pagination (offset and limit)
      List<User> paginatedUsers =
          sortedUsers.stream().skip(criteria.getOffset()).limit(criteria.getLimit()).toList();

      // Step 6: Convert User entities to UserDTOs (delegated to UserAssembler)
      List<UserDTO> userDTOs = paginatedUsers.stream().map(userAssembler::toDTO).toList();

      // Step 7: Build result with users and pagination metadata
      GetUsersResult result = new GetUsersResult();
      result.setUsers(userDTOs);
      result.setTotal(total);

      return result;

    } catch (Exception e) {
      if (log.isErrorEnabled()) {
        log.error("Error retrieving users: {}", e.getMessage(), e);
      }
      throw new GenericException(GenericException.GENERIC_ERROR);
    }
  }

}
