package com.iteam.bdd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteam.entities.User;
import com.iteam.repositories.UserRepository;
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
public class UserStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private ResultActions lastResult;
    private List<User> userList;
    private Long currentUserId;

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
    public void je_cree_un_utilisateur(String firstName, String lastName, String email) throws Exception {
        Map<String, Object> userRequest = new HashMap<>();
        userRequest.put("firstName", firstName);
        userRequest.put("lastName", lastName);
        userRequest.put("email", email);
        userRequest.put("phoneNumber", "12345678");

        lastResult = mockMvc.perform(post("/api/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)));
    }
    @Alors("l'utilisateur {string} existe dans la base")
    public void l_utilisateur_existe(String firstName) throws Exception {
        lastResult.andExpect(status().isCreated())
                // FIX: Use "$.user.firstName" (lowercase u)
                .andExpect(jsonPath("$.user.firstName").value(firstName));

        // Also verify in database
        List<User> users = userRepository.findAll();
        assertThat(users).anyMatch(u -> u.getFirstName().equals(firstName));
    }

    @Etantdonné("les utilisateurs suivants existent:")
    public void les_utilisateurs_suivants_existent(DataTable dataTable) throws Exception {
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            Map<String, Object> userRequest = new HashMap<>();
            userRequest.put("firstName", row.get("firstName"));
            userRequest.put("lastName", row.get("lastName"));
            userRequest.put("email", row.get("email"));
            userRequest.put("phoneNumber", row.get("phoneNumber"));

            mockMvc.perform(post("/api/users/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userRequest)))
                    .andExpect(status().isCreated());
        }
    }

    @Quand("je demande la liste des utilisateurs")
    public void je_demande_la_liste() throws Exception {
        lastResult = mockMvc.perform(get("/api/users"));
    }

    @Alors("je reçois {int} utilisateurs")
    public void je_recois_utilisateurs(int count) throws Exception {
        lastResult.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(count));
    }

    @Etantdonné("un utilisateur {string} existe avec l'ID {int}")
    public void un_utilisateur_existe_avec_id(String firstName, int id) throws Exception {
        // Create user via API
        Map<String, Object> userRequest = new HashMap<>();
        userRequest.put("firstName", firstName);
        userRequest.put("lastName", "Test");
        userRequest.put("email", firstName.toLowerCase() + "@email.com");
        userRequest.put("phoneNumber", "12345678");

        lastResult = mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated());

        // Get the ID from response
        String response = lastResult.andReturn().getResponse().getContentAsString();
        currentUserId = objectMapper.readTree(response).path("id").asLong();
    }

    @Quand("je supprime l'utilisateur avec l'ID {int}")
    public void je_supprime_utilisateur(int id) throws Exception {
        lastResult = mockMvc.perform(delete("/api/users/" + id));
    }

    @Alors("l'utilisateur avec l'ID {int} n'existe plus")
    public void utilisateur_n_existe_plus(int id) throws Exception {
        lastResult.andExpect(status().isOk());
        assertThat(userRepository.existsById((long) id)).isFalse();
    }
}