package com.capgemini.opapolicies.domain;

public class RecipeIngredient {
    private String foodName;
    private double quantity;
    private String unit;

    public RecipeIngredient() {
    }

    public RecipeIngredient(String foodName, double quantity, String unit) {
        this.foodName = foodName;
        this.quantity = quantity;
        this.unit = unit;
    }

    // Getters and Setters
    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
