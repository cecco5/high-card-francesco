package it.sara.demo.web.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Base request class for all API requests.
 *
 * <p>All web layer request DTOs extend this class to ensure
 * a consistent structure. Currently serves as a marker class
 * for type hierarchy and future extensibility.</p>
 */
@Getter
@Setter
public class GenericRequest {}
