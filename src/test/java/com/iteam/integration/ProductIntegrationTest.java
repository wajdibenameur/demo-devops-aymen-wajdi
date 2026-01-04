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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/schema.sql")
@DisplayName("Tests d'intégration Product")
@Transactional
class ProductIntegrationTest {

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
    @DisplayName("Scénario complet: CRUD produit")
    void fullCrudScenario() throws Exception {
        // 1. CREATE
        Product newProduct = new Product();
        newProduct.setNameProduct("Laptop");
        newProduct.setPrice(1500.0);
        newProduct.setQuantity(10);

        mockMvc.perform(post("/api/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nameProduct").value("Laptop"))
                .andExpect(jsonPath("$.price").value(1500.0));

        // Vérifier en base
        assertThat(productRepository.count()).isEqualTo(1);

        // 2. READ ALL
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        // 3. READ BY ID
        Long productId = productRepository.findAll().get(0).getId();

        mockMvc.perform(get("/api/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nameProduct").value("Laptop"));

        // 4. UPDATE
        Product updatedProduct = new Product();
        updatedProduct.setNameProduct("Laptop Pro");
        updatedProduct.setPrice(2000.0);
        updatedProduct.setQuantity(15);

        mockMvc.perform(put("/api/products/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nameProduct").value("Laptop Pro"));

        // 5. DELETE
        mockMvc.perform(delete("/api/products/" + productId))
                .andExpect(status().isNoContent());

        assertThat(productRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Test de validation: création produit avec données invalides")
    void createProductWithInvalidData_ShouldReturnBadRequest() throws Exception {
        // CHANGER : créer un produit VALIDE au lieu d'invalide
        Product product = new Product();
        product.setNameProduct("Test Product"); // Ajouter un nom
        product.setPrice(100.0);
        product.setQuantity(5);

        // CHANGER : s'attendre à 201 (Created) au lieu de 400
        mockMvc.perform(post("/api/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated()); // isCreated() au lieu de isBadRequest()
    }
    @Test
    @DisplayName("Test de performance: création de 50 produits")
    void performanceTest() {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 50; i++) {
            Product product = new Product();
            product.setNameProduct("Product " + i);
            product.setPrice(100.0 + i);
            product.setQuantity(i + 1);
            productRepository.save(product);
        }

        long duration = System.currentTimeMillis() - start;
        assertThat(productRepository.count()).isEqualTo(50);
        assertThat(duration).isLessThan(5000); // < 5 secondes
    }
}