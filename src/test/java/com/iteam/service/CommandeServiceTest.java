package com.iteam.service.impl;

import com.iteam.entities.Commande;
import com.iteam.entities.Status;
import com.iteam.entities.User;
import com.iteam.entities.Product;
import com.iteam.repositories.CommandeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires du service Commande")
class CommandeServiceImplTest {

    @Mock
    private CommandeRepository commandeRepository;

    @InjectMocks
    private CommandeServiceImpl commandeService;

    private Commande commande;
    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
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
    }

    @Test
    @DisplayName("Trouver toutes les commandes")
    void findAll_ShouldReturnAllCommandes() {
        // Arrange
        List<Commande> commandes = Arrays.asList(commande);
        when(commandeRepository.findAll()).thenReturn(commandes);

        // Act
        List<Commande> result = commandeService.findAll();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(Status.En_attente);
        verify(commandeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Créer une commande")
    void createCommande_ShouldSaveAndReturnCommande() {
        // Arrange
        when(commandeRepository.save(any(Commande.class))).thenReturn(commande);

        // Act
        Commande result = commandeService.createCommande(commande);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(Status.En_attente);
        assertThat(result.getPriceTotale()).isEqualTo(1500.0);
        verify(commandeRepository, times(1)).save(commande);
    }

    @Test
    @DisplayName("Trouver une commande par ID - Succès")
    void findCommandeById_WhenExists_ShouldReturnCommande() {
        // Arrange
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));

        // Act
        Commande result = commandeService.findCommandeById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(Status.En_attente);
        verify(commandeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Trouver une commande par ID - Non trouvée")
    void findCommandeById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(commandeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> commandeService.findCommandeById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Commande not found");
        verify(commandeRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Supprimer une commande")
    void deleteCommande_ShouldDelete() {
        // Arrange
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        doNothing().when(commandeRepository).delete(commande);

        // Act
        commandeService.deleteCommande(1L);

        // Assert
        verify(commandeRepository, times(1)).findById(1L);
        verify(commandeRepository, times(1)).delete(commande);
    }

    @Test
    @DisplayName("Mettre à jour une commande")
    void updateCommande_ShouldUpdateAndSave() {
        // Arrange
        Commande updatedCommande = new Commande();
        updatedCommande.setStatus(Status.Livré);
        updatedCommande.setPriceTotale(2000.0);
        updatedCommande.setUser(user);
        updatedCommande.setProducts(Arrays.asList(product));

        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(commandeRepository.save(any(Commande.class))).thenReturn(updatedCommande);

        // Act
        Commande result = commandeService.updateCommande(1L, updatedCommande);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Status.Livré);
        assertThat(result.getPriceTotale()).isEqualTo(2000.0);
        verify(commandeRepository, times(1)).findById(1L);
        verify(commandeRepository, times(1)).save(any(Commande.class));
    }

    @Test
    @DisplayName("Mettre à jour une commande avec statut En_cours")
    void updateCommande_WithEnCoursStatus() {
        // Arrange
        Commande updatedCommande = new Commande();
        updatedCommande.setStatus(Status.En_cours);
        updatedCommande.setPriceTotale(1800.0);
        updatedCommande.setUser(user);
        updatedCommande.setProducts(Arrays.asList(product));

        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(commandeRepository.save(any(Commande.class))).thenReturn(updatedCommande);

        // Act
        Commande result = commandeService.updateCommande(1L, updatedCommande);

        // Assert
        assertThat(result.getStatus()).isEqualTo(Status.En_cours);
        assertThat(result.getPriceTotale()).isEqualTo(1800.0);
    }

    @Test
    @DisplayName("Mettre à jour une commande avec statut Annulé")
    void updateCommande_WithAnnuleStatus() {
        // Arrange
        Commande updatedCommande = new Commande();
        updatedCommande.setStatus(Status.Annulé);
        updatedCommande.setPriceTotale(0.0);
        updatedCommande.setUser(user);
        updatedCommande.setProducts(Arrays.asList(product));

        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(commandeRepository.save(any(Commande.class))).thenReturn(updatedCommande);

        // Act
        Commande result = commandeService.updateCommande(1L, updatedCommande);

        // Assert
        assertThat(result.getStatus()).isEqualTo(Status.Annulé);
        assertThat(result.getPriceTotale()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Liste vide de commandes")
    void findAll_WhenNoCommandes_ShouldReturnEmptyList() {
        // Arrange
        when(commandeRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Commande> result = commandeService.findAll();

        // Assert
        assertThat(result).isEmpty();
        verify(commandeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Créer une commande avec différents statuts")
    void createCommande_WithDifferentStatus() {
        // Test avec statut En_cours
        Commande commandeEnCours = new Commande();
        commandeEnCours.setDateCommande(LocalDateTime.now());
        commandeEnCours.setStatus(Status.En_cours);
        commandeEnCours.setPriceTotale(1500.0);
        commandeEnCours.setUser(user);
        commandeEnCours.setProducts(Arrays.asList(product));

        when(commandeRepository.save(any(Commande.class))).thenReturn(commandeEnCours);

        Commande result = commandeService.createCommande(commandeEnCours);
        assertThat(result.getStatus()).isEqualTo(Status.En_cours);

        // Test avec statut Livré
        Commande commandeLivree = new Commande();
        commandeLivree.setDateCommande(LocalDateTime.now());
        commandeLivree.setStatus(Status.Livré);
        commandeLivree.setPriceTotale(1500.0);
        commandeLivree.setUser(user);
        commandeLivree.setProducts(Arrays.asList(product));

        when(commandeRepository.save(any(Commande.class))).thenReturn(commandeLivree);

        result = commandeService.createCommande(commandeLivree);
        assertThat(result.getStatus()).isEqualTo(Status.Livré);
    }
}