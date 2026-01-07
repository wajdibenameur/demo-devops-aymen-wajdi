package com.iteam.service;

import com.iteam.entities.Commande;

import java.util.List;

public interface CommandeService {


    List<Commande> findAll();
    Commande createCommande(Long userId,List<Long> productsId);
    Commande findCommandeById(Long id);
    void deleteCommande(Long id);
    Commande updateCommande(Long id,Commande commande);





}
