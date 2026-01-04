package com.iteam.service.impl;

import com.iteam.entities.Commande;
import com.iteam.repositories.CommandeRepository;
import com.iteam.service.CommandeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandeServiceImpl implements CommandeService {

    private final CommandeRepository commandeRepository;

    @Override
    public List<Commande> findAll() {
        return commandeRepository.findAll();
    }

    @Override
    public Commande createCommande(Commande commande)
    {
        return commandeRepository.save(commande);
    }

    @Override
    public Commande findCommandeById(Long id) {

        return commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande not found"));
    }

    @Override
    public void deleteCommande(Long id) {
       Commande deletedCommande = findCommandeById(id);
       commandeRepository.delete(deletedCommande);
    }

    @Override
    public Commande updateCommande(Long id, Commande commande) {
        Commande updatedCommande = findCommandeById(id);

        // Mettre à jour uniquement les champs fournis (ne pas écraser les champs null)
        if (commande.getStatus() != null) {
            updatedCommande.setStatus(commande.getStatus());
        }
        // Ne pas changer l'utilisateur s'il n'est pas fourni
        if (commande.getUser() != null) {
            updatedCommande.setUser(commande.getUser());
        }
        // Ne pas changer les produits s'ils ne sont pas fournis
        if (commande.getProducts() != null && !commande.getProducts().isEmpty()) {
            updatedCommande.setProducts(commande.getProducts());
        }
        if (commande.getPriceTotale() != null) {
            updatedCommande.setPriceTotale(commande.getPriceTotale());
        }
        if (commande.getDateCommande() != null) {
            updatedCommande.setDateCommande(commande.getDateCommande());
        }

        return commandeRepository.save(updatedCommande);
    }
}
