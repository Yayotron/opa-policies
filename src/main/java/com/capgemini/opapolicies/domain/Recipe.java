package com.capgemini.opapolicies.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Recipe {
    private UUID id;
    private String name;
    private List<RecipeIngredient> ingredients;
    private DifficultyLevel difficulty;
    private int prepTimeMinutes;
    private String cuisine;
    private String createdBy;
    private boolean isPublic;

    public Recipe() {
        this.ingredients = new ArrayList<>();
    }

    public Recipe(UUID id, String name, DifficultyLevel difficulty, int prepTimeMinutes, 
                  String cuisine, String createdBy, boolean isPublic) {
        this.id = id;
        this.name = name;
        this.ingredients = new ArrayList<>();
        this.difficulty = difficulty;
        this.prepTimeMinutes = prepTimeMinutes;
        this.cuisine = cuisine;
        this.createdBy = createdBy;
        this.isPublic = isPublic;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void addIngredient(RecipeIngredient ingredient) {
        this.ingredients.add(ingredient);
    }

    public void removeIngredient(String foodName) {
        this.ingredients.removeIf(ing -> ing.getFoodName().equalsIgnoreCase(foodName));
    }

    public DifficultyLevel getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(DifficultyLevel difficulty) {
        this.difficulty = difficulty;
    }

    public int getPrepTimeMinutes() {
        return prepTimeMinutes;
    }

    public void setPrepTimeMinutes(int prepTimeMinutes) {
        this.prepTimeMinutes = prepTimeMinutes;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
}
