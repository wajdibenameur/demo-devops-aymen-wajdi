package com.iteam.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteam.dto.CreateCommandeRequestDTO;
import com.iteam.entities.Commande;
import com.iteam.entities.Status;
import com.iteam.entities.User;
import com.iteam.entities.Product;
import com.iteam.repositories.CommandeRepository;
import com.iteam.repositories.UserRepository;
import com.iteam.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Tests d'intégration Commande")
@Transactional
class CommandeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        commandeRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        // Créer un utilisateur pour les tests
        user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@email.com");
        user.setPhoneNumber("12345678");
        user = userRepository.save(user);

        // Créer un produit pour les tests
        product = new Product();
        product.setNameProduct("Test Product");
        product.setPrice(100.0);
        product.setQuantity(5);
        product = productRepository.save(product);
    }

    @Test
    @DisplayName("Scénario complet: CRUD commande")
    void fullCrudScenario() throws Exception {
        // 1. CREATE - Utiliser CreateCommandeRequestDTO, pas Commande
        CreateCommandeRequestDTO requestDTO = new CreateCommandeRequestDTO();
        requestDTO.setUserId(user.getId());
        requestDTO.setProductsId(Arrays.asList(product.getId()));

        mockMvc.perform(post("/api/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Order create with success"))
                .andExpect(jsonPath("$['orders'].status").value("En_attente"))
                .andExpect(jsonPath("$['orders'].priceTotale").value(100.0));

        // Vérifier en base
        assertThat(commandeRepository.count()).isEqualTo(1);

        // 2. READ ALL
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        // 3. READ BY ID
        Long commandeId = commandeRepository.findAll().get(0).getId();

        mockMvc.perform(get("/api/orders/" + commandeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("En_attente"));

        // 4. UPDATE
        Commande updatedCommande = new Commande();
        updatedCommande.setStatus(Status.Livré);
        updatedCommande.setPriceTotale(150.0);

        mockMvc.perform(put("/api/orders/" + commandeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCommande)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Update Orders Successufully"))
                .andExpect(jsonPath("$.orders.status").value("Livré"));

        // 5. DELETE - Retourne 200 avec message
        mockMvc.perform(delete("/api/orders/" + commandeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("order delete with success"));

        assertThat(commandeRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("GET /api/orders - Liste vide")
    void findAllCommandes_EmptyList() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/orders/{id} - Commande non trouvée")
    void findCommandeById_NotFound() throws Exception {
        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Commande with ID : 999 not found"));
    }

    @Test
    @DisplayName("DELETE /api/orders/{id} - Commande non trouvée")
    void deleteCommande_NotFound() throws Exception {
        mockMvc.perform(delete("/api/orders/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No Orders with the ID: 999"));
    }

    @Test
    @DisplayName("Création de commande avec plusieurs produits")
    void createCommandeWithMultipleProducts() throws Exception {
        // Créer un deuxième produit
        Product product2 = new Product();
        product2.setNameProduct("Phone");
        product2.setPrice(800.0);
        product2.setQuantity(15);
        product2 = productRepository.save(product2);

        CreateCommandeRequestDTO requestDTO = new CreateCommandeRequestDTO();
        requestDTO.setUserId(user.getId());
        requestDTO.setProductsId(Arrays.asList(product.getId(), product2.getId()));

        mockMvc.perform(post("/api/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Order create with success"));
    }
}