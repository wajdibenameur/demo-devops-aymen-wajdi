package com.iteam.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Commande extends  BaseEntity{



    private LocalDateTime dateCommande;
    @Enumerated(EnumType.STRING)
    private Status status;
    private Double priceTotale;

    @ManyToOne(optional = false)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "commande_products",
            joinColumns = @JoinColumn(name = "commande_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;








}
