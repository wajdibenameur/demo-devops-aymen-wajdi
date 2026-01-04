package com.iteam.controllers;
import com.iteam.exception.GlobalExceptionHandler; // IMPORT AJOUTÉ
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteam.entities.Product;
import com.iteam.service.ProductService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires du contrôleur ProductController")
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ObjectMapper objectMapper;
    private Product product;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        product = new Product();
        product.setId(1L);
        product.setNameProduct("Laptop");
        product.setPrice(1500.0);
        product.setQuantity(10);
        // Initialiser MockMvc avec le contrôleur ET GlobalExceptionHandler
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(new GlobalExceptionHandler()) // IMPORTANT
                .build();

    }

    @Test
    @DisplayName("POST /api/products/create - Créer un produit avec succès")
    void createProduct_ShouldReturnCreated() throws Exception {
        // Arrange
        when(productService.createProduct(any(Product.class))).thenReturn(product);

        // Act & Assert
        mockMvc.perform(post("/api/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nameProduct", is("Laptop")))
                .andExpect(jsonPath("$.price", is(1500.0)))
                .andExpect(jsonPath("$.quantity", is(10)));
    }

    @Test
    @DisplayName("GET /api/products - Récupérer tous les produits")
    void getAllProducts_ShouldReturnAllProducts() throws Exception {
        // Arrange
        List<Product> products = Arrays.asList(
                product,
                new Product(2L, "Phone", 800.0, 20)
        );
        when(productService.findAll()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nameProduct", is("Laptop")))
                .andExpect(jsonPath("$[1].nameProduct", is("Phone")));
    }

    @Test
    @DisplayName("GET /api/products/{id} - Récupérer un produit par ID")
    void getProductById_ShouldReturnProduct() throws Exception {
        // Arrange
        when(productService.findProductById(1L)).thenReturn(product);

        // Act & Assert
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nameProduct", is("Laptop")))
                .andExpect(jsonPath("$.price", is(1500.0)));
    }

    @Test
    @DisplayName("GET /api/products/{id} - Produit non trouvé")
    void getProductById_NotFound() throws Exception {
        // Arrange
        when(productService.findProductById(99L))
                .thenThrow(new RuntimeException("Product not found"));

        // Act & Assert - Maintenant avec GlobalExceptionHandler, ça renvoie 404
        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound()) // CHANGÉ : isNotFound() au lieu de is5xxServerError()
                .andExpect(jsonPath("$.message").value("Product not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
    @Test
    @DisplayName("PUT /api/products/{id} - Mettre à jour un produit")
    void updateProduct_ShouldUpdateProduct() throws Exception {
        // Arrange
        Product updatedProduct = new Product(1L, "Laptop Pro", 2000.0, 15);
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);

        // Act & Assert
        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nameProduct", is("Laptop Pro")))
                .andExpect(jsonPath("$.price", is(2000.0)));
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - Supprimer un produit")
    void deleteProduct_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(productService).deleteProduct(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/products/create - Données valides simples")
    void createProduct_WithMinimalData() throws Exception {
        // Arrange
        Product minimalProduct = new Product();
        minimalProduct.setNameProduct("Test");
        minimalProduct.setPrice(10.0);
        minimalProduct.setQuantity(1);

        when(productService.createProduct(any(Product.class))).thenReturn(minimalProduct);

        // Act & Assert
        mockMvc.perform(post("/api/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(minimalProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nameProduct", is("Test")))
                .andExpect(jsonPath("$.price", is(10.0)));
    }
}