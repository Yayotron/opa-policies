package com.capgemini.opapolicies.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@TestComponent
public class KeycloakAuthenticator {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String CLIENT_SECRET = "Et40F9LFZbgtdg8zNPCVLyR0ZLUjr2Sx";

    @Autowired
    private ObjectMapper objectMapper;
    @Value("${keycloak.auth.url}")
    private String keycloakAuthUrl;

    public String getAccessToken(String clientId) throws Exception {
        return getAccessToken(clientId, CLIENT_SECRET);
    }

    public String getAccessToken(String clientId, String clientSecret) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String body = "grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(keycloakAuthUrl, requestEntity, String.class);
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
