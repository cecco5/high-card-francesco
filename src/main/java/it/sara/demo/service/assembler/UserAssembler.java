package it.sara.demo.service.assembler;

import it.sara.demo.dto.UserDTO;
import it.sara.demo.service.database.model.User;
import org.springframework.stereotype.Component;

/**
 * Assembler for converting User entities to UserDTO objects.
 *
 * <p>Handles the mapping between the service layer model and the DTO
 * used for external communication.</p>
 */
@Component
public class UserAssembler {

    /**
     * Converts a User entity to a UserDTO.
     *
     * @param user The user entity from the service layer
     * @return UserDTO for external communication
     */
    public UserDTO toDTO(User user) {
        UserDTO returnValue = new UserDTO();
        returnValue.setGuid(user.getGuid());
        returnValue.setFirstName(user.getFirstName());
        returnValue.setLastName(user.getLastName());
        returnValue.setEmail(user.getEmail()); // Fixed: was taking only domain
        returnValue.setPhoneNumber(user.getPhoneNumber()); // Fixed: was missing
        return returnValue;
    }
}
