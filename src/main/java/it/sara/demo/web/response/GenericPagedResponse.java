package it.sara.demo.web.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Base response class for paginated API responses.
 *
 * <p>Extends {@code GenericResponse} adding pagination metadata
 * (total count) for endpoints that return paginated results.</p>
 */
@Getter
@Setter
public class GenericPagedResponse extends GenericResponse {
  /** Total count of items matching the query (across all pages). */
  protected int total;
}
