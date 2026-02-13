package com.capgemini.opapolicies.api;

import com.capgemini.opapolicies.domain.Allergen;
import com.capgemini.opapolicies.domain.FoodCategory;
import com.capgemini.opapolicies.domain.FoodItem;
import com.capgemini.opapolicies.service.FoodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/food")
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    /**
     * GET /food/{foodName}
     * Retrieve food item details
     */
    @GetMapping("/{foodName}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<FoodItem> getFoodItem(@PathVariable String foodName) {
        FoodItem foodItem = foodService.getFoodItem(foodName);
        if (foodItem == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foodItem);
    }

    /**
     * GET /food
     * List foods with optional filtering
     * Query params: category, allergen-free, season
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<FoodItem>> listFoods(
            @RequestParam(required = false) FoodCategory category,
            @RequestParam(name = "allergen-free", required = false) Allergen allergenFree,
            @RequestParam(required = false) String season) {
        List<FoodItem> foods = foodService.listFoods(category, allergenFree, season);
        return ResponseEntity.ok(foods);
    }

    /**
     * POST /food/{foodName}/rate
     * Rate a food item (1-5)
     */
    @PostMapping("/{foodName}/rate")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> rateFood(
            @PathVariable String foodName,
            @RequestParam int rating) {
        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().body("Rating must be between 1 and 5");
        }
        boolean success = foodService.rateFood(foodName, rating);
        if (!success) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("Rating submitted successfully");
    }

    /**
     * DELETE /food/{foodName}
     * Remove a food item (admin only)
     */
    @DeleteMapping("/{foodName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteFood(@PathVariable String foodName) {
        boolean success = foodService.deleteFood(foodName);
        if (!success) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /food/{foodName}/allergens
     * Get allergen information for a food
     */
    @GetMapping("/{foodName}/allergens")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Set<Allergen>> getFoodAllergens(@PathVariable String foodName) {
        Set<Allergen> allergens = foodService.getFoodAllergens(foodName);
        if (allergens == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allergens);
    }

    /**
     * GET /food/safe-for/{allergyType}
     * List foods safe for specific allergy
     */
    @GetMapping("/safe-for/{allergyType}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<FoodItem>> getSafeFoods(@PathVariable Allergen allergyType) {
        List<FoodItem> safeFoods = foodService.getSafeFoodsForAllergy(allergyType);
        return ResponseEntity.ok(safeFoods);
    }

    /**
     * POST /food/{foodName}/flag
     * Flag a food item for review (nutritionist/admin only)
     */
    @PostMapping("/{foodName}/flag")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> flagFood(
            @PathVariable String foodName,
            @RequestParam String reason) {
        boolean success = foodService.flagFood(foodName, reason);
        if (!success) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("Food item flagged for review");
    }
}
