package it.sara.demo.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object representing the status of a response.
 *
 * <p>Contains standardized fields for status code, message, and trace ID
 * used across all API responses in the application.</p>
 */
@Getter
@Setter
public class StatusDTO {
    /** HTTP status code (e.g., 200, 400, 401, 500). */
    private int code;

    /** Human-readable status message. */
    private String message;

    /** Unique trace identifier for request tracking and debugging. */
    private String traceId;
}
