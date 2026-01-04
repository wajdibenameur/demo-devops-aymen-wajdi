package com.iteam.controllers;

import com.iteam.entities.Product;
import com.iteam.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.TableGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@Tag(name = "products" , description = "Gestion des Produits")
public class ProductController {

    private final ProductService productService;


    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Create Product
    @Operation(summary = "Create a Product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201" , description = "product created"),
            @ApiResponse(responseCode = "400" , description = "invalid request")

    })
    @PostMapping("/create")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.createProduct(product);
        return  ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }
    // Get All Product
    @Operation(summary = "Get all Product")
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }
    // Get Product By Id
    @Operation(summary = "Get a Product By Id")
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(productService.findProductById(id));
    }
    // Update Product
    @Operation(summary = "Updatee a Product")
    @ApiResponse(responseCode = "404" , description = "Product Not found")
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable(name = "id") Long id,
                                                 @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }
    // Delete Product
    @Operation(summary = "Delete a Product")
    @DeleteMapping("/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable(name = "id") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); // 204
    }



    // Gestion globale des exceptions (ajouté)
//    @exceptionhandler(runtimeexception.class)
//    public responseentity<map<string, string>> handleruntimeexception(runtimeexception e) {
//        map<string, string> error = new hashmap<>();
//        error.put("message", e.getmessage());
//        error.put("status", "not_found");
//        return responseentity.status(httpstatus.not_found).body(error);
//    }
//








}
