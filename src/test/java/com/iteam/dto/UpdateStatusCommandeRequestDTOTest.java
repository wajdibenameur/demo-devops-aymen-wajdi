package com.iteam.dto;

import com.iteam.entities.Status;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateStatusCommandeRequestDTOTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        // Arrange & Act
        UpdateStatusCommandeRequestDTO dto = new UpdateStatusCommandeRequestDTO();
        dto.setStatus(Status.Livré);

        // Assert
        assertThat(dto.getStatus()).isEqualTo(Status.Livré);
    }

    @Test
    void testAllStatusValues() {
        // Test toutes les valeurs de votre enum
        UpdateStatusCommandeRequestDTO dto = new UpdateStatusCommandeRequestDTO();

        dto.setStatus(Status.En_attente);
        assertThat(dto.getStatus()).isEqualTo(Status.En_attente);

        dto.setStatus(Status.En_cours);
        assertThat(dto.getStatus()).isEqualTo(Status.En_cours);

        dto.setStatus(Status.Livré);
        assertThat(dto.getStatus()).isEqualTo(Status.Livré);

        dto.setStatus(Status.Annulé);
        assertThat(dto.getStatus()).isEqualTo(Status.Annulé);
    }

    @Test
    void testGetterReturnsCorrectValue() {
        // Arrange
        UpdateStatusCommandeRequestDTO dto = new UpdateStatusCommandeRequestDTO();
        dto.setStatus(Status.En_cours);

        // Act & Assert
        assertThat(dto.getStatus()).isEqualTo(Status.En_cours);
    }

    @Test
    void testNullStatus() {
        // Arrange & Act
        UpdateStatusCommandeRequestDTO dto = new UpdateStatusCommandeRequestDTO();
        dto.setStatus(null);

        // Assert
        assertThat(dto.getStatus()).isNull();
    }
}