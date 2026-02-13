package com.capgemini.opapolicies.service;

import com.capgemini.opapolicies.domain.Allergen;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserProfileService {

    // In-memory storage for demo purposes
    private final Map<String, Map<String, Object>> userProfiles = new HashMap<>();

    public UserProfileService() {
        initializeUserProfiles();
    }

    private void initializeUserProfiles() {
        // Sample user profiles
        Map<String, Object> user1 = new HashMap<>();
        user1.put("userId", "user1");
        user1.put("dietaryPreferences", List.of("vegan"));
        user1.put("allergies", List.of());
        user1.put("healthConditions", List.of());
        userProfiles.put("user1", user1);

        Map<String, Object> user2 = new HashMap<>();
        user2.put("userId", "user2");
        user2.put("dietaryPreferences", List.of("vegetarian"));
        user2.put("allergies", List.of(Allergen.NUTS));
        user2.put("healthConditions", List.of());
        userProfiles.put("user2", user2);

        Map<String, Object> user3 = new HashMap<>();
        user3.put("userId", "user3");
        user3.put("dietaryPreferences", List.of("omnivorous"));
        user3.put("allergies", List.of(Allergen.SHELLFISH, Allergen.DAIRY));
        user3.put("healthConditions", List.of("lactose_intolerant"));
        userProfiles.put("user3", user3);
    }

    public Map<String, Object> getDietaryProfile(String userId) {
        return userProfiles.get(userId);
    }
}
