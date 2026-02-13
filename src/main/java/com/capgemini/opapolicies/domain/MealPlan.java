package com.capgemini.opapolicies.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class MealPlan {
    private UUID id;
    private String userId;
    private LocalDate date;
    private MealType mealType;
    private UUID recipeId;
    private int servings;
    private LocalTime scheduledTime;

    public MealPlan() {
    }

    public MealPlan(UUID id, String userId, LocalDate date, MealType mealType, 
                    UUID recipeId, int servings, LocalTime scheduledTime) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.mealType = mealType;
        this.recipeId = recipeId;
        this.servings = servings;
        this.scheduledTime = scheduledTime;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public UUID getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(UUID recipeId) {
        this.recipeId = recipeId;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public LocalTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
}
