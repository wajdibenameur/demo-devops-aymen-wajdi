package com.iteam.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
@Entity
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Product extends BaseEntity {

    @Column(nullable = false)
    private String nameProduct;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer quantity;

    // Constructeur par défaut (généré par @NoArgsConstructor)

    // Constructeur pratique pour les tests
    public Product(String nameProduct, Double price, Integer quantity) {
        this.nameProduct = nameProduct;
        this.price = price;
        this.quantity = quantity;
    }

    // Constructeur pour mettre à jour
    public Product(Long id, String nameProduct, Double price, Integer quantity) {
        this.setId(id);
        this.nameProduct = nameProduct;
        this.price = price;
        this.quantity = quantity;
    }
}