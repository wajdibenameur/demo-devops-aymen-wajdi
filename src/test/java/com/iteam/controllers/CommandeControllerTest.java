package com.iteam.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // IMPORT AJOUTÉ
import com.iteam.entities.Commande;
import com.iteam.entities.Status;
import com.iteam.entities.User;
import com.iteam.entities.Product;
import com.iteam.exception.GlobalExceptionHandler;
import com.iteam.service.CommandeService;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires du contrôleur CommandeController")
class CommandeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommandeService commandeService;

    @InjectMocks
    private CommandeController commandeController;

    private ObjectMapper objectMapper;
    private Commande commande;
    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        // Configurer ObjectMapper avec JavaTimeModule pour LocalDateTime
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // IMPORTANT

        // Créer un utilisateur
        user = new User();
        user.setId(1L);
        user.setFirstName("Ahmed");
        user.setLastName("Ben Ali");
        user.setEmail("ahmed@email.com");

        // Créer un produit
        product = new Product();
        product.setId(1L);
        product.setNameProduct("Laptop");
        product.setPrice(1500.0);
        product.setQuantity(10);

        // Créer une commande
        commande = new Commande();
        commande.setId(1L);
        commande.setDateCommande(LocalDateTime.now());
        commande.setStatus(Status.En_attente);
        commande.setPriceTotale(1500.0);
        commande.setUser(user);
        commande.setProducts(Arrays.asList(product));

        // Initialiser MockMvc avec le contrôleur ET GlobalExceptionHandler
        mockMvc = MockMvcBuilders.standaloneSetup(commandeController)
                .setControllerAdvice(new GlobalExceptionHandler()) // IMPORTANT
                .build();
    }

    @Test
    @DisplayName("POST /api/ordres/create - Créer une commande")
    void createCommande_Success() throws Exception {
        // Arrange
        when(commandeService.createCommande(any(Commande.class))).thenReturn(commande);

        // Act & Assert
        mockMvc.perform(post("/api/ordres/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commande)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("En_attente"))
                .andExpect(jsonPath("$.priceTotale").value(1500.0));
    }

    @Test
    @DisplayName("GET /api/ordres - Liste toutes les commandes")
    void findAllCommandes_Success() throws Exception {
        // Arrange
        List<Commande> commandes = Arrays.asList(commande);
        when(commandeService.findAll()).thenReturn(commandes);

        // Act & Assert
        mockMvc.perform(get("/api/ordres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("En_attente"));
    }

    @Test
    @DisplayName("GET /api/ordres/{id} - Trouver une commande par ID")
    void findCommandeById_Success() throws Exception {
        // Arrange
        when(commandeService.findCommandeById(1L)).thenReturn(commande);

        // Act & Assert
        mockMvc.perform(get("/api/ordres/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("En_attente"));
    }

    @Test
    @DisplayName("GET /api/ordres/{id} - Commande non trouvée")
    void findCommandeById_NotFound() throws Exception {
        // Arrange
        when(commandeService.findCommandeById(99L))
                .thenThrow(new RuntimeException("Commande not found"));

        // Act & Assert
        mockMvc.perform(get("/api/ordres/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Commande not found"));
    }

    @Test
    @DisplayName("PUT /api/ordres/{id} - Mettre à jour le statut d'une commande")
    void updateCommande_Success() throws Exception {
        // Arrange
        Commande updatedCommande = new Commande();
        updatedCommande.setStatus(Status.Livré);
        updatedCommande.setPriceTotale(2000.0);

        when(commandeService.updateCommande(eq(1L), any(Commande.class))).thenReturn(updatedCommande);

        // Act & Assert
        mockMvc.perform(put("/api/ordres/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCommande)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Livré"))
                .andExpect(jsonPath("$.priceTotale").value(2000.0));
    }

    @Test
    @DisplayName("PUT /api/ordres/{id} - Mettre à jour une commande (En_cours)")
    void updateCommande_EnCours() throws Exception {
        // Arrange
        Commande updatedCommande = new Commande();
        updatedCommande.setStatus(Status.En_cours);
        updatedCommande.setPriceTotale(1800.0);

        when(commandeService.updateCommande(eq(1L), any(Commande.class))).thenReturn(updatedCommande);

        // Act & Assert
        mockMvc.perform(put("/api/ordres/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCommande)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("En_cours"))
                .andExpect(jsonPath("$.priceTotale").value(1800.0));
    }

    @Test
    @DisplayName("PUT /api/ordres/{id} - Mettre à jour une commande (Annulé)")
    void updateCommande_Annule() throws Exception {
        // Arrange
        Commande updatedCommande = new Commande();
        updatedCommande.setStatus(Status.Annulé);
        updatedCommande.setPriceTotale(0.0);

        when(commandeService.updateCommande(eq(1L), any(Commande.class))).thenReturn(updatedCommande);

        // Act & Assert
        mockMvc.perform(put("/api/ordres/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCommande)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Annulé"))
                .andExpect(jsonPath("$.priceTotale").value(0.0));
    }

    @Test
    @DisplayName("DELETE /api/ordres/{id} - Supprimer une commande")
    void deleteCommande_Success() throws Exception {
        // Arrange
        doNothing().when(commandeService).deleteCommande(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/ordres/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/ordres/{id} - Commande non trouvée")
    void deleteCommande_NotFound() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Commande not found")).when(commandeService).deleteCommande(99L);

        // Act & Assert
        mockMvc.perform(delete("/api/ordres/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Commande not found"));
    }

    @Test
    @DisplayName("POST /api/ordres/create - Créer commande avec différents statuts")
    void createCommande_WithDifferentStatus() throws Exception {
        // Test avec statut En_cours
        Commande commandeEnCours = new Commande();
        commandeEnCours.setDateCommande(LocalDateTime.now());
        commandeEnCours.setStatus(Status.En_cours);
        commandeEnCours.setPriceTotale(1500.0);
        commandeEnCours.setUser(user);
        commandeEnCours.setProducts(Arrays.asList(product));

        when(commandeService.createCommande(any(Commande.class))).thenReturn(commandeEnCours);

        mockMvc.perform(post("/api/ordres/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commandeEnCours)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("En_cours"));

        // Test avec statut Livré
        Commande commandeLivree = new Commande();
        commandeLivree.setDateCommande(LocalDateTime.now());
        commandeLivree.setStatus(Status.Livré);
        commandeLivree.setPriceTotale(1500.0);
        commandeLivree.setUser(user);
        commandeLivree.setProducts(Arrays.asList(product));

        when(commandeService.createCommande(any(Commande.class))).thenReturn(commandeLivree);

        mockMvc.perform(post("/api/ordres/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commandeLivree)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("Livré"));
    }

    @Test
    @DisplayName("GET /api/ordres - Liste vide")
    void findAllCommandes_EmptyList() throws Exception {
        // Arrange
        when(commandeService.findAll()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/ordres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}