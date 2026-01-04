package com.iteam.entities;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    @Test
    void testNoArgsConstructor() {
        // Arrange & Act
        Product product = new Product();

        // Assert
        assertThat(product).isNotNull();
        assertThat(product.getNameProduct()).isNull();
        assertThat(product.getPrice()).isNull();
        assertThat(product.getQuantity()).isNull();
    }

    @Test
    void testConstructorWithoutId() {
        // Arrange & Act
        Product product = new Product("Laptop", 1500.0, 10);

        // Assert
        assertThat(product.getNameProduct()).isEqualTo("Laptop");
        assertThat(product.getPrice()).isEqualTo(1500.0);
        assertThat(product.getQuantity()).isEqualTo(10);
        assertThat(product.getId()).isNull();
    }

    @Test
    void testConstructorWithId() {
        // Arrange & Act
        Product product = new Product(1L, "Laptop", 1500.0, 10);

        // Assert
        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getNameProduct()).isEqualTo("Laptop");
        assertThat(product.getPrice()).isEqualTo(1500.0);
        assertThat(product.getQuantity()).isEqualTo(10);
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        Product product = new Product();

        // Act
        product.setId(1L);
        product.setNameProduct("Smartphone");
        product.setPrice(800.0);
        product.setQuantity(5);

        // Assert
        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getNameProduct()).isEqualTo("Smartphone");
        assertThat(product.getPrice()).isEqualTo(800.0);
        assertThat(product.getQuantity()).isEqualTo(5);
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Product product1 = new Product("Laptop", 1500.0, 10);
        product1.setId(1L);

        Product product2 = new Product("Laptop", 1500.0, 10);
        product2.setId(1L);

        Product product3 = new Product("Phone", 800.0, 20);
        product3.setId(2L);

        // Assert
        assertThat(product1).isEqualTo(product2);
        assertThat(product1.hashCode()).isEqualTo(product2.hashCode());
        assertThat(product1).isNotEqualTo(product3);
    }

    @Test
    void testToString() {
        // Arrange
        Product product = new Product("Laptop", 1500.0, 10);
        product.setId(1L);

        // Act
        String toString = product.toString();

        // Assert
        assertThat(toString).contains("Product");
        assertThat(toString).contains("nameProduct=Laptop");
        assertThat(toString).contains("price=1500.0");
        assertThat(toString).contains("quantity=10");
    }

    @Test
    void testInheritance() {
        // Arrange
        Product product = new Product();

        // Assert
        assertThat(product).isInstanceOf(BaseEntity.class);
    }
}