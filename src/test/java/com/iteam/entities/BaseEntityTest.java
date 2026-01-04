package com.iteam.entities;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BaseEntityTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        // Arrange & Act
        // Création d'une classe anonyme pour tester BaseEntity (classe abstraite)
        BaseEntity entity = new BaseEntity() {};
        entity.setId(1L);

        // Assert
        assertThat(entity.getId()).isEqualTo(1L);
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange & Act
        // Création d'une classe anonyme avec constructeur
        BaseEntity entity = new BaseEntity(1L) {};

        // Assert
        assertThat(entity.getId()).isEqualTo(1L);
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        BaseEntity entity1 = new BaseEntity(1L) {};
        BaseEntity entity2 = new BaseEntity(1L) {};
        BaseEntity entity3 = new BaseEntity(2L) {};

        // Assert
        assertThat(entity1).isEqualTo(entity2);
        assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
        assertThat(entity1).isNotEqualTo(entity3);
    }

    @Test
    void testToString() {
        // Arrange
        BaseEntity entity = new BaseEntity(1L) {};

        // Act
        String toString = entity.toString();

        // Assert
        assertThat(toString).contains("BaseEntity");
        assertThat(toString).contains("id=1");
    }
}