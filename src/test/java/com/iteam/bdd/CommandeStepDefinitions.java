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
        // Create request map matching CreateCommandeRequestDTO
        Map<String, Object> request = new HashMap<>();
        request.put("userId", usersCache.get(userName).getId());
        request.put("productsId", Arrays.asList(productsCache.get(productName).getId()));

        lastResult = mockMvc.perform(post("/api/orders/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    @Alors("la commande existe dans la base avec le statut {string}")
    public void verifier_commande(String statut) throws Exception {
        Status status = convert(statut);

        lastResult.andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Order create with success"))
                // FIX: Use "$.orders.status" (lowercase o) - based on your controller
                .andExpect(jsonPath("$.orders.status").value(status.name()));

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

            // Use service to create commande properly
            Map<String, Object> request = new HashMap<>();
            request.put("userId", user.getId());
            request.put("productsId", Arrays.asList(product.getId()));

            try {
                mockMvc.perform(post("/api/orders/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Quand("je demande la liste des commandes")
    public void lister() throws Exception {
        lastResult = mockMvc.perform(get("/api/orders"));
    }

    @Alors("je reçois {int} commandes")
    public void verifier_nombre(int count) throws Exception {
        lastResult.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(count));
    }

    // ================= UPDATE =================
    @Etantdonné("une commande existe avec le statut {string}")
    public void commande_existe(String statut) throws Exception {
        // First create user and product
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@email.com");
        user.setPhoneNumber("12345678");
        user = userRepository.save(user);

        Product product = new Product();
        product.setNameProduct("Test Product");
        product.setPrice(100.0);
        product.setQuantity(5);
        product = productRepository.save(product);

        // Create commande via API
        Map<String, Object> request = new HashMap<>();
        request.put("userId", user.getId());
        request.put("productsId", Arrays.asList(product.getId()));

        String response = mockMvc.perform(post("/api/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract commande ID from response
        currentCommandeId = objectMapper.readTree(response)
                .path("orders")
                .path("id")
                .asLong();
    }

    @Quand("je mets à jour la commande avec le statut {string}")
    public void update(String statut) throws Exception {
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("status", statut);
        updateRequest.put("priceTotale", 100.0);

        lastResult = mockMvc.perform(put("/api/orders/" + currentCommandeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)));
    }

    @Alors("la commande a le statut {string}")
    public void verifier_update(String statut) throws Exception {
        lastResult.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Update Orders Successufully"))
                .andExpect(jsonPath("$.orders.status").value(convert(statut).name())); // Note: lowercase 'o'
    }

    // ================= DELETE =================
    @Etantdonné("une commande existe")
    public void commande_existe() throws Exception {
        commande_existe("En_attente");
    }

    @Quand("je supprime la commande")
    public void supprimer() throws Exception {
        lastResult = mockMvc.perform(delete("/api/orders/" + currentCommandeId));
    }

    @Alors("la commande n'existe plus")
    public void verifier_delete() throws Exception {
        lastResult.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("order delete with success"));
        assertThat(commandeRepository.existsById(currentCommandeId)).isFalse();
    }

    // ================= UTIL =================
    private Status convert(String s) {
        return Status.valueOf(s.replace("é", "e").replace("É", "E"));
    }
}