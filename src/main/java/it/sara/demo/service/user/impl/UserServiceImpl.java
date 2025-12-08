package it.sara.demo.service.user.impl;

import it.sara.demo.exception.GenericException;
import it.sara.demo.service.database.UserRepository;
import it.sara.demo.service.database.model.User;
import it.sara.demo.service.user.UserService;
import it.sara.demo.service.user.criteria.CriteriaAddUser;
import it.sara.demo.service.user.criteria.CriteriaGetUsers;
import it.sara.demo.service.user.result.AddUserResult;
import it.sara.demo.service.user.result.GetUsersResult;
import it.sara.demo.service.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final StringUtil stringUtil;

  /**
   * Constructor-based dependency injection (best practice).
   * Allows for immutable fields, easier testing, and explicit dependencies.
   *
   * @param userRepository Repository for user data access
   * @param stringUtil Utility class for string operations
   */
  public UserServiceImpl(UserRepository userRepository, StringUtil stringUtil) {
    this.userRepository = userRepository;
    this.stringUtil = stringUtil;
  }

  /**
   * Adds a new user to the system with SQL injection prevention.
   *
   * <p><strong>Security Strategy - Defense in Depth:</strong></p>
   * <ul>
   *   <li>Layer 1: Bean Validation via {@code @Valid} in Controller</li>
   *   <li>Layer 2: Regex validation in {@link CriteriaAddUser} ({@code @Email}, {@code @Pattern})</li>
   *   <li>Layer 3: Input sanitization in Service Layer (removes SQL dangerous characters)</li>
   *   <li>Layer 4: SQL keyword detection to prevent injection attempts</li>
   * </ul>
   *
   * <p>Even though the current implementation uses {@code FakeDatabase} (in-memory list),
   * this sanitization prepares the code for migration to a real database with prepared statements.</p>
   *
   * @param criteria Validated user data from the web layer
   * @return AddUserResult confirmation of user creation
   * @throws GenericException if validation fails, SQL patterns detected, or save operation fails
   */
  @Override
  public AddUserResult addUser(CriteriaAddUser criteria) throws GenericException {

    AddUserResult returnValue;
    User user;

    try {

      // Additional validation: detect SQL injection patterns
      validateAgainstSqlInjection(criteria);

      returnValue = new AddUserResult();

      user = new User();
      // Apply sanitization to all user inputs (defense in depth)
      user.setFirstName(sanitizeInput(criteria.getFirstName()));
      user.setLastName(sanitizeInput(criteria.getLastName()));
      user.setEmail(sanitizeInput(criteria.getEmail()));
      user.setPhoneNumber(sanitizeInput(criteria.getPhoneNumber()));

      if (!userRepository.save(user)) {
        throw new GenericException(500, "Error saving user");
      }

      // Set the created user in the result
      returnValue.setUser(user);

    } catch (GenericException e) {
      // Re-throw GenericException with original code
      throw e;
    } catch (Exception e) {
      if (log.isErrorEnabled()) {
        log.error(e.getMessage(), e);
      }
      throw new GenericException(GenericException.GENERIC_ERROR);
    }
    return returnValue;
  }

  /**
   * Sanitizes user input by removing potentially dangerous SQL characters.
   * Applied as defense in depth, even after Bean Validation.
   *
   * <p><strong>Removed characters:</strong></p>
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
  private String sanitizeInput(String input) {
    if (input == null) {
      return null;
    }
    // Remove SQL dangerous characters and trim whitespace
    return input.trim().replaceAll("[;<>\"'\\\\]", "");
  }

  /**
   * Validates user input against common SQL injection patterns.
   * Detects SQL keywords and comment markers in combined user input.
   *
   * <p><strong>Detected patterns:</strong></p>
   * <ul>
   *   <li>SQL DML keywords: DROP, DELETE, INSERT, UPDATE, SELECT</li>
   *   <li>SQL comment markers: {@code --}, {@code /*}, {@code * /}</li>
   * </ul>
   *
   * @param criteria User data to validate
   * @throws GenericException with 400 status code if SQL patterns are detected
   */
  private void validateAgainstSqlInjection(CriteriaAddUser criteria) throws GenericException {
    // SQL keywords commonly used in injection attacks
    String[] sqlKeywords = {"DROP", "DELETE", "INSERT", "UPDATE", "SELECT", "--", "/*", "*/"};

    // Combine all input fields for comprehensive check
    String fullInput = String.join(" ",
        criteria.getFirstName(),
        criteria.getLastName(),
        criteria.getEmail(),
        criteria.getPhoneNumber()
    ).toUpperCase();

    // Check for SQL keywords
    for (String keyword : sqlKeywords) {
      if (fullInput.contains(keyword)) {
        if (log.isWarnEnabled()) {
          log.warn("SQL injection attempt detected. Keyword found: {}", keyword);
        }
        throw new GenericException(400, "Invalid input: SQL keywords detected");
      }
    }
  }

  @Override
  public GetUsersResult getUsers(CriteriaGetUsers criteriaGetUsers) throws GenericException {
    return null;
  }
}
