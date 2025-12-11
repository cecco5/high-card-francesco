package it.sara.demo.web.user.request;

import it.sara.demo.web.request.GenericRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * Web layer DTO for adding a new user.
 *
 * <p>Contains Bean Validation (JSR-380) annotations for input validation
 * at the controller level. Validation is triggered by {@code @Valid} annotation
 * in the controller methods.</p>
 *
 * <p><strong>Security:</strong> Additional sanitization and SQL injection
 * prevention is performed in the service layer.</p>
 */
@Getter
@Setter
public class AddUserRequest extends GenericRequest {

    /** User's first name (required, not blank). */
    @NotBlank(message = "First name is required")
    private String firstName;

    /** User's last name (required, not blank). */
    @NotBlank(message = "Last name is required")
    private String lastName;

    /** User's email address (required, validated with email regex). */
    @NotBlank(message = "Email is required")
    @Email(
        regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message = "Email must be valid")
    private String email;

    /** User's phone number in Italian mobile format (required, validated with regex). */
    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^(\\+39)?\\s?3\\d{2}\\s?\\d{7}$",
        message = "Phone number must be a valid Italian mobile number")
    private String phoneNumber;
}
