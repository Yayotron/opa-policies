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
 * Test cases for Enhanced Food Controller endpoints
 * Demonstrates OPA policy enforcement for:
 * - Basic food access based on dietary restrictions
 * - Data filtering for allergen-free foods
 * - Rating functionality for authenticated users
 * - Admin-only deletion operations
 */
@WebMvcTest(EnhancedFoodController.class)
@Import({OPAConfiguration.class, KeycloakAuthenticator.class})
class EnhancedFoodControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private KeycloakAuthenticator authenticator;

    // ========== GET /food/{foodName} Tests ==========

    @Test
    void testVeganCanAccessVegetables() throws Exception {
        String token = authenticator.getAccessToken("client_vegan_user1");

        mockMvc.perform(get("/food/carrot")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testVeganCannotAccessDairy() throws Exception {
        String token = authenticator.getAccessToken("client_vegan_user1");

        mockMvc.perform(get("/food/milk")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testOmnivorousCanAccessAllFoods() throws Exception {
        String token = authenticator.getAccessToken("client_omnivorous_user1");

        mockMvc.perform(get("/food/beef")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/food/milk")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testPescatarianCanAccessFishButNotMeat() throws Exception {
        String token = authenticator.getAccessToken("client_pescatarian_user1");

        mockMvc.perform(get("/food/salmon")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/food/beef")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    // ========== GET /food (List with filters) Tests ==========

    @Test
    void testListFoodsWithCategoryFilter() throws Exception {
        String token = authenticator.getAccessToken("client_omnivorous_user1");

        mockMvc.perform(get("/food")
                .param("category", "VEGETABLE")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testListAllergenFreeFoods() throws Exception {
        String token = authenticator.getAccessToken("client_omnivorous_user1");

        mockMvc.perform(get("/food")
                .param("allergen-free", "DAIRY")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testListFoodsBySeasonFilter() throws Exception {
        String token = authenticator.getAccessToken("client_vegan_user1");

        mockMvc.perform(get("/food")
                .param("season", "summer")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    // ========== POST /food/{foodName}/rate Tests ==========

    @Test
    void testAuthenticatedUserCanRateFood() throws Exception {
        String token = authenticator.getAccessToken("client_vegan_user1");

        mockMvc.perform(post("/food/carrot/rate")
                .param("rating", "5")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testUnauthenticatedUserCannotRateFood() throws Exception {
        mockMvc.perform(post("/food/carrot/rate")
                .param("rating", "5"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testInvalidRatingIsRejected() throws Exception {
        String token = authenticator.getAccessToken("client_vegan_user1");

        mockMvc.perform(post("/food/carrot/rate")
                .param("rating", "10")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    // ========== DELETE /food/{foodName} Tests ==========

    @Test
    void testAdminCanDeleteFood() throws Exception {
        String token = authenticator.getAccessToken("client_admin_user1");

        mockMvc.perform(delete("/food/carrot")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void testRegularUserCannotDeleteFood() throws Exception {
        String token = authenticator.getAccessToken("client_vegan_user1");

        mockMvc.perform(delete("/food/carrot")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testChefCannotDeleteFood() throws Exception {
        String token = authenticator.getAccessToken("client_chef_user1");

        mockMvc.perform(delete("/food/carrot")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    // ========== GET /food/{foodName}/allergens Tests ==========

    @Test
    void testAnyAuthenticatedUserCanViewAllergens() throws Exception {
        String token = authenticator.getAccessToken("client_vegan_user1");

        mockMvc.perform(get("/food/milk/allergens")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testUnauthenticatedUserCannotViewAllergens() throws Exception {
        mockMvc.perform(get("/food/milk/allergens"))
                .andExpect(status().isUnauthorized());
    }

    // ========== GET /food/safe-for/{allergyType} Tests ==========

    @Test
    void testUserCanQuerySafeFoodsForAllergy() throws Exception {
        String token = authenticator.getAccessToken("client_vegan_user1");

        mockMvc.perform(get("/food/safe-for/DAIRY")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testGlutenFreeUserCanQuerySafeFoods() throws Exception {
        String token = authenticator.getAccessToken("client_gluten_free_user1");

        mockMvc.perform(get("/food/safe-for/GLUTEN")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    // ========== POST /food/{foodName}/flag Tests ==========

    @Test
    void testNutritionistCanFlagFood() throws Exception {
        String token = authenticator.getAccessToken("client_nutritionist_user1");

        mockMvc.perform(post("/food/beef/flag")
                .param("reason", "High cholesterol content")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testAdminCanFlagFood() throws Exception {
        String token = authenticator.getAccessToken("client_admin_user1");

        mockMvc.perform(post("/food/beef/flag")
                .param("reason", "Quality concerns")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testRegularUserCannotFlagFood() throws Exception {
        String token = authenticator.getAccessToken("client_vegan_user1");

        mockMvc.perform(post("/food/beef/flag")
                .param("reason", "Personal preference")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
