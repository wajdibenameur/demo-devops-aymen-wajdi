package com.iteam.service;

import com.iteam.entities.User;
import com.iteam.repositories.UserRepository;
import com.iteam.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires de UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private User user2;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("Ahmed");
        user.setLastName("Ben Ali");
        user.setEmail("ahmed@email.com");
        user.setPhoneNumber("12345678");

        user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Fatma");
        user2.setLastName("Ben Ahmed");
        user2.setEmail("fatma@email.com");
        user2.setPhoneNumber("87654321");
    }

    @Test
    @DisplayName("Doit créer un utilisateur avec succès")
    void createUser_Success() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.createUser(user);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Ahmed");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Doit retourner tous les utilisateurs")
    void findAll_Success() {
        // Arrange
        List<User> users = Arrays.asList(user, user2);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> result = userService.findAll();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("Ahmed");
        assertThat(result.get(1).getFirstName()).isEqualTo("Fatma");
    }

    @Test
    @DisplayName("Doit trouver un utilisateur par ID")
    void findUserById_Found() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        User result = userService.findUserById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Ahmed");
    }

    @Test
    @DisplayName("Doit lancer une exception si utilisateur non trouvé")
    void findUserById_NotFound() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.findUserById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Doit supprimer un utilisateur")
    void deleteUserById_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(any(User.class));

        // Act
        userService.deleteUserById(1L);

        // Assert
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    @DisplayName("Doit mettre à jour un utilisateur existant")
    void updateUser_Success() {
        // Arrange
        User userToUpdate = new User();
        userToUpdate.setFirstName("Ahmed Updated");
        userToUpdate.setLastName("New Lastname");
        userToUpdate.setEmail("new@email.com");
        userToUpdate.setPhoneNumber("99999999");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(userToUpdate);

        // Act
        User result = userService.updateUser(1L, userToUpdate);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Ahmed Updated");
        verify(userRepository, times(1)).save(any(User.class));
    }
}