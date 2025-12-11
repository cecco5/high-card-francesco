package it.sara.demo.service.user;

import it.sara.demo.exception.GenericException;
import it.sara.demo.service.user.criteria.CriteriaAddUser;
import it.sara.demo.service.user.criteria.CriteriaGetUsers;
import it.sara.demo.service.user.result.AddUserResult;
import it.sara.demo.service.user.result.GetUsersResult;

/**
 * Service interface for user-related business operations.
 *
 * <p>Defines the contract for user management operations including
 * creation and retrieval with search/pagination capabilities.</p>
 */
public interface UserService {

    /**
     * Creates a new user in the system.
     *
     * @param addUserRequest Criteria containing user data to create
     * @return Result containing the created user entity
     * @throws GenericException if user creation fails or validation errors occur
     */
    AddUserResult addUser(CriteriaAddUser addUserRequest) throws GenericException;

    /**
     * Retrieves users with pagination, filtering, and sorting.
     *
     * @param criteriaGetUsers Criteria containing search query, pagination, and sort parameters
     * @return Result containing the list of users and total count
     * @throws GenericException if retrieval fails or unexpected errors occur
     */
    GetUsersResult getUsers(CriteriaGetUsers criteriaGetUsers) throws GenericException;
}
