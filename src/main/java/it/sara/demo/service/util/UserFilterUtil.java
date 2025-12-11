package it.sara.demo.service.util;

import it.sara.demo.service.database.model.User;
import it.sara.demo.service.user.criteria.CriteriaGetUsers;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Utility class for filtering and sorting User entities.
 *
 * <p>Provides methods for:
 * <ul>
 *   <li>Case-insensitive search filtering</li>
 *   <li>Sorting based on OrderType enum</li>
 * </ul>
 *
 * <p><strong>Note:</strong> This implementation uses Java Streams for in-memory operations
 * because the application uses {@code FakeDatabase}. With a real database,
 * these operations would be delegated to Spring Data JPA.</p>
 */
@Component
public class UserFilterUtil {

  private final StringUtil stringUtil;

  public UserFilterUtil(StringUtil stringUtil) {
    this.stringUtil = stringUtil;
  }

  /**
   * Checks if a user matches the search query (case-insensitive).
   * Searches in firstName, lastName, and email fields using contains.
   *
   * @param user User entity to check
   * @param query Search query (can be null or empty)
   * @return true if user matches, or if query is null/blank (returns all)
   */
  public boolean matchesSearchQuery(User user, String query) {
    return stringUtil.containsIgnoreCase(user.getFirstName(), query)
        || stringUtil.containsIgnoreCase(user.getLastName(), query)
        || stringUtil.containsIgnoreCase(user.getEmail(), query);
  }

  /**
   * Applies sorting to the user list based on OrderType enum.
   *
   * @param users List of users to sort
   * @param orderType Sorting order (BY_FIRSTNAME, BY_LASTNAME, with optional DESC)
   * @return Sorted list of users
   */
  public List<User> applySorting(List<User> users, CriteriaGetUsers.OrderType orderType) {
    Comparator<User> comparator = getComparator(orderType);
    return users.stream().sorted(comparator).toList();
  }

  /**
   * Returns the appropriate comparator based on the OrderType.
   *
   * @param orderType Sorting order
   * @return Comparator for User entities
   */
  private Comparator<User> getComparator(CriteriaGetUsers.OrderType orderType) {
    return switch (orderType) {
      case BY_FIRSTNAME ->
          Comparator.comparing(User::getFirstName, String.CASE_INSENSITIVE_ORDER);

      case BY_FIRSTNAME_DESC ->
          Comparator.comparing(User::getFirstName, String.CASE_INSENSITIVE_ORDER).reversed();

      case BY_LASTNAME ->
          Comparator.comparing(User::getLastName, String.CASE_INSENSITIVE_ORDER);

      case BY_LASTNAME_DESC ->
          Comparator.comparing(User::getLastName, String.CASE_INSENSITIVE_ORDER).reversed();
    };
  }
}

