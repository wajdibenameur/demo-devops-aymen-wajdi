package com.iteam.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Test
    void handleRuntimeException_ShouldReturnNotFoundResponse() {
        // Arrange
        RuntimeException exception = new RuntimeException("Ressource non trouvée");

        // Act
        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleRuntimeException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getError()).isEqualTo("Not Found");
        assertThat(response.getBody().getMessage()).isEqualTo("Ressource non trouvée");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void handleValidationExceptions_ShouldReturnValidationErrorResponse() {
        // Arrange
        List<FieldError> fieldErrors = Arrays.asList(
                new FieldError("user", "email", "Email invalide"),
                new FieldError("user", "password", "Mot de passe trop court")
        );

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(new ArrayList<>(fieldErrors));

        // Act
        ResponseEntity<ValidationErrorResponse> response =
                globalExceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("VALIDATION_FAILED");
        assertThat(response.getBody().getMessage()).isEqualTo("Validation failed for one or more fields");
        assertThat(response.getBody().getTimestamp()).isNotNull();

        Map<String, String> fieldErrorsMap = response.getBody().getFieldErrors();
        assertThat(fieldErrorsMap).hasSize(2);
        assertThat(fieldErrorsMap.get("email")).isEqualTo("Email invalide");
        assertThat(fieldErrorsMap.get("password")).isEqualTo("Mot de passe trop court");
    }

    @Test
    void handleValidationExceptions_WithNoFieldErrors_ShouldReturnEmptyFieldErrors() {
        // Arrange
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<ValidationErrorResponse> response =
                globalExceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getFieldErrors()).isEmpty();
    }
}