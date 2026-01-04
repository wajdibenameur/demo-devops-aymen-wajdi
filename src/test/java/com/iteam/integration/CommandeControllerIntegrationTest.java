package com.iteam.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/schema.sql")
@DisplayName("Tests d'intégration CommandeController")
@Transactional
class CommandeControllerIntegrationTest {

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
    @DisplayName("CRUD complet via API REST - Commande")
    void fullCrudScenario() throws Exception {
        // CREATE
        Commande newCommande = new Commande();
        newCommande.setDateCommande(LocalDateTime.now());
        newCommande.setStatus(Status.En_attente);
        newCommande.setPriceTotale(100.0);
        newCommande.setUser(user);
        newCommande.setProducts(Arrays.asList(product));

        mockMvc.perform(post("/api/ordres/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommande)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("En_attente"))
                .andExpect(jsonPath("$.priceTotale").value(100.0));

        // Vérifier en base
        assertThat(commandeRepository.count()).isEqualTo(1);

        // READ ALL
        mockMvc.perform(get("/api/ordres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        // READ BY ID
        Long commandeId = commandeRepository.findAll().get(0).getId();

        mockMvc.perform(get("/api/ordres/" + commandeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("En_attente"));

        // UPDATE
        Commande updatedCommande = new Commande();
        updatedCommande.setStatus(Status.Livré);
        updatedCommande.setPriceTotale(150.0);
        updatedCommande.setUser(user);
        updatedCommande.setProducts(Arrays.asList(product));

        mockMvc.perform(put("/api/ordres/" + commandeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCommande)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Livré"))
                .andExpect(jsonPath("$.priceTotale").value(150.0));

        // DELETE
        mockMvc.perform(delete("/api/ordres/" + commandeId))
                .andExpect(status().isNoContent());

        assertThat(commandeRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("GET /api/ordres - Liste vide")
    void findAllCommandes_EmptyList() throws Exception {
        mockMvc.perform(get("/api/ordres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/ordres/{id} - Commande non trouvée")
    void findCommandeById_NotFound() throws Exception {
        mockMvc.perform(get("/api/ordres/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Commande not found"));
    }

    @Test
    @DisplayName("Création de commandes avec différents statuts")
    void createCommandesWithDifferentStatus() throws Exception {
        // Créer commande En_attente
        Commande commande1 = new Commande();
        commande1.setDateCommande(LocalDateTime.now());
        commande1.setStatus(Status.En_attente);
        commande1.setPriceTotale(100.0);
        commande1.setUser(user);
        commande1.setProducts(Arrays.asList(product));

        mockMvc.perform(post("/api/ordres/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commande1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("En_attente"));

        // Créer commande En_cours
        Commande commande2 = new Commande();
        commande2.setDateCommande(LocalDateTime.now());
        commande2.setStatus(Status.En_cours);
        commande2.setPriceTotale(200.0);
        commande2.setUser(user);
        commande2.setProducts(Arrays.asList(product));

        mockMvc.perform(post("/api/ordres/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commande2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("En_cours"));

        // Créer commande Livré
        Commande commande3 = new Commande();
        commande3.setDateCommande(LocalDateTime.now());
        commande3.setStatus(Status.Livré);
        commande3.setPriceTotale(300.0);
        commande3.setUser(user);
        commande3.setProducts(Arrays.asList(product));

        mockMvc.perform(post("/api/ordres/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commande3)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("Livré"));

        // Vérifier le compte
        assertThat(commandeRepository.count()).isEqualTo(3);

        // Vérifier la liste
        mockMvc.perform(get("/api/ordres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @DisplayName("Mise à jour d'une commande (Annulé)")
    void updateCommandeToAnnule() throws Exception {
        // Créer d'abord une commande
        Commande commande = new Commande();
        commande.setDateCommande(LocalDateTime.now());
        commande.setStatus(Status.En_attente);
        commande.setPriceTotale(100.0);
        commande.setUser(user);
        commande.setProducts(Arrays.asList(product));

        // Sauvegarder via repository
        Commande savedCommande = commandeRepository.save(commande);
        Long commandeId = savedCommande.getId();

        // Mettre à jour pour Annulé
        Commande updatedCommande = new Commande();
        updatedCommande.setStatus(Status.Annulé);
        updatedCommande.setPriceTotale(0.0);
        updatedCommande.setUser(user);
        updatedCommande.setProducts(Arrays.asList(product));

        mockMvc.perform(put("/api/ordres/" + commandeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCommande)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Annulé"))
                .andExpect(jsonPath("$.priceTotale").value(0.0));
    }
}