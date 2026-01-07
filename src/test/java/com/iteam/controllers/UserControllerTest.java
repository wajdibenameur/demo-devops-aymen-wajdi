package com.iteam.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteam.Exceptions.NotFoundEntityExceptions;
import com.iteam.entities.User;
import com.iteam.handler.GlobalExceptionsHandler;
import com.iteam.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du contrôleur UserController")
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;
    private User user;
    private User user2;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

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

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionsHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/users - Liste des utilisateurs")
    void getAllUsers_Success() throws Exception {
        List<User> users = Arrays.asList(user, user2);
        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Ahmed"))
                .andExpect(jsonPath("$[1].firstName").value("Fatma"));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Utilisateur trouvé")
    void getUserById_Found() throws Exception {
        when(userService.findUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ahmed"))
                .andExpect(jsonPath("$.email").value("ahmed@email.com"));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Utilisateur non trouvé")
    void getUserById_NotFound() throws Exception {
        when(userService.findUserById(99L))
                .thenThrow(new NotFoundEntityExceptions("No User present with the ID: 99"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No User present with the ID: 99"))
                .andExpect(jsonPath("$.error").value("Ressources_Not_Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("POST /api/users/create - Créer un utilisateur")
    void createUser_Success() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.firstName").value("Ahmed"))
                .andExpect(jsonPath("$.user.email").value("ahmed@email.com"))
                .andExpect(jsonPath("$.message").value("User created successfully"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Mise à jour réussie")
    void updateUser_Success() throws Exception {
        User updatedUser = new User();
        updatedUser.setFirstName("Ahmed Updated");
        updatedUser.setLastName("New Lastname");
        updatedUser.setEmail("new@email.com");
        updatedUser.setPhoneNumber("99999999");

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ahmed Updated"))
                .andExpect(jsonPath("$.email").value("new@email.com"));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Suppression")
    void deleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUserById(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted with success"))
                .andExpect(jsonPath("$.id").value(1));
    }
}