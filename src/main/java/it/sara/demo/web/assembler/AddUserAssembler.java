package it.sara.demo.web.assembler;

import it.sara.demo.service.assembler.UserAssembler;
import it.sara.demo.service.user.criteria.CriteriaAddUser;
import it.sara.demo.service.user.result.AddUserResult;
import it.sara.demo.web.user.request.AddUserRequest;
import it.sara.demo.web.user.response.AddUserResponse;
import org.springframework.stereotype.Component;

/**
 * Assembler to convert between web layer and service layer DTOs for user operations.
 * Maintains separation of concerns between presentation and business logic layers.
 */
@Component
public class AddUserAssembler {

    private final UserAssembler userAssembler;

    /**
     * Constructor-based dependency injection.
     *
     * @param userAssembler Assembler for converting User entities to UserDTO
     */
    public AddUserAssembler(UserAssembler userAssembler) {
        this.userAssembler = userAssembler;
    }

    /**
     * Converts AddUserRequest (web layer) to CriteriaAddUser (service layer).
     *
     * @param addUserRequest User data from the web request
     * @return CriteriaAddUser for service layer processing
     */
    public CriteriaAddUser toCriteria(AddUserRequest addUserRequest) {
        CriteriaAddUser returnValue = new CriteriaAddUser();
        returnValue.setEmail(addUserRequest.getEmail());
        returnValue.setFirstName(addUserRequest.getFirstName());
        returnValue.setLastName(addUserRequest.getLastName()); // Fixed: was getFirstName()
        returnValue.setPhoneNumber(addUserRequest.getPhoneNumber());
        return returnValue;
    }

    /**
     * Converts AddUserResult (service layer) to AddUserResponse (web layer).
     *
     * @param result Result from service layer containing the created user
     * @return AddUserResponse for web layer with UserDTO
     */
    public AddUserResponse toResponse(AddUserResult result) {
        AddUserResponse response = new AddUserResponse();
        response.setStatus(AddUserResponse.success("User added.").getStatus());

        // Convert User entity to UserDTO
        if (result.getUser() != null) {
            response.setUser(userAssembler.toDTO(result.getUser()));
        }

        return response;
    }
}
