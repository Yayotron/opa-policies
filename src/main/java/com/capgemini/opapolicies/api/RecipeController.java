package com.capgemini.opapolicies.api;

import com.capgemini.opapolicies.domain.DifficultyLevel;
import com.capgemini.opapolicies.domain.Recipe;
import com.capgemini.opapolicies.service.RecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    /**
     * GET /recipes/{recipeId}
     * Retrieve recipe details
     * Public recipes accessible to all; private recipes only by owner or admin
     */
    @GetMapping("/{recipeId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Recipe> getRecipe(@PathVariable UUID recipeId) {
        Recipe recipe = recipeService.getRecipe(recipeId);
        if (recipe == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recipe);
    }

    /**
     * GET /recipes
     * List recipes with filtering
     * Query params: difficulty, cuisine, max-prep-time, public-only
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Recipe>> listRecipes(
            @RequestParam(required = false) DifficultyLevel difficulty,
            @RequestParam(required = false) String cuisine,
            @RequestParam(name = "max-prep-time", required = false) Integer maxPrepTime,
            @RequestParam(name = "public-only", required = false, defaultValue = "false") boolean publicOnly) {
        List<Recipe> recipes = recipeService.listRecipes(difficulty, cuisine, maxPrepTime, publicOnly);
        return ResponseEntity.ok(recipes);
    }

    /**
     * POST /recipes
     * Create a new recipe (chef or admin roles)
     * Query params: name, difficulty, cuisine, is-public
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Recipe> createRecipe(
            @RequestParam String name,
            @RequestParam DifficultyLevel difficulty,
            @RequestParam String cuisine,
            @RequestParam(name = "prep-time", defaultValue = "30") int prepTime,
            @RequestParam(name = "is-public", defaultValue = "false") boolean isPublic,
            Authentication authentication) {
        String username = authentication.getName();
        Recipe recipe = recipeService.createRecipe(name, difficulty, prepTime, cuisine, username, isPublic);
        return ResponseEntity.status(HttpStatus.CREATED).body(recipe);
    }

    /**
     * PUT /recipes/{recipeId}
     * Update recipe metadata (owner or admin only)
     * Query params: difficulty, is-public
     */
    @PutMapping("/{recipeId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Recipe> updateRecipe(
            @PathVariable UUID recipeId,
            @RequestParam(required = false) DifficultyLevel difficulty,
            @RequestParam(name = "is-public", required = false) Boolean isPublic) {
        Recipe recipe = recipeService.updateRecipe(recipeId, difficulty, isPublic);
        if (recipe == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recipe);
    }

    /**
     * DELETE /recipes/{recipeId}
     * Delete a recipe (owner or admin only)
     */
    @DeleteMapping("/{recipeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteRecipe(@PathVariable UUID recipeId) {
        boolean success = recipeService.deleteRecipe(recipeId);
        if (!success) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /recipes/{recipeId}/ingredients/{foodName}
     * Add ingredient to recipe (owner or admin)
     * Query params: quantity, unit
     */
    @PostMapping("/{recipeId}/ingredients/{foodName}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> addIngredient(
            @PathVariable UUID recipeId,
            @PathVariable String foodName,
            @RequestParam double quantity,
            @RequestParam String unit) {
        boolean success = recipeService.addIngredient(recipeId, foodName, quantity, unit);
        if (!success) {
            return ResponseEntity.badRequest().body("Failed to add ingredient");
        }
        return ResponseEntity.ok("Ingredient added successfully");
    }

    /**
     * DELETE /recipes/{recipeId}/ingredients/{foodName}
     * Remove ingredient from recipe (owner or admin only)
     */
    @DeleteMapping("/{recipeId}/ingredients/{foodName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> removeIngredient(
            @PathVariable UUID recipeId,
            @PathVariable String foodName) {
        boolean success = recipeService.removeIngredient(recipeId, foodName);
        if (!success) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /recipes/{recipeId}/compatibility
     * Check if recipe is compatible with user's dietary restrictions
     * Query params: user-id
     */
    @GetMapping("/{recipeId}/compatibility")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<String, Object>> checkCompatibility(
            @PathVariable UUID recipeId,
            @RequestParam(name = "user-id") String userId) {
        Map<String, Object> compatibility = recipeService.checkCompatibility(recipeId, userId);
        if (compatibility == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(compatibility);
    }
}
