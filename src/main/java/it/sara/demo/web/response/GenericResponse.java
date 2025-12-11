package it.sara.demo.web.response;

import it.sara.demo.dto.StatusDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * Base response class for all API responses.
 *
 * <p>All web layer response DTOs extend this class to ensure
 * consistent status information (code, message, traceId) across
 * all API endpoints.</p>
 */
@Getter
@Setter
public class GenericResponse {
    /** Status information with code, message, and trace ID. */
    private StatusDTO status;

    /**
     * Factory method to create a successful response with HTTP 200 status.
     *
     * @param message Success message (defaults to "Success" if null)
     * @return GenericResponse with 200 status code and generated trace ID
     */
    public static GenericResponse success(String message) {
        GenericResponse returnValue = new GenericResponse();
        returnValue.setStatus(new StatusDTO());
        returnValue.getStatus().setCode(200);
        returnValue.getStatus().setMessage(message != null ? message : "Success");
        returnValue.getStatus().setTraceId(java.util.UUID.randomUUID().toString());
        return returnValue;
    }
}
