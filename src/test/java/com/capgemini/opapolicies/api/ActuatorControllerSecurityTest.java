package com.capgemini.opapolicies.api;

import com.capgemini.opapolicies.security.OPAConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({OPAConfiguration.class, KeycloakAuthenticator.class})
class ActuatorControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private KeycloakAuthenticator authenticator;

    @Test
    void testOperatorCanAccessActuatorEndpoints() throws Exception {
        // Arrange
        String clientId = "client_operator_user1";

        // Act
        String token = authenticator.getAccessToken(clientId);

        mockMvc.perform(get("/management/health").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/management/info").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testUnauthenticatedCannotAccessActuatorEndpoints() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/management/health"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/management/info"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testNonOperatorCannotAccessActuatorEndpoints() throws Exception {
        // Arrange
        String clientId = "client_vegan_user1";

        // Act
        String token = authenticator.getAccessToken(clientId);

        mockMvc.perform(get("/management/health").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/management/info").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}