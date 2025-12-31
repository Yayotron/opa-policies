package com.capgemini.opapolicies.service;

import com.capgemini.opapolicies.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FoodService {

    // In-memory storage for demo purposes
    private final Map<String, FoodItem> foodDatabase = new HashMap<>();
    private final Map<String, List<String>> flaggedFoods = new HashMap<>();

    public FoodService() {
        initializeFoodDatabase();
    }

    private void initializeFoodDatabase() {
        // Vegetables
        foodDatabase.put("onion", new FoodItem("onion", FoodCategory.VEGETABLE, 
            Set.of(), new NutritionalInfo(40, 1.1, 9.3, 0.1, 1.7), 
            List.of("spring", "summer", "fall", "winter")));
        
        foodDatabase.put("carrot", new FoodItem("carrot", FoodCategory.VEGETABLE, 
            Set.of(), new NutritionalInfo(41, 0.9, 9.6, 0.2, 2.8), 
            List.of("fall", "winter")));

        // Fruits
        foodDatabase.put("apple", new FoodItem("apple", FoodCategory.FRUIT, 
            Set.of(), new NutritionalInfo(52, 0.3, 14.0, 0.2, 2.4), 
            List.of("fall", "winter")));

        // Dairy
        foodDatabase.put("milk", new FoodItem("milk", FoodCategory.DAIRY, 
            Set.of(Allergen.DAIRY), new NutritionalInfo(61, 3.2, 4.8, 3.3, 0.0), 
            List.of("spring", "summer", "fall", "winter")));
        
        foodDatabase.put("cheese", new FoodItem("cheese", FoodCategory.DAIRY, 
            Set.of(Allergen.DAIRY), new NutritionalInfo(402, 25.0, 1.3, 33.0, 0.0), 
            List.of("spring", "summer", "fall", "winter")));

        // Meat
        foodDatabase.put("beef", new FoodItem("beef", FoodCategory.MEAT, 
            Set.of(), new NutritionalInfo(250, 26.0, 0.0, 15.0, 0.0), 
            List.of("spring", "summer", "fall", "winter")));

        // Fish
        foodDatabase.put("salmon", new FoodItem("salmon", FoodCategory.FISH, 
            Set.of(Allergen.FISH), new NutritionalInfo(208, 20.0, 0.0, 13.0, 0.0), 
            List.of("spring", "summer")));
        
        foodDatabase.put("fish", new FoodItem("fish", FoodCategory.FISH, 
            Set.of(Allergen.FISH), new NutritionalInfo(206, 22.0, 0.0, 12.0, 0.0), 
            List.of("spring", "summer", "fall")));

        // Eggs
        foodDatabase.put("egg", new FoodItem("egg", FoodCategory.DAIRY, 
            Set.of(Allergen.EGGS), new NutritionalInfo(155, 13.0, 1.1, 11.0, 0.0), 
            List.of("spring", "summer", "fall", "winter")));

        // Grains
        foodDatabase.put("bread", new FoodItem("bread", FoodCategory.GRAIN, 
            Set.of(Allergen.GLUTEN, Allergen.WHEAT), new NutritionalInfo(265, 9.0, 49.0, 3.2, 2.7), 
            List.of("spring", "summer", "fall", "winter")));
    }

    public FoodItem getFoodItem(String foodName) {
        return foodDatabase.get(foodName.toLowerCase());
    }

    public List<FoodItem> listFoods(FoodCategory category, Allergen allergenFree, String season) {
        return foodDatabase.values().stream()
            .filter(food -> category == null || food.getCategory() == category)
            .filter(food -> allergenFree == null || !food.getAllergens().contains(allergenFree))
            .filter(food -> season == null || food.getAvailableSeasons().contains(season.toLowerCase()))
            .collect(Collectors.toList());
    }

    public boolean rateFood(String foodName, int rating) {
        FoodItem food = foodDatabase.get(foodName.toLowerCase());
        if (food == null) {
            return false;
        }
        food.addRating(rating);
        return true;
    }

    public boolean deleteFood(String foodName) {
        return foodDatabase.remove(foodName.toLowerCase()) != null;
    }

    public Set<Allergen> getFoodAllergens(String foodName) {
        FoodItem food = foodDatabase.get(foodName.toLowerCase());
        return food != null ? food.getAllergens() : null;
    }

    public List<FoodItem> getSafeFoodsForAllergy(Allergen allergyType) {
        return foodDatabase.values().stream()
            .filter(food -> !food.getAllergens().contains(allergyType))
            .collect(Collectors.toList());
    }

    public boolean flagFood(String foodName, String reason) {
        FoodItem food = foodDatabase.get(foodName.toLowerCase());
        if (food == null) {
            return false;
        }
        flaggedFoods.computeIfAbsent(foodName.toLowerCase(), k -> new ArrayList<>()).add(reason);
        return true;
    }
}
