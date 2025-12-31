package com.capgemini.opapolicies.service;

import com.capgemini.opapolicies.domain.MealPlan;
import com.capgemini.opapolicies.domain.MealType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MealPlanService {

    private final Map<UUID, MealPlan> mealPlanDatabase = new HashMap<>();
    private final RecipeService recipeService;

    public MealPlanService(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    public List<MealPlan> getMealPlans(String userId, LocalDate date, MealType mealType) {
        return mealPlanDatabase.values().stream()
            .filter(mp -> mp.getUserId().equals(userId))
            .filter(mp -> date == null || mp.getDate().equals(date))
            .filter(mp -> mealType == null || mp.getMealType() == mealType)
            .collect(Collectors.toList());
    }

    public MealPlan createMealPlan(String userId, LocalDate date, MealType mealType, 
                                   UUID recipeId, int servings) {
        // Check if recipe exists
        if (recipeService.getRecipe(recipeId) == null) {
            return null;
        }

        // Determine scheduled time based on meal type
        LocalTime scheduledTime = getScheduledTimeForMealType(mealType);

        UUID mealPlanId = UUID.randomUUID();
        MealPlan mealPlan = new MealPlan(mealPlanId, userId, date, mealType, recipeId, servings, scheduledTime);
        mealPlanDatabase.put(mealPlanId, mealPlan);
        return mealPlan;
    }

    public MealPlan updateMealPlan(String userId, UUID mealPlanId, int servings) {
        MealPlan mealPlan = mealPlanDatabase.get(mealPlanId);
        if (mealPlan == null || !mealPlan.getUserId().equals(userId)) {
            return null;
        }
        mealPlan.setServings(servings);
        return mealPlan;
    }

    public boolean deleteMealPlan(String userId, UUID mealPlanId) {
        MealPlan mealPlan = mealPlanDatabase.get(mealPlanId);
        if (mealPlan == null || !mealPlan.getUserId().equals(userId)) {
            return false;
        }
        return mealPlanDatabase.remove(mealPlanId) != null;
    }

    private LocalTime getScheduledTimeForMealType(MealType mealType) {
        return switch (mealType) {
            case BREAKFAST -> LocalTime.of(8, 0);
            case LUNCH -> LocalTime.of(12, 0);
            case DINNER -> LocalTime.of(19, 0);
            case SNACK -> LocalTime.of(15, 0);
        };
    }

    public Map<UUID, MealPlan> getMealPlanDatabase() {
        return mealPlanDatabase;
    }
}
