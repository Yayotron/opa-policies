package com.capgemini.opapolicies.domain;

import java.util.List;
import java.util.Set;

public class FoodItem {
    private String name;
    private FoodCategory category;
    private Set<Allergen> allergens;
    private NutritionalInfo nutritionalInfo;
    private List<String> availableSeasons;
    private double averageRating;
    private int ratingCount;

    public FoodItem() {
    }

    public FoodItem(String name, FoodCategory category, Set<Allergen> allergens, 
                    NutritionalInfo nutritionalInfo, List<String> availableSeasons) {
        this.name = name;
        this.category = category;
        this.allergens = allergens;
        this.nutritionalInfo = nutritionalInfo;
        this.availableSeasons = availableSeasons;
        this.averageRating = 0.0;
        this.ratingCount = 0;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FoodCategory getCategory() {
        return category;
    }

    public void setCategory(FoodCategory category) {
        this.category = category;
    }

    public Set<Allergen> getAllergens() {
        return allergens;
    }

    public void setAllergens(Set<Allergen> allergens) {
        this.allergens = allergens;
    }

    public NutritionalInfo getNutritionalInfo() {
        return nutritionalInfo;
    }

    public void setNutritionalInfo(NutritionalInfo nutritionalInfo) {
        this.nutritionalInfo = nutritionalInfo;
    }

    public List<String> getAvailableSeasons() {
        return availableSeasons;
    }

    public void setAvailableSeasons(List<String> availableSeasons) {
        this.availableSeasons = availableSeasons;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public void addRating(int rating) {
        double totalRating = this.averageRating * this.ratingCount;
        this.ratingCount++;
        this.averageRating = (totalRating + rating) / this.ratingCount;
    }
}
