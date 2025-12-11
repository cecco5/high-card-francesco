package it.sara.demo.service.database;

import it.sara.demo.service.database.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory database simulation for development and testing.
 *
 * <p>Provides a static list of users pre-populated with test data.
 * In a production environment, this would be replaced with a real
 * database implementation (e.g., PostgreSQL, MySQL) using JPA/Hibernate.</p>
 *
 * <p><strong>Note:</strong> This is volatile storage - data is lost
 * when the application restarts.</p>
 */
public class FakeDatabase {

    /** Static list containing all user records (in-memory storage). */
    public static final List<User> TABLE_USER = new ArrayList<>();

    static {
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setGuid(java.util.UUID.randomUUID().toString());
            user.setFirstName("First name " + i);
            user.setLastName("Last name " + i);
            user.setEmail("user" + i + "@example.com");
            user.setPhoneNumber("+39" + i);
            TABLE_USER.add(user);
        }
    }

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static members.
     */
    private FakeDatabase() {

    }

}
