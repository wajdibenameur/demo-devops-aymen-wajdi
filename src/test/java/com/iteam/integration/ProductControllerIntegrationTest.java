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
@DisplayName("Tests d'intégration ProductController")
@Transactional
class ProductControllerIntegrationTest {

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
    @DisplayName("CRUD complet via API REST - Produit")
    void fullCrudScenario() throws Exception {
        // CREATE
        Product newProduct = new Product();
        newProduct.setNameProduct("Laptop");
        newProduct.setPrice(1500.0);
        newProduct.setQuantity(10);

        mockMvc.perform(post("/api/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nameProduct").value("Laptop"))
                .andExpect(jsonPath("$.price").value(1500.0))
                .andExpect(jsonPath("$.quantity").value(10));

        // Vérifier en base
        assertThat(productRepository.count()).isEqualTo(1);

        // READ ALL
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nameProduct").value("Laptop"));

        // READ BY ID
        Long productId = productRepository.findAll().get(0).getId();

        mockMvc.perform(get("/api/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nameProduct").value("Laptop"))
                .andExpect(jsonPath("$.price").value(1500.0));

        // UPDATE
        Product updatedProduct = new Product();
        updatedProduct.setNameProduct("Laptop Pro");
        updatedProduct.setPrice(2000.0);
        updatedProduct.setQuantity(15);

        mockMvc.perform(put("/api/products/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nameProduct").value("Laptop Pro"))
                .andExpect(jsonPath("$.price").value(2000.0))
                .andExpect(jsonPath("$.quantity").value(15));

        // Vérifier la mise à jour en base
        Product productFromDb = productRepository.findById(productId).orElseThrow();
        assertThat(productFromDb.getNameProduct()).isEqualTo("Laptop Pro");
        assertThat(productFromDb.getPrice()).isEqualTo(2000.0);
        assertThat(productFromDb.getQuantity()).isEqualTo(15);

        // DELETE
        mockMvc.perform(delete("/api/products/" + productId))
                .andExpect(status().isNoContent());

        // Vérifier la suppression
        assertThat(productRepository.count()).isEqualTo(0);
        assertThat(productRepository.findById(productId)).isEmpty();
    }

    @Test
    @DisplayName("GET /api/products - Liste vide")
    void getAllProducts_EmptyList() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/products/{id} - Produit non trouvé")
    void getProductById_NotFound() throws Exception {
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("PUT /api/products/{id} - Produit non trouvé")
    void updateProduct_NotFound() throws Exception {
        Product product = new Product();
        product.setNameProduct("Test");
        product.setPrice(100.0);
        product.setQuantity(5);

        mockMvc.perform(put("/api/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - Produit non trouvé")
    void deleteProduct_NotFound() throws Exception {
        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Création de plusieurs produits")
    void createMultipleProducts() throws Exception {
        // Créer premier produit
        Product product1 = new Product();
        product1.setNameProduct("Laptop");
        product1.setPrice(1500.0);
        product1.setQuantity(10);

        mockMvc.perform(post("/api/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product1)))
                .andExpect(status().isCreated());

        // Créer deuxième produit
        Product product2 = new Product();
        product2.setNameProduct("Phone");
        product2.setPrice(800.0);
        product2.setQuantity(20);

        mockMvc.perform(post("/api/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product2)))
                .andExpect(status().isCreated());

        // Vérifier le compte
        assertThat(productRepository.count()).isEqualTo(2);

        // Vérifier la liste
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nameProduct").value("Laptop"))
                .andExpect(jsonPath("$[1].nameProduct").value("Phone"));
    }

    @Test
    @DisplayName("Test avec des données minimales")
    void createProductWithMinimalData() throws Exception {
        Product product = new Product();
        product.setNameProduct("Test");
        product.setPrice(10.0);
        product.setQuantity(1);

        mockMvc.perform(post("/api/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nameProduct").value("Test"))
                .andExpect(jsonPath("$.price").value(10.0))
                .andExpect(jsonPath("$.quantity").value(1));
    }
}