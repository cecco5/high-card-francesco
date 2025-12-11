package it.sara.demo.service.result;

import lombok.Getter;
import lombok.Setter;

/**
 * Base result class for paginated service layer responses.
 *
 * <p>Extends {@link GenericResult} with pagination metadata.</p>
 */
@Getter
@Setter
public class GenericPagedResult extends GenericResult {
    /**
     * Total number of records matching the query (before pagination).
     */
    private int total;
}
