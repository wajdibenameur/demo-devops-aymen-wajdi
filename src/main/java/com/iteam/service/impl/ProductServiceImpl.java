package com.iteam.service.impl;

import com.iteam.Exceptions.NotFoundEntityExceptions;
import com.iteam.entities.Product;
import com.iteam.repositories.ProductRepository;
import com.iteam.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
                .orElseThrow(()-> new NotFoundEntityExceptions("No Product present with ID : " + id));
    }

    @Override
    public Product updateProduct(Long id, Product product)
    {
        Optional<Product> existingProduct = productRepository.findById(id);
        if(!existingProduct.isPresent()){
            throw new NotFoundEntityExceptions("No Product present with ID : " + id);
        } else {
            Product updatedProduct = existingProduct.get();
            updatedProduct.setNameProduct(product.getNameProduct());
            updatedProduct.setPrice(product.getPrice());
            updatedProduct.setQuantity(product.getQuantity());
            return productRepository.save(updatedProduct);
        }

        /*Product updetedProduct = findProductById(id);
        updetedProduct.setNameProduct(product.getNameProduct());
        updetedProduct.setPrice(product.getPrice());
        updetedProduct.setQuantity(product.getQuantity());
        return productRepository.save(updetedProduct);*/
    }

    @Override
    public void deleteProduct(Long id) {

        Optional<Product> existingProduct = productRepository.findById(id);
        if(!existingProduct.isPresent()){
            throw new NotFoundEntityExceptions("No Product present with ID : " + id);
        } else {
            productRepository.delete(existingProduct.get());
        }

       // Product deletedProduct = findProductById(id);
       // productRepository.delete(deletedProduct);
    }
}
