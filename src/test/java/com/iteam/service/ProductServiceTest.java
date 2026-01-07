package com.iteam.service;
import com.iteam.Exceptions.NotFoundEntityExceptions;
import com.iteam.entities.Product;
import com.iteam.repositories.ProductRepository;
import com.iteam.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires de ProductService")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setNameProduct("Laptop");
        product.setPrice(1500.0);
        product.setQuantity(10);
    }

    @Test
    @DisplayName("findAll() - Doit retourner tous les produits")
    void findAll_ShouldReturnAllProducts() {
        // Arrange
        List<Product> products = Arrays.asList(
                new Product(1L, "Laptop", 1500.0, 10),
                new Product(2L, "Phone", 800.0, 20)
        );
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = productService.findAll();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNameProduct()).isEqualTo("Laptop");
        assertThat(result.get(1).getPrice()).isEqualTo(800.0);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("createProduct() - Doit créer un produit avec succès")
    void createProduct_ShouldSaveProduct() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product result = productService.createProduct(product);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getNameProduct()).isEqualTo("Laptop");
        assertThat(result.getPrice()).isEqualTo(1500.0);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("findProductById() - Doit retourner un produit existant")
    void findProductById_ShouldReturnProduct_WhenProductExists() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        Product result = productService.findProductById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getNameProduct()).isEqualTo("Laptop");
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findProductById() - Doit lancer une exception quand produit non trouvé")
    void findProductById_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.findProductById(99L))
                .isInstanceOf(NotFoundEntityExceptions.class)
                .hasMessage("No Product present with ID : 99"); // Message EXACT
        verify(productRepository, times(1)).findById(99L);
    }
    @Test
    @DisplayName("updateProduct() - Doit mettre à jour un produit existant")
    void updateProduct_ShouldUpdateProduct_WhenProductExists() {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setNameProduct("Laptop Pro");
        updatedProduct.setPrice(2000.0);
        updatedProduct.setQuantity(15);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        Product result = productService.updateProduct(1L, updatedProduct);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getNameProduct()).isEqualTo("Laptop Pro");
        assertThat(result.getPrice()).isEqualTo(2000.0);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("deleteProduct() - Doit supprimer un produit existant")
    void deleteProduct_ShouldDeleteProduct_WhenProductExists() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(any(Product.class));

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(product);
    }
}