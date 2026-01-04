package com.iteam.service;

import com.iteam.entities.Product;

import java.util.List;

public interface ProductService {


    List<Product> findAll();
    Product createProduct(Product product);
    Product findProductById(Long id);
    Product updateProduct(Long id,Product product);
    void deleteProduct(Long id);






}
