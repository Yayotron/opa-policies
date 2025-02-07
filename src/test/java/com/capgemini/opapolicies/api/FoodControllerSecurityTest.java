package com.capgemini.opapolicies.api;

import com.capgemini.opapolicies.security.OPAConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FoodController.class)
@Import({OPAConfiguration.class, KeycloakAuthenticator.class})
class FoodControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private KeycloakAuthenticator authenticator;

    @Test
    void testOmnivorousCanAccessAllFood() throws Exception {
        // Arrange
        String clientId = "client_omnivorous_user1";

        // Act
        String token = authenticator.getAccessToken(clientId);

        mockMvc.perform(get("/food/onion").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Onion"));

        mockMvc.perform(get("/food/milk").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Milk"));

        mockMvc.perform(get("/food/beef").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Beef"));
    }

    @Test
    void testVeganCanAccessOnion() throws Exception {
        // Arrange
        String clientId = "client_vegan_user1";

        // Act
        String token = authenticator.getAccessToken(clientId);

        mockMvc.perform(get("/food/onion").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Onion"));

        mockMvc.perform(get("/food/milk").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/food/beef").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testVegetarianCanAccessEgg() throws Exception {
        // Arrange
        String clientId = "client_vegetarian_user1";

        // Act
        String token = authenticator.getAccessToken(clientId);

        mockMvc.perform(get("/food/egg").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Egg"));
    }

    @Test
    void testOmnivorousCanAccessEgg() throws Exception {
        // Arrange
        String clientId = "client_omnivorous_user1";

        // Act
        String token = authenticator.getAccessToken(clientId);

        mockMvc.perform(get("/food/egg").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Egg"));
    }

    @Test
    void testVeganCannotAccessEgg() throws Exception {
        // Arrange
        String clientId = "client_vegan_user1";

        // Act
        String token = authenticator.getAccessToken(clientId);

        mockMvc.perform(get("/food/egg").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testPescatarianCannotAccessEgg() throws Exception {
        // Arrange
        String clientId = "client_pescatarian_user1";

        // Act
        String token = authenticator.getAccessToken(clientId);

        mockMvc.perform(get("/food/egg").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testFruitarianCannotAccessEgg() throws Exception {
        // Arrange
        String clientId = "client_fruitarian_user1";

        // Act
        String token = authenticator.getAccessToken(clientId);

        mockMvc.perform(get("/food/egg").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testOmnivorousCanAccessSalmon() throws Exception {
        // Arrange
        String clientId = "client_omnivorous_user1";

        // Act
        String token = authenticator.getAccessToken(clientId);

        mockMvc.perform(get("/food/salmon").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Salmon"));
    }

    @Test
    void testPescatarianCanAccessSalmon() throws Exception {
        // Arrange
        String clientId = "client_pescatarian_user1";

        // Act
        String token = authenticator.getAccessToken(clientId);

        mockMvc.perform(get("/food/salmon").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Salmon"));
    }
}