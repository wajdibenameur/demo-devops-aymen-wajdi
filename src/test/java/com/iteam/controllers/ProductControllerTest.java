package com.iteam.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteam.entities.Product;
import com.iteam.repositories.ProductRepository;
import org.junit.jupiter.api.*;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class productIntegrationTest {

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
    @Order(1)
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
                .andExpect(jsonPath("$.Product.nameProduct").value("Laptop"))  // CORRECTION: $.Product.nameProduct
                .andExpect(jsonPath("$.Product.price").value(1500.0))
                .andExpect(jsonPath("$.Product.quantity").value(10))
                .andExpect(jsonPath("$.message").value("Created Product successfully"));

        // Vérifier en base
        assertThat(productRepository.count()).isEqualTo(1);

        // 2. READ ALL
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nameProduct").value("Laptop"));

        // 3. READ BY ID
        Long productId = productRepository.findAll().get(0).getId();

        mockMvc.perform(get("/api/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nameProduct").value("Laptop"))
                .andExpect(jsonPath("$.price").value(1500.0));

        // 4. UPDATE
        Product updatedProduct = new Product();
        updatedProduct.setNameProduct("Laptop Pro");
        updatedProduct.setPrice(2000.0);
        updatedProduct.setQuantity(15);

        mockMvc.perform(put("/api/products/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nameProduct").value("Laptop Pro"))
                .andExpect(jsonPath("$.price").value(2000.0));

        // 5. DELETE
        mockMvc.perform(delete("/api/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product delete with success"))
                .andExpect(jsonPath("$.id").value(productId.intValue()));

        assertThat(productRepository.count()).isEqualTo(0);
    }
}