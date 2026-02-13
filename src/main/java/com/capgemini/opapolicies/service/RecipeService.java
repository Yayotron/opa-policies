package com.capgemini.opapolicies.service;

import com.capgemini.opapolicies.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final Map<UUID, Recipe> recipeDatabase = new HashMap<>();
    private final FoodService foodService;

    public RecipeService(FoodService foodService) {
        this.foodService = foodService;
        initializeRecipes();
    }

    private void initializeRecipes() {
        // Sample recipe: Vegetable Salad
        UUID recipe1Id = UUID.randomUUID();
        Recipe recipe1 = new Recipe(recipe1Id, "Vegetable Salad", DifficultyLevel.BEGINNER, 
            15, "Mediterranean", "chef1", true);
        recipe1.addIngredient(new RecipeIngredient("carrot", 2, "pieces"));
        recipe1.addIngredient(new RecipeIngredient("onion", 1, "piece"));
        recipeDatabase.put(recipe1Id, recipe1);

        // Sample recipe: Grilled Salmon
        UUID recipe2Id = UUID.randomUUID();
        Recipe recipe2 = new Recipe(recipe2Id, "Grilled Salmon", DifficultyLevel.INTERMEDIATE, 
            30, "American", "chef2", true);
        recipe2.addIngredient(new RecipeIngredient("salmon", 200, "grams"));
        recipeDatabase.put(recipe2Id, recipe2);
    }

    public Recipe getRecipe(UUID recipeId) {
        return recipeDatabase.get(recipeId);
    }

    public List<Recipe> listRecipes(DifficultyLevel difficulty, String cuisine, Integer maxPrepTime, boolean publicOnly) {
        return recipeDatabase.values().stream()
            .filter(recipe -> !publicOnly || recipe.isPublic())
            .filter(recipe -> difficulty == null || recipe.getDifficulty() == difficulty)
            .filter(recipe -> cuisine == null || recipe.getCuisine().equalsIgnoreCase(cuisine))
            .filter(recipe -> maxPrepTime == null || recipe.getPrepTimeMinutes() <= maxPrepTime)
            .collect(Collectors.toList());
    }

    public Recipe createRecipe(String name, DifficultyLevel difficulty, int prepTime, 
                               String cuisine, String username, boolean isPublic) {
        UUID recipeId = UUID.randomUUID();
        Recipe recipe = new Recipe(recipeId, name, difficulty, prepTime, cuisine, username, isPublic);
        recipeDatabase.put(recipeId, recipe);
        return recipe;
    }

    public Recipe updateRecipe(UUID recipeId, DifficultyLevel difficulty, Boolean isPublic) {
        Recipe recipe = recipeDatabase.get(recipeId);
        if (recipe == null) {
            return null;
        }
        if (difficulty != null) {
            recipe.setDifficulty(difficulty);
        }
        if (isPublic != null) {
            recipe.setPublic(isPublic);
        }
        return recipe;
    }

    public boolean deleteRecipe(UUID recipeId) {
        return recipeDatabase.remove(recipeId) != null;
    }

    public boolean addIngredient(UUID recipeId, String foodName, double quantity, String unit) {
        Recipe recipe = recipeDatabase.get(recipeId);
        if (recipe == null) {
            return false;
        }
        FoodItem food = foodService.getFoodItem(foodName);
        if (food == null) {
            return false;
        }
        recipe.addIngredient(new RecipeIngredient(foodName, quantity, unit));
        return true;
    }

    public boolean removeIngredient(UUID recipeId, String foodName) {
        Recipe recipe = recipeDatabase.get(recipeId);
        if (recipe == null) {
            return false;
        }
        recipe.removeIngredient(foodName);
        return true;
    }

    public Map<String, Object> checkCompatibility(UUID recipeId, String userId) {
        Recipe recipe = recipeDatabase.get(recipeId);
        if (recipe == null) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("recipeId", recipeId);
        result.put("recipeName", recipe.getName());
        result.put("userId", userId);
        
        // Check for allergens in ingredients
        Set<Allergen> allergens = new HashSet<>();
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            FoodItem food = foodService.getFoodItem(ingredient.getFoodName());
            if (food != null) {
                allergens.addAll(food.getAllergens());
            }
        }
        
        result.put("allergens", allergens);
        result.put("compatible", allergens.isEmpty()); // Simplified logic
        
        return result;
    }

    public Map<UUID, Recipe> getRecipeDatabase() {
        return recipeDatabase;
    }
}
