package com.iteam.bdd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteam.entities.Product;
import com.iteam.repositories.ProductRepository;
import io.cucumber.java.Before;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Etantdonné;
import io.cucumber.java.fr.Quand;
import io.cucumber.datatable.DataTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private ResultActions lastResult;
    private List<Product> productList;
    private Long currentProductId;
    private MvcResult lastMvcResult;

    @Before
    public void setup() {
        productRepository.deleteAll();
        currentProductId = null;
        lastMvcResult = null;
    }

    @Etantdonné("la base de données des produits est vide")
    public void la_base_de_donnees_des_produits_est_vide() {
        productRepository.deleteAll();
        assertThat(productRepository.count()).isZero();
    }

    @Etantdonné("un produit {string} existe avec l'ID {int}")
    public void un_produit_existe_avec_l_id(String productName, int referenceId) throws Exception {
        // Créer le produit via l'API
        Map<String, Object> productRequest = new HashMap<>();
        productRequest.put("nameProduct", productName);
        productRequest.put("price", 1000.0);
        productRequest.put("quantity", 10);

        // Effectuer la requête et stocker le résultat
        lastMvcResult = mockMvc.perform(post("/api/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extraire la réponse
        String response = lastMvcResult.getResponse().getContentAsString();
        System.out.println("DEBUG - Product creation response: " + response);

        // Parser la réponse JSON
        JsonNode jsonNode = objectMapper.readTree(response);

        // Extraire l'ID du produit selon la structure de réponse
        if (jsonNode.has("Product") && jsonNode.get("Product").has("id")) {
            // Structure: {"Product": {"id": 1, ...}}
            currentProductId = jsonNode.get("Product").get("id").asLong();
        } else if (jsonNode.has("id")) {
            // Structure: {"id": 1, ...}
            currentProductId = jsonNode.get("id").asLong();
        } else {
            // Structure: {"product": {"id": 1, ...}}
            currentProductId = jsonNode.get("product").get("id").asLong();
        }

        System.out.println("DEBUG - Extracted product ID: " + currentProductId);

        // Préparer lastResult pour les assertions futures
        lastResult = mockMvc.perform(get("/api/products/" + currentProductId));
    }

    @Quand("je crée un produit {string} avec le prix {string} et quantité {string}")
    public void je_crée_un_produit_avec_le_prix_et_quantité(String name, String price, String quantity) throws Exception {
        Map<String, Object> productRequest = new HashMap<>();
        productRequest.put("nameProduct", name);
        productRequest.put("price", Double.parseDouble(price));
        productRequest.put("quantity", Integer.parseInt(quantity));

        // Effectuer la requête et stocker le résultat
        lastResult = mockMvc.perform(post("/api/products/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)));
    }

    @Alors("le produit {string} existe dans la base")
    public void le_produit_existe_dans_la_base(String productName) throws Exception {
        // Vérifier le statut HTTP
        lastResult.andExpect(status().isCreated());

        // Extraire et afficher la réponse pour débogage
        String response = lastResult.andReturn().getResponse().getContentAsString();
        System.out.println("DEBUG - Actual response: " + response);

        // Essayer différentes structures JSON possibles
        try {
            lastResult.andExpect(jsonPath("$.Product.nameProduct").value(productName));
        } catch (AssertionError e1) {
            try {
                lastResult.andExpect(jsonPath("$.product.nameProduct").value(productName));
            } catch (AssertionError e2) {
                lastResult.andExpect(jsonPath("$.nameProduct").value(productName));
            }
        }

        // Vérifier aussi dans la base de données
        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getNameProduct()).isEqualTo(productName);
    }

    @Etantdonné("les produits suivants existent:")
    public void les_produits_suivants_existent(DataTable dataTable) throws Exception {
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            Map<String, Object> productRequest = new HashMap<>();
            productRequest.put("nameProduct", row.get("nameProduct"));
            productRequest.put("price", Double.parseDouble(row.get("price")));
            productRequest.put("quantity", Integer.parseInt(row.get("quantity")));

            mockMvc.perform(post("/api/products/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productRequest)))
                    .andExpect(status().isCreated());
        }
    }

    @Quand("je demande la liste des produits")
    public void je_demande_la_liste_des_produits() throws Exception {
        lastResult = mockMvc.perform(get("/api/products"));
    }

    @Alors("je reçois {int} produits")
    public void je_reçois_produits(int count) throws Exception {
        lastResult.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(count));
    }

    @Quand("je mets à jour le produit {int} avec le nom {string} et prix {string}")
    public void je_mets_à_jour_le_produit_avec_le_nom_et_prix(int referenceId, String newName, String newPrice) throws Exception {
        // Utiliser l'ID réel si disponible, sinon utiliser la référence
        Long productId = currentProductId != null ? currentProductId : (long) referenceId;

        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("nameProduct", newName);
        updateRequest.put("price", Double.parseDouble(newPrice));
        updateRequest.put("quantity", 10); // Valeur par défaut

        lastResult = mockMvc.perform(put("/api/products/" + productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)));
    }

    @Alors("le produit {int} a le nom {string} et prix {string}")
    public void le_produit_a_le_nom_et_prix(int referenceId, String expectedName, String expectedPrice) throws Exception {
        lastResult.andExpect(status().isOk());

        // Essayer différentes structures JSON
        try {
            lastResult.andExpect(jsonPath("$.nameProduct").value(expectedName))
                    .andExpect(jsonPath("$.price").value(Double.parseDouble(expectedPrice)));
        } catch (AssertionError e) {
            // Si la réponse est encapsulée
            lastResult.andExpect(jsonPath("$.Product.nameProduct").value(expectedName))
                    .andExpect(jsonPath("$.Product.price").value(Double.parseDouble(expectedPrice)));
        }
    }

    @Quand("je supprime le produit avec l'ID {int}")
    public void je_supprime_le_produit_avec_l_id(int referenceId) throws Exception {
        // Utiliser l'ID réel si disponible, sinon utiliser la référence
        Long productId = currentProductId != null ? currentProductId : (long) referenceId;

        lastResult = mockMvc.perform(delete("/api/products/" + productId));
    }

    @Alors("le produit avec l'ID {int} n'existe plus")
    public void le_produit_avec_l_id_n_existe_plus(int referenceId) throws Exception {
        lastResult.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product delete with success"));

        // Utiliser l'ID réel si disponible, sinon utiliser la référence
        Long productId = currentProductId != null ? currentProductId : (long) referenceId;
        assertThat(productRepository.existsById(productId)).isFalse();
    }

    // Méthode utilitaire pour obtenir le contenu de la réponse
    private String getResponseContent() throws Exception {
        if (lastMvcResult != null) {
            return lastMvcResult.getResponse().getContentAsString();
        } else if (lastResult != null) {
            return lastResult.andReturn().getResponse().getContentAsString();
        }
        return "";
    }
}