package com.iteam.repositories;

import com.iteam.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Sql(scripts = "/schema.sql")  // ← AJOUTEZ JUSTE CETTE LIGNE
@DisplayName("Tests du repository User")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    // ❌ SUPPRIMEZ COMPLÈTEMENT CE BLOC ❌
    // @BeforeEach
    // void setUp() {
    //     userRepository.deleteAllInBatch();  // ← PROBLÈME ICI !
    // }

    @Test
    @DisplayName("Doit sauvegarder et retrouver un utilisateur")
    void saveAndFind() {
        // Arrange
        User user = new User();
        user.setFirstName("Ahmed");
        user.setLastName("Ben Ali");
        user.setEmail("ahmed@email.com");
        user.setPhoneNumber("12345678");

        // Act
        User saved = userRepository.save(user);
        Optional<User> found = userRepository.findById(saved.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Ahmed");
        assertThat(found.get().getEmail()).isEqualTo("ahmed@email.com");
    }

    @Test
    @DisplayName("Doit retourner tous les utilisateurs")
    void findAll() {
        // Arrange
        User user1 = new User();
        user1.setFirstName("Ahmed");
        user1.setLastName("Ben Ali");
        user1.setEmail("ahmed@email.com");
        user1.setPhoneNumber("12345678");

        User user2 = new User();
        user2.setFirstName("Fatma");
        user2.setLastName("Ben Ahmed");
        user2.setEmail("fatma@email.com");
        user2.setPhoneNumber("87654321");

        userRepository.save(user1);
        userRepository.save(user2);

        // Act
        List<User> users = userRepository.findAll();

        // Assert
        assertThat(users).hasSize(2);
    }

    @Test
    @DisplayName("Doit supprimer un utilisateur")
    void deleteUser() {
        // Arrange
        User user = new User();
        user.setFirstName("Ahmed");
        user.setLastName("Ben Ali");
        user.setEmail("ahmed@email.com");
        user.setPhoneNumber("12345678");

        User savedUser = userRepository.save(user);

        // Act
        userRepository.deleteById(savedUser.getId());

        // Assert
        assertThat(userRepository.existsById(savedUser.getId())).isFalse();
    }

    @Test
    @DisplayName("Doit mettre à jour un utilisateur")
    void updateUser() {
        // Arrange
        User user = new User();
        user.setFirstName("Ahmed");
        user.setLastName("Ben Ali");
        user.setEmail("ahmed@email.com");
        user.setPhoneNumber("12345678");

        User savedUser = userRepository.save(user);

        // Act
        savedUser.setFirstName("Ahmed Updated");
        userRepository.save(savedUser);

        // Assert
        Optional<User> updated = userRepository.findById(savedUser.getId());
        assertThat(updated.get().getFirstName()).isEqualTo("Ahmed Updated");
    }
}