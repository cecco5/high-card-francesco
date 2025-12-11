package it.sara.demo.service.user.result;

import it.sara.demo.dto.UserDTO;
import it.sara.demo.service.result.GenericPagedResult;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Service layer result for paginated user search operation.
 *
 * <p>Contains a list of users matching the search criteria along
 * with pagination metadata (total count) inherited from {@code GenericPagedResult}.</p>
 */
@Getter
@Setter
public class GetUsersResult extends GenericPagedResult {
  /** List of users matching the search criteria for the current page. */
  private List<UserDTO> users;
}
