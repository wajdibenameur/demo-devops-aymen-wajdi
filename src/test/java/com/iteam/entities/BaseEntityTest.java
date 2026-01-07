package com.iteam.entities;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BaseEntityTest {

    // Création d'une classe concrète pour le test
    @lombok.ToString(callSuper = true)
    static class TestEntity extends BaseEntity {
        public TestEntity() {
            super();
        }

        public TestEntity(Long id) {
            super(id);
        }
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        // Arrange & Act
        TestEntity entity = new TestEntity();
        entity.setId(1L);

        // Assert
        assertThat(entity.getId()).isEqualTo(1L);
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange & Act
        TestEntity entity = new TestEntity(1L);

        // Assert
        assertThat(entity.getId()).isEqualTo(1L);
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        TestEntity entity1 = new TestEntity(1L);
        TestEntity entity2 = new TestEntity(1L);
        TestEntity entity3 = new TestEntity(2L);

        // Assert
        assertThat(entity1).isEqualTo(entity2);
        assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
        assertThat(entity1).isNotEqualTo(entity3);
        assertThat(entity1.hashCode()).isNotEqualTo(entity3.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        TestEntity entity = new TestEntity(1L);

        // Act
        String toString = entity.toString();

        // Assert - Maintenant cela affichera "BaseEntityTest$TestEntity(id=1)"
        assertThat(toString).contains("id=1");
        // Vous pouvez ajuster l'assertion pour être plus flexible :
        assertThat(toString).contains("TestEntity");
        assertThat(toString).contains("id=1");
    }

    @Test
    void testNullId() {
        // Arrange
        TestEntity entity = new TestEntity();

        // Assert
        assertThat(entity.getId()).isNull();

        // Test toString avec id null
        String toString = entity.toString();
        assertThat(toString).contains("TestEntity");
        assertThat(toString).contains("id=null");
    }
}