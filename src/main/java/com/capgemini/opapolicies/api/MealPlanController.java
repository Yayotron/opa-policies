package com.capgemini.opapolicies.api;

import com.capgemini.opapolicies.domain.MealPlan;
import com.capgemini.opapolicies.domain.MealType;
import com.capgemini.opapolicies.service.MealPlanService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/meal-plans")
public class MealPlanController {

    private final MealPlanService mealPlanService;

    public MealPlanController(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
    }

    /**
     * GET /meal-plans/{userId}
     * Retrieve user's meal plans
     * Query params: date, meal-type
     */
    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<MealPlan>> getMealPlans(
            @PathVariable String userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(name = "meal-type", required = false) MealType mealType) {
        List<MealPlan> mealPlans = mealPlanService.getMealPlans(userId, date, mealType);
        return ResponseEntity.ok(mealPlans);
    }

    /**
     * POST /meal-plans/{userId}
     * Create a meal plan entry
     * Query params: date, meal-type, recipe-id, servings
     * Must be within planning hours (6 AM - 10 PM)
     */
    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MealPlan> createMealPlan(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(name = "meal-type") MealType mealType,
            @RequestParam(name = "recipe-id") UUID recipeId,
            @RequestParam(defaultValue = "1") int servings) {
        MealPlan mealPlan = mealPlanService.createMealPlan(userId, date, mealType, recipeId, servings);
        if (mealPlan == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(mealPlan);
    }

    /**
     * PUT /meal-plans/{userId}/{mealPlanId}
     * Update meal plan servings
     * Query params: servings
     * Must be before meal time
     */
    @PutMapping("/{userId}/{mealPlanId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MealPlan> updateMealPlan(
            @PathVariable String userId,
            @PathVariable UUID mealPlanId,
            @RequestParam int servings) {
        MealPlan mealPlan = mealPlanService.updateMealPlan(userId, mealPlanId, servings);
        if (mealPlan == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mealPlan);
    }

    /**
     * DELETE /meal-plans/{userId}/{mealPlanId}
     * Delete a meal plan entry
     * Must be future meal
     */
    @DeleteMapping("/{userId}/{mealPlanId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteMealPlan(
            @PathVariable String userId,
            @PathVariable UUID mealPlanId) {
        boolean success = mealPlanService.deleteMealPlan(userId, mealPlanId);
        if (!success) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
