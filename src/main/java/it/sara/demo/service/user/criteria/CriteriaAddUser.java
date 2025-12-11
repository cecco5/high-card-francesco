package it.sara.demo.service.user.criteria;

import it.sara.demo.service.criteria.GenericCriteria;
import lombok.Getter;
import lombok.Setter;

/**
 * Service layer criteria for adding a new user.
 * <p>This design maintains separation of concerns and allows the service layer
 * to remain independent from web framework validation constraints.</p>
 */
@Getter
@Setter
public class CriteriaAddUser extends GenericCriteria {

  /** User's first name (sanitized at service layer). */
  private String firstName;

  /** User's last name (sanitized at service layer). */
  private String lastName;

  /** User's email address (validated and sanitized at service layer). */
  private String email;

  /** User's phone number in Italian format (validated at web layer). */
  private String phoneNumber;
}
