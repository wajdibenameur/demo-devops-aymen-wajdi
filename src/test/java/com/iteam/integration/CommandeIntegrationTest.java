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
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/schema.sql")
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
        // Nettoyer toutes les tables
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
        // 1. CREATE
        Commande newCommande = new Commande();
        newCommande.setDateCommande(LocalDateTime.now());
        newCommande.setStatus(Status.En_attente);
        newCommande.setPriceTotale(100.0);
        newCommande.setUser(user);

        // Utiliser ArrayList pour une liste modifiable
        ArrayList<Product> products = new ArrayList<>();
        products.add(product);
        newCommande.setProducts(products);

        mockMvc.perform(post("/api/ordres/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommande)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("En_attente"))
                .andExpect(jsonPath("$.priceTotale").value(100.0));

        // Vérifier en base
        assertThat(commandeRepository.count()).isEqualTo(1);

        // 2. READ ALL
        mockMvc.perform(get("/api/ordres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        // 3. READ BY ID
        Long commandeId = commandeRepository.findAll().get(0).getId();

        mockMvc.perform(get("/api/ordres/" + commandeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("En_attente"));

        // 4. UPDATE
        Commande updatedCommande = new Commande();
        updatedCommande.setStatus(Status.Livré);
        updatedCommande.setPriceTotale(150.0);
        updatedCommande.setUser(user);

        ArrayList<Product> updatedProducts = new ArrayList<>();
        updatedProducts.add(product);
        updatedCommande.setProducts(updatedProducts);

        mockMvc.perform(put("/api/ordres/" + commandeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCommande)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Livré"))
                .andExpect(jsonPath("$.priceTotale").value(150.0));

        // 5. DELETE
        mockMvc.perform(delete("/api/ordres/" + commandeId))
                .andExpect(status().isNoContent());

        assertThat(commandeRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Création de commande avec différents statuts")
    void createCommandeWithDifferentStatus() throws Exception {
        // Test avec statut En_attente
        Commande commandeEnAttente = new Commande();
        commandeEnAttente.setDateCommande(LocalDateTime.now());
        commandeEnAttente.setStatus(Status.En_attente);
        commandeEnAttente.setPriceTotale(100.0);
        commandeEnAttente.setUser(user);

        ArrayList<Product> products1 = new ArrayList<>();
        products1.add(product);
        commandeEnAttente.setProducts(products1);

        mockMvc.perform(post("/api/ordres/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commandeEnAttente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("En_attente"));

        // Test avec statut En_cours
        Commande commandeEnCours = new Commande();
        commandeEnCours.setDateCommande(LocalDateTime.now());
        commandeEnCours.setStatus(Status.En_cours);
        commandeEnCours.setPriceTotale(200.0);
        commandeEnCours.setUser(user);

        ArrayList<Product> products2 = new ArrayList<>();
        products2.add(product);
        commandeEnCours.setProducts(products2);

        mockMvc.perform(post("/api/ordres/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commandeEnCours)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("En_cours"));

        // Vérifier le compte
        assertThat(commandeRepository.count()).isEqualTo(2);

        // Vérifier la liste
        mockMvc.perform(get("/api/ordres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
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
    @DisplayName("DELETE /api/ordres/{id} - Commande non trouvée")
    void deleteCommande_NotFound() throws Exception {
        mockMvc.perform(delete("/api/ordres/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Commande not found"));
    }

    @Test
    @DisplayName("PUT /api/ordres/{id} - Commande non trouvée")
    void updateCommande_NotFound() throws Exception {
        Commande commande = new Commande();
        commande.setStatus(Status.Livré);
        commande.setPriceTotale(150.0);
        commande.setUser(user);

        ArrayList<Product> products = new ArrayList<>();
        products.add(product);
        commande.setProducts(products);

        mockMvc.perform(put("/api/ordres/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commande)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Commande not found"));
    }

    @Test
    @DisplayName("Test de performance: création de 20 commandes")
    void performanceTest() throws Exception {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 20; i++) {
            Commande commande = new Commande();
            commande.setDateCommande(LocalDateTime.now());
            commande.setStatus(Status.En_attente);
            commande.setPriceTotale(100.0 + i);
            commande.setUser(user);

            ArrayList<Product> products = new ArrayList<>();
            products.add(product);
            commande.setProducts(products);

            mockMvc.perform(post("/api/ordres/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(commande)))
                    .andExpect(status().isCreated());
        }

        long duration = System.currentTimeMillis() - start;
        assertThat(commandeRepository.count()).isEqualTo(20);
        assertThat(duration).isLessThan(10000); // < 10 secondes
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

        Commande newCommande = new Commande();
        newCommande.setDateCommande(LocalDateTime.now());
        newCommande.setStatus(Status.En_attente);
        newCommande.setPriceTotale(1500.0); // 100 + 800 = 900, mais on peut mettre autre chose
        newCommande.setUser(user);

        ArrayList<Product> products = new ArrayList<>();
        products.add(product);
        products.add(product2);
        newCommande.setProducts(products);

        mockMvc.perform(post("/api/ordres/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommande)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("En_attente"));

        assertThat(commandeRepository.count()).isEqualTo(1);

        // Vérifier que la commande a bien 2 produits
        Long commandeId = commandeRepository.findAll().get(0).getId();

        mockMvc.perform(get("/api/ordres/" + commandeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products.length()").value(2));
    }
}