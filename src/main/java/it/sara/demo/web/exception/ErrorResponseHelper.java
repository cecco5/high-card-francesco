package it.sara.demo.web.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.sara.demo.dto.StatusDTO;
import it.sara.demo.web.response.GenericResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper class for generating consistent error responses.
 *
 * <p>Used by filters and other components that operate outside the Spring MVC
 * context and cannot rely on {@link GlobalExceptionHandler}.</p>
 *
 * <p>Ensures all error responses follow the same {@link StatusDTO} format
 * regardless of where they originate.</p>
 */
@Slf4j
public class ErrorResponseHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private ErrorResponseHelper() {
        // Utility class
    }

    /**
     * Writes a standardized error response to the HTTP response.
     *
     * <p>Always returns HTTP 200 with error details in the response body,
     * consistent with application-wide error handling strategy.</p>
     *
     * @param response HTTP response to write to
     * @param code Error code (e.g., 400, 401, 500)
     * @param message Error message
     * @throws IOException if writing to response fails
     */
    public static void writeErrorResponse(
            HttpServletResponse response,
            int code,
            String message) throws IOException {

        String traceId = UUID.randomUUID().toString();

        if (log.isWarnEnabled()) {
            log.warn("Error response [traceId: {}]: {} (code: {})", traceId, message, code);
        }

        StatusDTO status = new StatusDTO();
        status.setCode(code);
        status.setMessage(message);
        status.setTraceId(traceId);

        GenericResponse errorResponse = new GenericResponse();
        errorResponse.setStatus(status);

        response.setStatus(HttpServletResponse.SC_OK);  // Always HTTP 200
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}

