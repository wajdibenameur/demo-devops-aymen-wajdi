package com.iteam.entities;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EntitiesRelationsTest {

    @Test
    void testUserCommandeRelation() {
        // Arrange
        User user = new User("John", "Doe", "john@email.com", "12345678");

        Commande commande = new Commande();
        commande.setUser(user);

        // Act
        user.setCommandes(Arrays.asList(commande));

        // Assert
        assertThat(commande.getUser()).isEqualTo(user);
        assertThat(user.getCommandes()).contains(commande);
    }

    @Test
    void testCommandeProductRelation() {
        // Arrange
        Product laptop = new Product("Laptop", 1500.0, 10);
        Product mouse = new Product("Mouse", 50.0, 20);

        Commande commande = new Commande();
        commande.setProducts(Arrays.asList(laptop, mouse));

        // Assert
        assertThat(commande.getProducts()).hasSize(2);
        assertThat(commande.getProducts()).contains(laptop, mouse);
    }
}