package com.capgemini.opapolicies.api;

import com.capgemini.opapolicies.service.UserProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * GET /users/{userId}/dietary-profile
     * View user's dietary restrictions and preferences
     * Accessible by: nutritionist, admin, or own profile
     */
    @GetMapping("/{userId}/dietary-profile")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<String, Object>> getDietaryProfile(@PathVariable String userId) {
        Map<String, Object> profile = userProfileService.getDietaryProfile(userId);
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(profile);
    }
}
