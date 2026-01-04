package com.iteam.bdd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteam.entities.*;
import com.iteam.repositories.*;
import io.cucumber.java.Before;
import io.cucumber.java.fr.*;
import io.cucumber.datatable.DataTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CommandeStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private ResultActions lastResult;
    private Long currentCommandeId;

    private final Map<String, User> usersCache = new HashMap<>();
    private final Map<String, Product> productsCache = new HashMap<>();

    @Before
    public void setup() {
        commandeRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
        usersCache.clear();
        productsCache.clear();
    }

    // ================= USERS =================

    @Etantdonné("un utilisateur {string} existe")
    public void un_utilisateur_existe(String name) {
        User user = new User();
        user.setFirstName(name);
        user.setLastName("Test");
        user.setEmail(name.toLowerCase() + "@email.com");
        user.setPhoneNumber(UUID.randomUUID().toString().substring(0, 8));
        usersCache.put(name, userRepository.save(user));
    }

    // ================= PRODUCTS =================

    @Et("un produit {string} existe")
    public void un_produit_existe(String name) {
        Product product = new Product();
        product.setNameProduct(name);
        product.setPrice(1000.0);
        product.setQuantity(10);
        productsCache.put(name, productRepository.save(product));
    }

    // ================= CREATE =================

    @Quand("je crée une commande pour l'utilisateur {string} avec le produit {string}")
    public void creer_commande(String userName, String productName) throws Exception {

        Commande commande = new Commande();
        commande.setDateCommande(LocalDateTime.now());
        commande.setStatus(Status.En_attente);
        commande.setUser(usersCache.get(userName));
        commande.setProducts(List.of(productsCache.get(productName)));
        commande.setPriceTotale(productsCache.get(productName).getPrice());

        lastResult = mockMvc.perform(post("/api/ordres/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commande)));
    }

    @Alors("la commande existe dans la base avec le statut {string}")
    public void verifier_commande(String statut) throws Exception {
        Status status = convert(statut);

        lastResult.andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(status.name()));

        assertThat(commandeRepository.findAll()).hasSize(1);
    }

    // ================= LIST =================

    @Etantdonné("les commandes suivantes existent:")
    public void commandes_existent(DataTable table) {

        for (Map<String, String> row : table.asMaps()) {

            User user = usersCache.computeIfAbsent(row.get("utilisateur"), n -> {
                User u = new User();
                u.setFirstName(n);
                u.setLastName("Test");
                u.setEmail(n.toLowerCase() + "@email.com");
                u.setPhoneNumber(UUID.randomUUID().toString().substring(0, 8));
                return userRepository.save(u);
            });

            Product product = productsCache.computeIfAbsent(row.get("produit"), n -> {
                Product p = new Product();
                p.setNameProduct(n);
                p.setPrice(Double.parseDouble(row.get("prixTotal")));
                p.setQuantity(5);
                return productRepository.save(p);
            });

            Commande c = new Commande();
            c.setDateCommande(LocalDateTime.now());
            c.setStatus(convert(row.get("statut")));
            c.setUser(user);
            c.setProducts(List.of(product));
            c.setPriceTotale(Double.parseDouble(row.get("prixTotal")));

            commandeRepository.save(c);
        }
    }

    @Quand("je demande la liste des commandes")
    public void lister() throws Exception {
        lastResult = mockMvc.perform(get("/api/ordres"));
    }

    @Alors("je reçois {int} commandes")
    public void verifier_nombre(int count) throws Exception {
        lastResult.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(count));
    }

    // ================= UPDATE =================

    @Etantdonné("une commande existe avec le statut {string}")
    public void commande_existe(String statut) {

        User u = new User();
        u.setFirstName("Test");
        u.setLastName("User");
        u.setEmail("test@email.com");
        u.setPhoneNumber(UUID.randomUUID().toString().substring(0, 8));
        u = userRepository.save(u);

        Product p = new Product();
        p.setNameProduct("Produit");
        p.setPrice(100.0);
        p.setQuantity(5);
        p = productRepository.save(p);

        Commande c = new Commande();
        c.setDateCommande(LocalDateTime.now());
        c.setStatus(convert(statut));
        c.setUser(u);
        c.setProducts(List.of(p));
        c.setPriceTotale(100.0);

        currentCommandeId = commandeRepository.save(c).getId();
    }

    @Quand("je mets à jour la commande avec le statut {string}")
    public void update(String statut) throws Exception {

        Commande c = new Commande();
        c.setStatus(convert(statut));

        lastResult = mockMvc.perform(put("/api/ordres/" + currentCommandeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(c)));
    }

    @Alors("la commande a le statut {string}")
    public void verifier_update(String statut) throws Exception {

        lastResult.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(convert(statut).name()));
    }

    // ================= DELETE =================

    @Etantdonné("une commande existe")
    public void commande_existe() {
        commande_existe("En_attente");
    }

    @Quand("je supprime la commande")
    public void supprimer() throws Exception {
        lastResult = mockMvc.perform(delete("/api/ordres/" + currentCommandeId));
    }

    @Alors("la commande n'existe plus")
    public void verifier_delete() throws Exception {
        lastResult.andExpect(status().isNoContent());
        assertThat(commandeRepository.existsById(currentCommandeId)).isFalse();
    }

    // ================= UTIL =================

    private Status convert(String s) {
        return Status.valueOf(s.replace("é", "e").replace("É", "E"));
    }
}
