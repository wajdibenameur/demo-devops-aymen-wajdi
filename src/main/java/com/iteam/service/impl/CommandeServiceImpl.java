package com.iteam.service.impl;

import com.iteam.Exceptions.NotFoundEntityExceptions;
import com.iteam.entities.Commande;
import com.iteam.entities.Product;
import com.iteam.entities.Status;
import com.iteam.entities.User;
import com.iteam.repositories.CommandeRepository;
import com.iteam.repositories.ProductRepository;
import com.iteam.repositories.UserRepository;
import com.iteam.service.CommandeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommandeServiceImpl implements CommandeService {

    private final CommandeRepository commandeRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public List<Commande> findAll() {
        log.info("Finding all commandes");
        return commandeRepository.findAll();
    }

    @Override
    public Commande createCommande(Long userId, List<Long> productsId) {
        log.info("Creating commande for user ID: {} with products: {}", userId, productsId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundEntityExceptions("User with ID : " + userId + " not found"));

        List<Product> products = productRepository.findAllById(productsId);
        if (products.size() != productsId.size()) {
            throw new NotFoundEntityExceptions("One or more products were not found");
        }

        Commande commande = new Commande();
        commande.setUser(user);
        commande.setProducts(products);
        commande.setStatus(Status.En_attente);
        commande.setDateCommande(LocalDateTime.now());

        Double totalPrice = products.stream()
                .mapToDouble(Product::getPrice)
                .sum();
        commande.setPriceTotale(totalPrice);

        Commande savedCommande = commandeRepository.save(commande);
        log.info("Commande created successfully with ID: {}", savedCommande.getId());
        return savedCommande;
    }

    @Override
    public Commande findCommandeById(Long id) {
        log.info("Finding commande by ID: {}", id);
        return commandeRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityExceptions("Commande with ID : " + id + " not found"));
    }

    @Override
    public void deleteCommande(Long id) {
        log.info("Deleting commande with ID: {}", id);

        // Vérifier si la commande existe d'abord
        if (!commandeRepository.existsById(id)) {
            throw new NotFoundEntityExceptions("No Orders with the ID: " + id);
        }

        // Supprimer les relations d'abord (si nécessaire selon votre mapping)
        // En général, avec cascade, ce n'est pas nécessaire
        commandeRepository.deleteById(id);
        log.info("Commande with ID: {} deleted successfully", id);
    }

    @Override
    public Commande updateCommande(Long id, Commande commandeUpdate) {
        log.info("Updating commande with ID: {}", id);

        // Trouver la commande existante
        Commande existingCommande = commandeRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityExceptions("Commande with ID : " + id + " not found"));

        // Mettre à jour le statut si fourni
        if (commandeUpdate.getStatus() != null) {
            existingCommande.setStatus(commandeUpdate.getStatus());
            log.debug("Updated status to: {}", commandeUpdate.getStatus());
        }

        // Mettre à jour le prix total si fourni
        if (commandeUpdate.getPriceTotale() != null) {
            existingCommande.setPriceTotale(commandeUpdate.getPriceTotale());
            log.debug("Updated price totale to: {}", commandeUpdate.getPriceTotale());
        }

        // Mettre à jour la date de commande si fournie
        if (commandeUpdate.getDateCommande() != null) {
            existingCommande.setDateCommande(commandeUpdate.getDateCommande());
            log.debug("Updated date commande to: {}", commandeUpdate.getDateCommande());
        }

        // Mettre à jour l'utilisateur si fourni avec un ID valide
        if (commandeUpdate.getUser() != null && commandeUpdate.getUser().getId() != null) {
            User user = userRepository.findById(commandeUpdate.getUser().getId())
                    .orElseThrow(() -> new NotFoundEntityExceptions("User with ID : " + commandeUpdate.getUser().getId() + " not found"));
            existingCommande.setUser(user);
            log.debug("Updated user to ID: {}", user.getId());
        }

        // Mettre à jour les produits si fournis
        if (commandeUpdate.getProducts() != null && !commandeUpdate.getProducts().isEmpty()) {
            List<Long> productIds = commandeUpdate.getProducts().stream()
                    .map(Product::getId)
                    .toList();

            List<Product> products = productRepository.findAllById(productIds);
            if (products.size() != productIds.size()) {
                throw new NotFoundEntityExceptions("One or more products were not found");
            }

            existingCommande.setProducts(products);
            log.debug("Updated products to: {}", productIds);

            // Recalculer le prix total si les produits changent
            Double totalPrice = products.stream()
                    .mapToDouble(Product::getPrice)
                    .sum();
            existingCommande.setPriceTotale(totalPrice);
        }

        Commande updatedCommande = commandeRepository.save(existingCommande);
        log.info("Commande with ID: {} updated successfully", id);
        return updatedCommande;
    }
}