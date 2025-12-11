package it.sara.demo.service.user.criteria;

import it.sara.demo.service.criteria.GenericCriteria;
import lombok.Getter;
import lombok.Setter;

/**
 * Service layer criteria for user search operations.
 *
 * <p>Contains parameters for filtering, pagination, and sorting
 * user search results.</p>
 */
@Getter
@Setter
public class CriteriaGetUsers extends GenericCriteria {

    /** Search query string for filtering users (case-insensitive, searches firstName, lastName, email). */
    private String query;

    /** Pagination offset (number of records to skip). */
    private int offset;

    /** Pagination limit (maximum number of records to return). */
    private int limit;

    /** Sort order for the results. */
    private OrderType order;

    /**
     * Enumeration of available sort orders for user search results.
     */
    @Getter
    public enum OrderType {
        /** Sort by first name ascending. */
        BY_FIRSTNAME("by firstName"),

        /** Sort by first name descending. */
        BY_FIRSTNAME_DESC("by firstName desc"),

        /** Sort by last name ascending. */
        BY_LASTNAME("by lastName"),

        /** Sort by last name descending. */
        BY_LASTNAME_DESC("by lastName");

        /** Human-readable display name for the sort order. */
        private final String displayName;

        /**
         * Constructor for OrderType enum.
         *
         * @param displayName Human-readable name for the sort order
         */
        OrderType(String displayName) {
            this.displayName = displayName;
        }
    }

}
