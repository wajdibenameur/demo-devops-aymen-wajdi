package com.iteam.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteam.entities.Product;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Tests d'intégration Product")
@Transactional
public class ProdctIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("Créer un produit")
    void createProduct() throws Exception {
        // Arrange
        Product newProduct = new Product();
        newProduct.setNameProduct("Laptop");
        newProduct.setPrice(1500.0);
        newProduct.setQuantity(10);

        // Act & Assert
        mockMvc.perform(post("/api/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.Product").exists())
                .andExpect(jsonPath("$.Product.nameProduct").value("Laptop"))
                .andExpect(jsonPath("$.message").exists());

        // Vérifier en base
        assertThat(productRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Récupérer tous les produits")
    void getAllProducts() throws Exception {
        // Arrange - Créer un produit directement via repository
        Product product = new Product();
        product.setNameProduct("Test");
        product.setPrice(100.0);
        product.setQuantity(5);
        productRepository.save(product);

        // Act & Assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nameProduct").value("Test"));
    }

    @Test
    @DisplayName("Récupérer un produit par ID")
    void getProductById() throws Exception {
        // Arrange
        Product product = new Product();
        product.setNameProduct("Phone");
        product.setPrice(500.0);
        product.setQuantity(20);
        Product savedProduct = productRepository.save(product);

        // Act & Assert
        mockMvc.perform(get("/api/products/" + savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nameProduct").value("Phone"))
                .andExpect(jsonPath("$.price").value(500.0));
    }

    @Test
    @DisplayName("Supprimer un produit")
    void deleteProduct() throws Exception {
        // Arrange
        Product product = new Product();
        product.setNameProduct("ToDelete");
        product.setPrice(10.0);
        product.setQuantity(1);
        Product savedProduct = productRepository.save(product);

        // Act & Assert
        mockMvc.perform(delete("/api/products/" + savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product delete with success"))
                .andExpect(jsonPath("$.id").value(savedProduct.getId().intValue()));

        // Vérifier que le produit a été supprimé
        assertThat(productRepository.count()).isEqualTo(0);
    }
}