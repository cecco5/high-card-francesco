package it.sara.demo.web.user.response;

import it.sara.demo.dto.UserDTO;
import it.sara.demo.web.response.GenericPagedResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Web layer response for paginated user search operation.
 *
 * <p>Contains the list of users and pagination metadata (total count)
 * along with the standard status information.</p>
 */
@Getter
@Setter
public class GetUsersResponse extends GenericPagedResponse {
  /** List of users for the current page. */
  private List<UserDTO> users;
}
