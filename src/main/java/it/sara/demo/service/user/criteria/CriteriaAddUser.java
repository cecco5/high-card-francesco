package it.sara.demo.service.user.criteria;

import it.sara.demo.service.criteria.GenericCriteria;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriteriaAddUser extends GenericCriteria {

  @NotBlank(message = "First name is required")
  private String firstName;

  @NotBlank(message = "Last name is required")
  private String lastName;

  @NotBlank(message = "Email is required")
  @Email(
      regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
      message = "Email must be valid")
  private String email;

  @NotBlank(message = "Phone number is required")
  @Pattern(
      regexp = "^(\\+39)?\\s?3\\d{2}\\s?\\d{6,7}$",
      message = "Phone number must be a valid Italian mobile number")
  private String phoneNumber;
}
