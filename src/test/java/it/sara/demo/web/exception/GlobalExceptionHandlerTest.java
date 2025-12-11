package it.sara.demo.web.exception;

import it.sara.demo.dto.StatusDTO;
import it.sara.demo.exception.GenericException;
import it.sara.demo.web.response.GenericResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link GlobalExceptionHandler}.
 */
@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  @DisplayName("handleValidationExceptions - single field error")
  void testHandleValidationExceptions_SingleError() {
    FieldError fieldError = new FieldError("object", "email", "Email is required");
    BindingResult bindingResult = mock(BindingResult.class);
    when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    when(ex.getBindingResult()).thenReturn(bindingResult);

    ResponseEntity<GenericResponse> response = handler.handleValidationExceptions(ex);

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());

    StatusDTO status = response.getBody().getStatus();
    assertEquals(400, status.getCode());
    assertEquals("Email is required", status.getMessage());
    assertNotNull(status.getTraceId());
  }

  @Test
  @DisplayName("handleValidationExceptions - multiple field errors concatenated")
  void testHandleValidationExceptions_MultipleErrors() {
    FieldError e1 = new FieldError("o", "email", "Email is required");
    FieldError e2 = new FieldError("o", "phone", "Phone is invalid");
    BindingResult bindingResult = mock(BindingResult.class);
    when(bindingResult.getFieldErrors()).thenReturn(List.of(e1, e2));

    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    when(ex.getBindingResult()).thenReturn(bindingResult);

    ResponseEntity<GenericResponse> response = handler.handleValidationExceptions(ex);

    StatusDTO status = response.getBody().getStatus();
    assertEquals(400, status.getCode());
    assertTrue(status.getMessage().contains("Email is required"));
    assertTrue(status.getMessage().contains("Phone is invalid"));
  }

  @Test
  @DisplayName("handleGenericException(GenericException) - uses status from exception and adds traceId if missing")
  void testHandleGenericException_Generic() {
    StatusDTO status = new StatusDTO();
    status.setCode(422);
    status.setMessage("Business error");
    GenericException ex = new GenericException(status);

    ResponseEntity<GenericResponse> response = handler.handleGenericException(ex);

    assertEquals(200, response.getStatusCode().value());
    StatusDTO bodyStatus = response.getBody().getStatus();
    assertEquals(422, bodyStatus.getCode());
    assertEquals("Business error", bodyStatus.getMessage());
    assertNotNull(bodyStatus.getTraceId());
  }

  @Test
  @DisplayName("handleGenericException(GenericException) - preserves existing traceId")
  void testHandleGenericException_PreserveTraceId() {
    StatusDTO status = new StatusDTO();
    status.setCode(400);
    status.setMessage("Test");
    status.setTraceId("existing-trace");
    GenericException ex = new GenericException(status);

    ResponseEntity<GenericResponse> response = handler.handleGenericException(ex);

    assertEquals("existing-trace", response.getBody().getStatus().getTraceId());
  }

  @Test
  @DisplayName("handleGenericException(Exception) - unexpected exception mapped to 500 status with traceId")
  void testHandleGenericException_Unexpected() {
    Exception ex = new RuntimeException("Unexpected");

    ResponseEntity<GenericResponse> response = handler.handleGenericException(ex);

    assertEquals(200, response.getStatusCode().value());
    StatusDTO status = response.getBody().getStatus();
    assertEquals(500, status.getCode());
    assertTrue(status.getMessage().contains("Internal server error"));
    assertNotNull(status.getTraceId());
  }
}

