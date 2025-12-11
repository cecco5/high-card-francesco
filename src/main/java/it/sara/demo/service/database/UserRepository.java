package it.sara.demo.service.database;

import it.sara.demo.service.database.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Repository for user data access operations.
 *
 * <p>Provides methods for retrieving and saving user entities.
 * Currently uses {@code FakeDatabase} (in-memory list), but designed
 * for easy migration to a real database (e.g., JPA repository).</p>
 */
@Component
public class UserRepository {

    /**
     * Saves a new user to the database.
     * Automatically generates and assigns a unique GUID to the user.
     *
     * @param user User entity to save (GUID will be generated)
     * @return true if save was successful
     */
    public boolean save(User user) {
        user.setGuid(java.util.UUID.randomUUID().toString());
        FakeDatabase.TABLE_USER.add(user);
        return true;
    }

    /**
     * Retrieves a user by their unique GUID.
     *
     * @param guid Unique identifier of the user
     * @return Optional containing the user if found, empty otherwise
     */
    public Optional<User> getByGuid(String guid) {
        return FakeDatabase.TABLE_USER.stream().filter(u -> u.getGuid().equals(guid)).findFirst();
    }

    /**
     * Retrieves all users from the database.
     *
     * @return List of all users
     */
    public List<User> getAll() {
        return FakeDatabase.TABLE_USER;
    }
}
