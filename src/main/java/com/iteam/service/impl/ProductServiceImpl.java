package com.iteam.service.impl;

import com.iteam.entities.Product;
import com.iteam.repositories.ProductRepository;
import com.iteam.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product findProductById(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public Product updateProduct(Long id, Product product)
    {
        Product updetedProduct = findProductById(id);
        updetedProduct.setNameProduct(product.getNameProduct());
        updetedProduct.setPrice(product.getPrice());
        updetedProduct.setQuantity(product.getQuantity());
        return productRepository.save(updetedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        Product deletedProduct = findProductById(id);
        productRepository.delete(deletedProduct);
    }
}
