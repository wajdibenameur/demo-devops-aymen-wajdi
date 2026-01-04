package com.iteam.entities;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void testNoArgsConstructor() {
        // Arrange & Act
        User user = new User();

        // Assert
        assertThat(user).isNotNull();
        assertThat(user.getCommandes()).isNull(); // Lombok initialise à null
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        List<Commande> commandes = new ArrayList<>();

        // Act
        User user = new User("John", "Doe", "john@email.com", "12345678", commandes);

        // Assert
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getEmail()).isEqualTo("john@email.com");
        assertThat(user.getPhoneNumber()).isEqualTo("12345678");
        assertThat(user.getCommandes()).isEqualTo(commandes);
        assertThat(user.getId()).isNull(); // Non défini dans ce constructeur
    }

    @Test
    void testCustomConstructor() {
        // Arrange & Act
        User user = new User("Jane", "Doe", "jane@email.com", "87654321");

        // Assert
        assertThat(user.getFirstName()).isEqualTo("Jane");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getEmail()).isEqualTo("jane@email.com");
        assertThat(user.getPhoneNumber()).isEqualTo("87654321");
        assertThat(user.getCommandes()).isNull();
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        User user = new User();
        List<Commande> commandes = new ArrayList<>();

        // Act
        user.setId(1L);
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setEmail("alice@email.com");
        user.setPhoneNumber("555-1234");
        user.setCommandes(commandes);

        // Assert
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getFirstName()).isEqualTo("Alice");
        assertThat(user.getLastName()).isEqualTo("Smith");
        assertThat(user.getEmail()).isEqualTo("alice@email.com");
        assertThat(user.getPhoneNumber()).isEqualTo("555-1234");
        assertThat(user.getCommandes()).isEqualTo(commandes);
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        User user1 = new User("John", "Doe", "john@email.com", "12345678");
        user1.setId(1L);

        User user2 = new User("John", "Doe", "john@email.com", "12345678");
        user2.setId(1L);

        User user3 = new User("Jane", "Doe", "jane@email.com", "87654321");
        user3.setId(2L);

        // Assert
        assertThat(user1).isEqualTo(user2);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
        assertThat(user1).isNotEqualTo(user3);
    }

    @Test
    void testToString() {
        // Arrange
        User user = new User("John", "Doe", "john@email.com", "12345678");
        user.setId(1L);

        // Act
        String toString = user.toString();

        // Assert
        assertThat(toString).contains("User");
        assertThat(toString).contains("firstName=John");
        assertThat(toString).contains("lastName=Doe");
        assertThat(toString).contains("email=john@email.com");
        assertThat(toString).contains("phoneNumber=12345678");
    }

    @Test
    void testInheritance() {
        // Arrange
        User user = new User();

        // Assert
        assertThat(user).isInstanceOf(BaseEntity.class);
    }
}