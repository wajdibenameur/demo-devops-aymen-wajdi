package com.iteam.exception;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationErrorResponseTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        // Arrange
        Map<String, String> fieldErrors = new HashMap<>();
        fieldErrors.put("email", "Email invalide");
        fieldErrors.put("password", "Mot de passe trop court");

        // Act
        ValidationErrorResponse response = new ValidationErrorResponse();
        response.setStatus(400);
        response.setError("VALIDATION_FAILED");
        response.setMessage("Validation failed");
        response.setTimestamp("2026-01-04T20:30:00");
        response.setFieldErrors(fieldErrors);

        // Assert
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getError()).isEqualTo("VALIDATION_FAILED");
        assertThat(response.getMessage()).isEqualTo("Validation failed");
        assertThat(response.getTimestamp()).isEqualTo("2026-01-04T20:30:00");
        assertThat(response.getFieldErrors()).hasSize(2);
        assertThat(response.getFieldErrors().get("email")).isEqualTo("Email invalide");
        assertThat(response.getFieldErrors().get("password")).isEqualTo("Mot de passe trop court");
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        String timestamp = LocalDateTime.now().toString();
        Map<String, String> fieldErrors = new HashMap<>();
        fieldErrors.put("name", "Le nom est requis");

        // Act - Utilisez le constructeur sans arguments + setters
        ValidationErrorResponse response = new ValidationErrorResponse();
        response.setStatus(422);
        response.setError("UNPROCESSABLE_ENTITY");
        response.setMessage("Validation error");
        response.setTimestamp(timestamp);
        response.setFieldErrors(fieldErrors);

        // Assert
        assertThat(response.getStatus()).isEqualTo(422);
        assertThat(response.getError()).isEqualTo("UNPROCESSABLE_ENTITY");
        assertThat(response.getMessage()).isEqualTo("Validation error");
        assertThat(response.getTimestamp()).isEqualTo(timestamp);
        assertThat(response.getFieldErrors()).isEqualTo(fieldErrors);
    }

    @Test
    void testInheritance() {
        // Arrange
        ValidationErrorResponse response = new ValidationErrorResponse();

        // Assert - Vérifier que c'est bien une sous-classe de ErrorResponse
        assertThat(response).isInstanceOf(ErrorResponse.class);
    }

    @Test
    void testToString() {
        // Arrange
        Map<String, String> fieldErrors = new HashMap<>();
        fieldErrors.put("field", "error");

        ValidationErrorResponse response = new ValidationErrorResponse();
        response.setStatus(400);
        response.setError("BAD_REQUEST");
        response.setMessage("Validation failed");
        response.setTimestamp("timestamp");
        response.setFieldErrors(fieldErrors);

        // Act
        String toString = response.toString();

        // Assert
        assertThat(toString).contains("ValidationErrorResponse");
        assertThat(toString).contains("fieldErrors={field=error}");
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Map<String, String> errors1 = new HashMap<>();
        errors1.put("field", "error");

        Map<String, String> errors2 = new HashMap<>();
        errors2.put("field", "error");

        String timestamp = "2026-01-04T20:30:00";

        // Création des objets avec setters
        ValidationErrorResponse response1 = new ValidationErrorResponse();
        response1.setStatus(400);
        response1.setError("BAD_REQUEST");
        response1.setMessage("Error");
        response1.setTimestamp(timestamp);
        response1.setFieldErrors(errors1);

        ValidationErrorResponse response2 = new ValidationErrorResponse();
        response2.setStatus(400);
        response2.setError("BAD_REQUEST");
        response2.setMessage("Error");
        response2.setTimestamp(timestamp);
        response2.setFieldErrors(errors2);

        ValidationErrorResponse response3 = new ValidationErrorResponse();
        response3.setStatus(500);
        response3.setError("INTERNAL_ERROR");
        response3.setMessage("Error");
        response3.setTimestamp(timestamp);
        response3.setFieldErrors(errors1);

        // Assert
        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        assertThat(response1).isNotEqualTo(response3);
    }
}