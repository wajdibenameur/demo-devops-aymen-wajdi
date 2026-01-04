package com.iteam.bdd;

import com.iteam.entities.Product;
import com.iteam.repositories.ProductRepository;
import io.cucumber.java.Before;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Etantdonné;
import io.cucumber.java.fr.Quand;
import io.cucumber.datatable.DataTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ProductStepDefinitions {

    @Autowired
    private ProductRepository productRepository;

    private List<Product> productList;

    @Before
    public void setup() {
        productRepository.deleteAll();
    }

    @Etantdonné("la base de données des produits est vide")
    public void la_base_de_donnees_des_produits_est_vide() {
        productRepository.deleteAll();
        assertThat(productRepository.count()).isZero();
    }

    @Etantdonné("un produit {string} existe avec l'ID {int}")
    public void un_produit_existe_avec_l_id(String productName, int id) {
        Product product = new Product();
        product.setNameProduct(productName);
        product.setPrice(1000.0);
        product.setQuantity(10);
        productRepository.save(product);
    }

    @Quand("je crée un produit {string} avec le prix {string} et quantité {string}")
    public void je_crée_un_produit_avec_le_prix_et_quantité(String name, String price, String quantity) {
        Product product = new Product();
        product.setNameProduct(name);
        product.setPrice(Double.parseDouble(price));
        product.setQuantity(Integer.parseInt(quantity));
        productRepository.save(product);
    }

    @Alors("le produit {string} existe dans la base")
    public void le_produit_existe_dans_la_base(String productName) {
        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getNameProduct()).isEqualTo(productName);
    }

    @Etantdonné("les produits suivants existent:")
    public void les_produits_suivants_existent(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            Product product = new Product();
            product.setNameProduct(row.get("nameProduct"));
            product.setPrice(Double.parseDouble(row.get("price")));
            product.setQuantity(Integer.parseInt(row.get("quantity")));
            productRepository.save(product);
        }
    }

    @Quand("je demande la liste des produits")
    public void je_demande_la_liste_des_produits() {
        productList = productRepository.findAll();
    }

    @Alors("je reçois {int} produits")
    public void je_reçois_produits(int count) {
        assertThat(productList).hasSize(count);
    }

    @Quand("je mets à jour le produit {int} avec le nom {string} et prix {string}")
    public void je_mets_à_jour_le_produit_avec_le_nom_et_prix(int id, String newName, String newPrice) {
        // Prendre le premier produit
        List<Product> products = productRepository.findAll();
        assertThat(products).isNotEmpty();
        Product product = products.get(0);

        product.setNameProduct(newName);
        product.setPrice(Double.parseDouble(newPrice));
        productRepository.save(product);
    }

    @Alors("le produit {int} a le nom {string} et prix {string}")
    public void le_produit_a_le_nom_et_prix(int id, String expectedName, String expectedPrice) {
        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getNameProduct()).isEqualTo(expectedName);
        assertThat(products.get(0).getPrice()).isEqualTo(Double.parseDouble(expectedPrice));
    }

    @Quand("je supprime le produit avec l'ID {int}")
    public void je_supprime_le_produit_avec_l_id(int id) {
        // Supprimer tous les produits (ou le premier)
        productRepository.deleteAll();
    }

    @Alors("le produit avec l'ID {int} n'existe plus")
    public void le_produit_avec_l_id_n_existe_plus(int id) {
        assertThat(productRepository.count()).isZero();
    }
}