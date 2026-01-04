package com.iteam.exception;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        // Arrange & Act
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(404);
        errorResponse.setError("Not Found");
        errorResponse.setMessage("Ressource non trouvée");
        errorResponse.setTimestamp("2026-01-04T20:30:00");

        // Assert
        assertThat(errorResponse.getStatus()).isEqualTo(404);
        assertThat(errorResponse.getError()).isEqualTo("Not Found");
        assertThat(errorResponse.getMessage()).isEqualTo("Ressource non trouvée");
        assertThat(errorResponse.getTimestamp()).isEqualTo("2026-01-04T20:30:00");
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        String timestamp = LocalDateTime.now().toString();

        // Act
        ErrorResponse errorResponse = new ErrorResponse(
                400, "Bad Request", "Requête invalide", timestamp
        );

        // Assert
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getError()).isEqualTo("Bad Request");
        assertThat(errorResponse.getMessage()).isEqualTo("Requête invalide");
        assertThat(errorResponse.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        String timestamp = "2026-01-04T20:30:00";
        ErrorResponse response1 = new ErrorResponse(404, "Not Found", "Not Found", timestamp);
        ErrorResponse response2 = new ErrorResponse(404, "Not Found", "Not Found", timestamp);
        ErrorResponse response3 = new ErrorResponse(500, "Internal Error", "Error", timestamp);

        // Assert
        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        assertThat(response1).isNotEqualTo(response3);
    }

    @Test
    void testToString() {
        // Arrange
        ErrorResponse errorResponse = new ErrorResponse(
                404, "Not Found", "Ressource non trouvée", "2026-01-04T20:30:00"
        );

        // Act
        String toString = errorResponse.toString();

        // Assert
        assertThat(toString).contains("ErrorResponse");
        assertThat(toString).contains("status=404");
        assertThat(toString).contains("error=Not Found");
        assertThat(toString).contains("message=Ressource non trouvée");
    }
}