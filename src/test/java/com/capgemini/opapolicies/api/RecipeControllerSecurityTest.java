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
 * Test cases for Recipe Controller endpoints
 * Demonstrates OPA policy enforcement for:
 * - Resource ownership (users can only modify their own recipes)
 * - Public vs private recipe visibility
 * - Role-based recipe creation (chef, admin)
 * - Cross-resource validation (adding compatible ingredients)
 */
@WebMvcTest(RecipeController.class)
@Import({OPAConfiguration.class, KeycloakAuthenticator.class})
class RecipeControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private KeycloakAuthenticator authenticator;

    // ========== GET /recipes/{recipeId} Tests ==========

    @Test
    void testAnyUserCanViewPublicRecipe() throws Exception {
        String token = authenticator.getAccessToken("client_vegan_user1");

        mockMvc.perform(get("/recipes/{recipeId}", "550e8400-e29b-41d4-a716-446655440000")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testOwnerCanViewPrivateRecipe() throws Exception {
        String token = authenticator.getAccessToken("client_chef_user1");

        mockMvc.perform(get("/recipes/{recipeId}", "550e8400-e29b-41d4-a716-446655440001")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testNonOwnerCannotViewPrivateRecipe() throws Exception {
        String token = authenticator.getAccessToken("client_vegan_user1");

        mockMvc.perform(get("/recipes/{recipeId}", "550e8400-e29b-41d4-a716-446655440001")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAdminCanViewAnyPrivateRecipe() throws Exception {
        String token = authenticator.getAccessToken("client_admin_user1");

        mockMvc.perform(get("/recipes/{recipeId}", "550e8400-e29b-41d4-a716-446655440001")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    // ========== GET /recipes (List with filters) Tests ==========

    @Test
    void testListPublicRecipesOnly() throws Exception {
        String token = authenticator.getAccessToken("client_vegan_user1");

        mockMvc.perform(get("/recipes")
                .param("public-only", "true")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testListRecipesByDifficulty() throws Exception {
        String token = authenticator.getAccessToken("client_chef_user1");

        mockMvc.perform(get("/recipes")
                .param("difficulty", "BEGINNER")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testListRecipesByCuisine() throws Exception {
        String token = authenticator.getAccessToken("client_omnivorous_user1");

        mockMvc.perform(get("/recipes")
                .param("cuisine", "Italian")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testListRecipesByMaxPrepTime() throws Exception {
        String token = authenticator.getAccessToken("client_vegan_user1");

        mockMvc.perform(get("/recipes")
                .param("max-prep-time", "30")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    // ========== POST /recipes Tests ==========

    @Test
    void testChefCanCreateRecipe() throws Exception {
        String token = authenticator.getAccessToken("client_chef_user1");

        mockMvc.perform(post("/recipes")
                .param("name", "Vegan Pasta")
                .param("difficulty", "INTERMEDIATE")
                .param("cuisine", "Italian")
                .param("prep-time", "25")
                .param("is-public", "true")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());
    }

    @Test
    void testAdminCanCreateRecipe() throws Exception {
        String token = authenticator.getAccessToken("client_admin_user1");

        mockMvc.perform(post("/recipes")
                .param("name", "Admin Special")
                .param("difficulty", "ADVANCED")
                .param("cuisine", "Fusion")
                .param("prep-time", "60")
                .param("is-public", "false")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());
    }

    @Test
    void testRegularUserCannotCreateRecipe() throws Exception {
        String token = authenticator.getAccessToken("client_vegan_user1");

        mockMvc.perform(post("/recipes")
                .param("name", "Simple Salad")
                .param("difficulty", "BEGINNER")
                .param("cuisine", "Mediterranean")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    // ========== PUT /recipes/{recipeId} Tests ==========

    @Test
    void testOwnerCanUpdateOwnRecipe() throws Exception {
        String token = authenticator.getAccessToken("client_chef_user1");

        mockMvc.perform(put("/recipes/{recipeId}", "550e8400-e29b-41d4-a716-446655440000")
                .param("difficulty", "ADVANCED")
                .param("is-public", "false")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testNonOwnerCannotUpdateRecipe() throws Exception {
        String token = authenticator.getAccessToken("client_chef_user2");

        mockMvc.perform(put("/recipes/{recipeId}", "550e8400-e29b-41d4-a716-446655440000")
                .param("difficulty", "BEGINNER")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAdminCanUpdateAnyRecipe() throws Exception {
        String token = authenticator.getAccessToken("client_admin_user1");

        mockMvc.perform(put("/recipes/{recipeId}", "550e8400-e29b-41d4-a716-446655440000")
                .param("is-public", "true")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    // ========== DELETE /recipes/{recipeId} Tests ==========

    @Test
    void testOwnerCanDeleteOwnRecipe() throws Exception {
        String token = authenticator.getAccessToken("client_chef_user1");

        mockMvc.perform(delete("/recipes/{recipeId}", "550e8400-e29b-41d4-a716-446655440000")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void testNonOwnerCannotDeleteRecipe() throws Exception {
        String token = authenticator.getAccessToken("client_chef_user2");

        mockMvc.perform(delete("/recipes/{recipeId}", "550e8400-e29b-41d4-a716-446655440000")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAdminCanDeleteAnyRecipe() throws Exception {
        String token = authenticator.getAccessToken("client_admin_user1");

        mockMvc.perform(delete("/recipes/{recipeId}", "550e8400-e29b-41d4-a716-446655440000")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    // ========== POST /recipes/{recipeId}/ingredients/{foodName} Tests ==========

    @Test
    void testOwnerCanAddIngredientToOwnRecipe() throws Exception {
        String token = authenticator.getAccessToken("client_chef_user1");

        mockMvc.perform(post("/recipes/{recipeId}/ingredients/{foodName}", 
                "550e8400-e29b-41d4-a716-446655440000", "carrot")
                .param("quantity", "2")
                .param("unit", "pieces")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testNonOwnerCannotAddIngredient() throws Exception {
        String token = authenticator.getAccessToken("client_chef_user2");

        mockMvc.perform(post("/recipes/{recipeId}/ingredients/{foodName}", 
                "550e8400-e29b-41d4-a716-446655440000", "onion")
                .param("quantity", "1")
                .param("unit", "piece")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testVeganChefCannotAddMeatToRecipe() throws Exception {
        String token = authenticator.getAccessToken("client_chef_vegan_user1");

        mockMvc.perform(post("/recipes/{recipeId}/ingredients/{foodName}", 
                "550e8400-e29b-41d4-a716-446655440000", "beef")
                .param("quantity", "200")
                .param("unit", "grams")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAdminCanAddAnyIngredient() throws Exception {
        String token = authenticator.getAccessToken("client_admin_user1");

        mockMvc.perform(post("/recipes/{recipeId}/ingredients/{foodName}", 
                "550e8400-e29b-41d4-a716-446655440000", "salmon")
                .param("quantity", "150")
                .param("unit", "grams")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    // ========== DELETE /recipes/{recipeId}/ingredients/{foodName} Tests ==========

    @Test
    void testOwnerCanRemoveIngredientFromOwnRecipe() throws Exception {
        String token = authenticator.getAccessToken("client_chef_user1");

        mockMvc.perform(delete("/recipes/{recipeId}/ingredients/{foodName}", 
                "550e8400-e29b-41d4-a716-446655440000", "carrot")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void testNonOwnerCannotRemoveIngredient() throws Exception {
        String token = authenticator.getAccessToken("client_vegan_user1");

        mockMvc.perform(delete("/recipes/{recipeId}/ingredients/{foodName}", 
                "550e8400-e29b-41d4-a716-446655440000", "carrot")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    // ========== GET /recipes/{recipeId}/compatibility Tests ==========

    @Test
    void testUserCanCheckOwnCompatibility() throws Exception {
        String token = authenticator.getAccessToken("client_vegan_user1");

        mockMvc.perform(get("/recipes/{recipeId}/compatibility", "550e8400-e29b-41d4-a716-446655440000")
                .param("user-id", "vegan_user1")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testNutritionistCanCheckAnyUserCompatibility() throws Exception {
        String token = authenticator.getAccessToken("client_nutritionist_user1");

        mockMvc.perform(get("/recipes/{recipeId}/compatibility", "550e8400-e29b-41d4-a716-446655440000")
                .param("user-id", "vegan_user1")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testAdminCanCheckAnyUserCompatibility() throws Exception {
        String token = authenticator.getAccessToken("client_admin_user1");

        mockMvc.perform(get("/recipes/{recipeId}/compatibility", "550e8400-e29b-41d4-a716-446655440000")
                .param("user-id", "omnivorous_user1")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testUserCannotCheckOtherUserCompatibility() throws Exception {
        String token = authenticator.getAccessToken("client_vegan_user1");

        mockMvc.perform(get("/recipes/{recipeId}/compatibility", "550e8400-e29b-41d4-a716-446655440000")
                .param("user-id", "omnivorous_user1")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
