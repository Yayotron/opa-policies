package com.capgemini.opapolicies.api;

import com.capgemini.opapolicies.security.OPAConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test cases for User Profile Controller endpoints
 * Demonstrates OPA policy enforcement for:
 * - Viewing own dietary profile
 * - Role-based access to other users' profiles (nutritionist, admin)
 */
@WebMvcTest(UserProfileController.class)
@Import({OPAConfiguration.class, KeycloakAuthenticator.class})
class UserProfileControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private KeycloakAuthenticator authenticator;

    // ========== GET /users/{userId}/dietary-profile Tests ==========

    @Test
    void testUserCanViewOwnDietaryProfile() throws Exception {
        String token = authenticator.getAccessToken("client_vegan_user1");

        mockMvc.perform(get("/users/{userId}/dietary-profile", "vegan_user1")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testUserCannotViewOtherUserDietaryProfile() throws Exception {
        String token = authenticator.getAccessToken("client_vegan_user1");

        mockMvc.perform(get("/users/{userId}/dietary-profile", "omnivorous_user1")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testNutritionistCanViewAnyUserDietaryProfile() throws Exception {
        String token = authenticator.getAccessToken("client_nutritionist_user1");

        mockMvc.perform(get("/users/{userId}/dietary-profile", "vegan_user1")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{userId}/dietary-profile", "omnivorous_user1")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testAdminCanViewAnyUserDietaryProfile() throws Exception {
        String token = authenticator.getAccessToken("client_admin_user1");

        mockMvc.perform(get("/users/{userId}/dietary-profile", "vegan_user1")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{userId}/dietary-profile", "pescatarian_user1")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testChefCannotViewOtherUserDietaryProfile() throws Exception {
        String token = authenticator.getAccessToken("client_chef_user1");

        mockMvc.perform(get("/users/{userId}/dietary-profile", "vegan_user1")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUnauthenticatedUserCannotViewDietaryProfile() throws Exception {
        mockMvc.perform(get("/users/{userId}/dietary-profile", "vegan_user1"))
                .andExpect(status().isUnauthorized());
    }
}
