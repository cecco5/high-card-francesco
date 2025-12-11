package it.sara.demo.web.user.request;

import it.sara.demo.service.user.criteria.CriteriaGetUsers;
import it.sara.demo.web.request.GenericRequest;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

/**
 * Web layer request for retrieving users with pagination, sorting, and filtering.
 *
 * <p>This request supports:</p>
 * <ul>
 *   <li>Case-insensitive search on firstName, lastName, and email</li>
 *   <li>Pagination with offset and limit</li>
 *   <li>Sorting by various fields (see {@link CriteriaGetUsers.OrderType})</li>
 * </ul>
 */
@Getter
@Setter
public class GetUsersRequest extends GenericRequest {

  /**
   * Search query string for filtering users (case-insensitive).
   * Filters firstName, lastName, and email fields using contains.
   * If null or empty, returns all users.
   */
  private String query;

  /**
   * Offset for pagination (number of records to skip).
   * Must be non-negative. Default: 0
   */
  @Min(value = 0, message = "Offset must be non-negative")
  private int offset = 0;

  /**
   * Maximum number of records to return.
   * Must be positive. Default: 10
   */
  @Min(value = 1, message = "Limit must be at least 1")
  private int limit = 10;

  /**
   * Sorting order for results.
   * If null, uses default order (BY_FIRSTNAME).
   */
  private CriteriaGetUsers.OrderType order;
}
