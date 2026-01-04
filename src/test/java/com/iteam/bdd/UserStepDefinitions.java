package com.iteam.bdd;

import com.iteam.entities.User;
import com.iteam.repositories.UserRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class UserStepDefinitions {

    @Autowired
    private UserRepository userRepository;

    private List<User> userList;
    private Long lastUserId;

    @Before
    public void setup() {
        userRepository.deleteAll();
    }

    @Etantdonné("une base de données vide")
    public void une_base_de_donnees_vide() {
        userRepository.deleteAll();
        assertThat(userRepository.count()).isZero();
    }

    @Quand("je crée un utilisateur {string} avec le nom {string} et l'email {string}")
    public void je_cree_un_utilisateur(String firstName, String lastName, String email) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhoneNumber("12345678");
        User saved = userRepository.save(user);
        lastUserId = saved.getId();
    }

    @Alors("l'utilisateur {string} existe dans la base")
    public void l_utilisateur_existe(String firstName) {
        List<User> users = userRepository.findAll();
        assertThat(users).anyMatch(u -> u.getFirstName().equals(firstName));
    }

    @Etantdonné("les utilisateurs suivants existent:")
    public void les_utilisateurs_suivants_existent(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            User user = new User();
            user.setFirstName(row.get("firstName"));
            user.setLastName(row.get("lastName"));
            user.setEmail(row.get("email"));
            user.setPhoneNumber(row.get("phoneNumber"));
            userRepository.save(user);
        }
    }

    @Quand("je demande la liste des utilisateurs")
    public void je_demande_la_liste() {
        userList = userRepository.findAll();
    }

    @Alors("je reçois {int} utilisateurs")
    public void je_recois_utilisateurs(int count) {
        assertThat(userList).hasSize(count);
    }

    @Etantdonné("un utilisateur {string} existe avec l'ID {int}")
    public void un_utilisateur_existe_avec_id(String firstName, int id) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName("Test");
        user.setEmail(firstName.toLowerCase() + "@email.com");
        user.setPhoneNumber("12345678");
        userRepository.save(user);
    }

    @Quand("je supprime l'utilisateur avec l'ID {int}")
    public void je_supprime_utilisateur(int id) {
        userRepository.deleteById((long) id);
    }

    @Alors("l'utilisateur avec l'ID {int} n'existe plus")
    public void utilisateur_n_existe_plus(int id) {
        assertThat(userRepository.existsById((long) id)).isFalse();
    }
}