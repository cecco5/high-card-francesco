package it.sara.demo.service.util;

import it.sara.demo.service.database.model.User;
import it.sara.demo.service.user.criteria.CriteriaGetUsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserFilterUtil Focused Unit Tests")
class UserFilterUtilTest {

  private UserFilterUtil userFilterUtil;

  private User user1;
  private User user2;
  private User user3;

  @BeforeEach
  void setUp() {
    userFilterUtil = new UserFilterUtil(new StringUtil());

    user1 = new User();
    user1.setGuid("guid-1");
    user1.setFirstName("Alice");
    user1.setLastName("Smith");
    user1.setEmail("alice.smith@example.com");

    user2 = new User();
    user2.setGuid("guid-2");
    user2.setFirstName("Bob");
    user2.setLastName("Johnson");
    user2.setEmail("bob.johnson@example.com");

    user3 = new User();
    user3.setGuid("guid-3");
    user3.setFirstName("Charlie");
    user3.setLastName("Brown");
    user3.setEmail("charlie.brown@example.com");
  }

  @Test
  @DisplayName("matchesSearchQuery - should match by firstName/lastName/email (case-insensitive)")
  void testMatchesSearchQuery() {
    assertTrue(userFilterUtil.matchesSearchQuery(user1, "alice"));
    assertTrue(userFilterUtil.matchesSearchQuery(user2, "johnson"));
    assertTrue(userFilterUtil.matchesSearchQuery(user3, "brown"));
    assertFalse(userFilterUtil.matchesSearchQuery(user1, "xyz"));
  }

  @Test
  @DisplayName("applySorting - sort by firstName ASC/DESC")
  void testApplySorting_FirstName() {
    List<User> users = Arrays.asList(user2, user3, user1); // Bob, Charlie, Alice

    List<User> asc = userFilterUtil.applySorting(users, CriteriaGetUsers.OrderType.BY_FIRSTNAME);
    assertEquals("Alice", asc.get(0).getFirstName());
    assertEquals("Bob", asc.get(1).getFirstName());
    assertEquals("Charlie", asc.get(2).getFirstName());

    List<User> desc =
        userFilterUtil.applySorting(users, CriteriaGetUsers.OrderType.BY_FIRSTNAME_DESC);
    assertEquals("Charlie", desc.get(0).getFirstName());
    assertEquals("Bob", desc.get(1).getFirstName());
    assertEquals("Alice", desc.get(2).getFirstName());
  }

  @Test
  @DisplayName("applySorting - sort by lastName ASC/DESC")
  void testApplySorting_LastName() {
    List<User> users = Arrays.asList(user1, user2, user3); // Smith, Johnson, Brown

    List<User> asc = userFilterUtil.applySorting(users, CriteriaGetUsers.OrderType.BY_LASTNAME);
    assertEquals("Brown", asc.get(0).getLastName());
    assertEquals("Johnson", asc.get(1).getLastName());
    assertEquals("Smith", asc.get(2).getLastName());

    List<User> desc =
        userFilterUtil.applySorting(users, CriteriaGetUsers.OrderType.BY_LASTNAME_DESC);
    assertEquals("Smith", desc.get(0).getLastName());
    assertEquals("Johnson", desc.get(1).getLastName());
    assertEquals("Brown", desc.get(2).getLastName());
  }
}
