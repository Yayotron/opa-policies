package com.capgemini.opapolicies.api;

import com.capgemini.opapolicies.security.OPAConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FoodController.class)
@Import(OPAConfiguration.class)
class FoodControllerSecurityTest {

    private static final String CLIENT_SECRET = "Et40F9LFZbgtdg8zNPCVLyR0ZLUjr2Sx";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    void testOmnivorousCanAccessAllFood() throws Exception {
        // Arrange
        String clientId = "client_omnivorous_user1";

        // Act
        String token = getAccessToken(clientId, CLIENT_SECRET);

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
        String token = getAccessToken(clientId, CLIENT_SECRET);

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
        String token = getAccessToken(clientId, CLIENT_SECRET);

        mockMvc.perform(get("/food/egg").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Egg"));
    }

    @Test
    void testOmnivorousCanAccessEgg() throws Exception {
        // Arrange
        String clientId = "client_omnivorous_user1";

        // Act
        String token = getAccessToken(clientId, CLIENT_SECRET);

        mockMvc.perform(get("/food/egg").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Egg"));
    }

    @Test
    void testVeganCannotAccessEgg() throws Exception {
        // Arrange
        String clientId = "client_vegan_user1";

        // Act
        String token = getAccessToken(clientId, CLIENT_SECRET);

        mockMvc.perform(get("/food/egg").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testPescatarianCannotAccessEgg() throws Exception {
        // Arrange
        String clientId = "client_pescatarian_user1";

        // Act
        String token = getAccessToken(clientId, CLIENT_SECRET);

        mockMvc.perform(get("/food/egg").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testFruitarianCannotAccessEgg() throws Exception {
        // Arrange
        String clientId = "client_fruitarian_user1";

        // Act
        String token = getAccessToken(clientId, CLIENT_SECRET);

        mockMvc.perform(get("/food/egg").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    private String getAccessToken(String clientId, String clientSecret) throws Exception {
        String url = "http://localhost:7070/realms/FoodSecurityRealm/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String body = "grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return extractAccessToken(response.getBody());
        } else {
            throw new IllegalStateException("Failed to retrieve access token");
        }
    }

    private String extractAccessToken(String tokenResponse) throws JsonProcessingException {
        return objectMapper.readTree(tokenResponse).get("access_token").asText();
    }
}