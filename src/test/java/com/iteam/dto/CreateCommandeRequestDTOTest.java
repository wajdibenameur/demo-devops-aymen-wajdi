package com.iteam.dto;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CreateCommandeRequestDTOTest {

    @Test
    void testBuilder() {
        // Arrange & Act
        List<Long> productIds = Arrays.asList(1L, 2L, 3L);
        CreateCommandeRequestDTO dto = CreateCommandeRequestDTO.builder()
                .userId(1L)
                .productsId(productIds)
                .build();

        // Assert
        assertThat(dto.getUserId()).isEqualTo(1L);
        assertThat(dto.getProductsId()).hasSize(3);
        assertThat(dto.getProductsId()).containsExactly(1L, 2L, 3L);
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        // Arrange & Act
        CreateCommandeRequestDTO dto = new CreateCommandeRequestDTO();
        List<Long> productIds = Arrays.asList(4L, 5L);

        dto.setUserId(2L);
        dto.setProductsId(productIds);

        // Assert
        assertThat(dto.getUserId()).isEqualTo(2L);
        assertThat(dto.getProductsId()).hasSize(2);
        assertThat(dto.getProductsId()).containsExactly(4L, 5L);
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        List<Long> productIds = Arrays.asList(10L, 20L);

        // Act
        CreateCommandeRequestDTO dto = new CreateCommandeRequestDTO(3L, productIds);

        // Assert
        assertThat(dto.getUserId()).isEqualTo(3L);
        assertThat(dto.getProductsId()).hasSize(2);
        assertThat(dto.getProductsId()).containsExactly(10L, 20L);
    }

    @Test
    void testEmptyProductsId() {
        // Arrange & Act
        CreateCommandeRequestDTO dto = CreateCommandeRequestDTO.builder()
                .userId(5L)
                .productsId(null)
                .build();

        // Assert
        assertThat(dto.getUserId()).isEqualTo(5L);
        assertThat(dto.getProductsId()).isNull();
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        List<Long> products1 = Arrays.asList(1L, 2L);
        List<Long> products2 = Arrays.asList(1L, 2L);

        CreateCommandeRequestDTO dto1 = new CreateCommandeRequestDTO(1L, products1);
        CreateCommandeRequestDTO dto2 = new CreateCommandeRequestDTO(1L, products2);
        CreateCommandeRequestDTO dto3 = new CreateCommandeRequestDTO(2L, products1);

        // Assert
        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1).isNotEqualTo(dto3);
    }

    @Test
    void testToString() {
        // Arrange & Act
        CreateCommandeRequestDTO dto = new CreateCommandeRequestDTO(1L, Arrays.asList(1L, 2L));
        String toString = dto.toString();

        // Assert
        assertThat(toString).contains("CreateCommandeRequestDTO");
        assertThat(toString).contains("userId=1");
    }
}