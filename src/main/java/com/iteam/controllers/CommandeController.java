package com.iteam.controllers;

import com.iteam.entities.Commande;
import com.iteam.service.CommandeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordres")
@Tag(name = "orders" , description = "Gestion des commandes")
public class CommandeController {

    private final CommandeService commandeService;


    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }


    // Create Order
    @Operation(summary = "Create an Order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201" , description = "order created"),
            @ApiResponse(responseCode = "400" , description = "invalid request")
    })
    @PostMapping("/create")
    public ResponseEntity<Commande> createCommande(@RequestBody Commande commande) {
        Commande savedCommande = commandeService.createCommande(commande);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCommande);
    }
    // Get All Order
    @Operation(summary = "Get all Order")
    @GetMapping
    public ResponseEntity<List<Commande>> findAllCommandes() {
        return ResponseEntity.ok(commandeService.findAll());
    }
    // Get Order By Id
    @Operation(summary = "Get an Order By Id")
    @GetMapping("/{id}")
    public ResponseEntity<Commande> findCommandeById(@PathVariable Long id) {
        return ResponseEntity.ok(commandeService.findCommandeById(id));
    }
    

    // Update Order
    @Operation(summary = "Update an Order")
    @PutMapping("/{id}")
    public ResponseEntity<Commande> updateCommande(@PathVariable(name = "id") Long id,
                                                   @RequestBody Commande commande) {
        return ResponseEntity.ok(commandeService.updateCommande(id, commande));
    }
    @Operation(summary = "Delete an Order")
    // Delete Order
    @DeleteMapping("/{id}")
    public ResponseEntity<Commande> deleteCommande(@PathVariable Long id) {
        commandeService.deleteCommande(id);
        return ResponseEntity.noContent().build(); //204
    }












}
