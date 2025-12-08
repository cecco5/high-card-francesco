package it.sara.demo.service.user.criteria;

import it.sara.demo.service.criteria.GenericCriteria;
import lombok.Getter;
import lombok.Setter;

/**
 * Service layer criteria for adding a new user.
 *
 * <p>This DTO is used internally by the service layer and does not contain
 * validation annotations. Input validation is performed at the web layer
 * ({@code AddUserRequest}) before data reaches this criteria.</p>
 *
 * <p>This design maintains separation of concerns and allows the service layer
 * to remain independent from web framework validation constraints.</p>
 */
@Getter
@Setter
public class CriteriaAddUser extends GenericCriteria {

  private String firstName;
  private String lastName;
  private String email;
  private String phoneNumber;
}
