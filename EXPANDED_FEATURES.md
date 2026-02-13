# Expanded OPA Policies Project - Feature Documentation

## Overview

This document describes the expanded features added to the OPA policies project, designed to help Java developers learn advanced OPA policy concepts through practical examples.

## New Endpoints Summary

### Food Management (7 endpoints)

| Endpoint | Method | Description | OPA Features |
|----------|--------|-------------|--------------|
| `/food/{foodName}` | GET | Get food item details | Basic RBAC, dietary restrictions |
| `/food` | GET | List foods with filters | Data filtering, query parameters |
| `/food/{foodName}/rate` | POST | Rate a food item | Authentication check |
| `/food/{foodName}` | DELETE | Delete food item | Admin-only access |
| `/food/{foodName}/allergens` | GET | Get allergen info | Simple authentication |
| `/food/safe-for/{allergyType}` | GET | List safe foods | Attribute-based filtering |
| `/food/{foodName}/flag` | POST | Flag food for review | Role-based (nutritionist/admin) |

### Recipe Management (7 endpoints)

| Endpoint | Method | Description | OPA Features |
|----------|--------|-------------|--------------|
| `/recipes/{recipeId}` | GET | Get recipe details | Resource ownership, visibility |
| `/recipes` | GET | List recipes | Data filtering |
| `/recipes` | POST | Create recipe | Role-based (chef/admin) |
| `/recipes/{recipeId}` | PUT | Update recipe | Resource ownership |
| `/recipes/{recipeId}` | DELETE | Delete recipe | Resource ownership |
| `/recipes/{recipeId}/ingredients/{foodName}` | POST | Add ingredient | Cross-resource validation |
| `/recipes/{recipeId}/ingredients/{foodName}` | DELETE | Remove ingredient | Resource ownership |
| `/recipes/{recipeId}/compatibility` | GET | Check compatibility | Multi-user access control |

### Meal Planning (4 endpoints)

| Endpoint | Method | Description | OPA Features |
|----------|--------|-------------|--------------|
| `/meal-plans/{userId}` | GET | Get meal plans | Resource ownership, role override |
| `/meal-plans/{userId}` | POST | Create meal plan | Time-based policy (6 AM - 10 PM) |
| `/meal-plans/{userId}/{mealPlanId}` | PUT | Update meal plan | Time-based (before meal time) |
| `/meal-plans/{userId}/{mealPlanId}` | DELETE | Delete meal plan | Future-only deletion |

### User Profile (1 endpoint)

| Endpoint | Method | Description | OPA Features |
|----------|--------|-------------|--------------|
| `/users/{userId}/dietary-profile` | GET | Get dietary profile | Self-access, role-based override |

## OPA Policy Features Demonstrated

### 1. Basic Role-Based Access Control (RBAC)

**Example**: Admin-only food deletion

```rego
allow if {
    startswith(input.path, "/food/")
    input.action.name == "DELETE"
    has_role("admin")
}
```

**Test Case**: `EnhancedFoodControllerSecurityTest.testAdminCanDeleteFood()`

### 2. Attribute-Based Access Control (ABAC)

**Example**: Dietary restriction-based food access

```rego
allow if {
    input.path != "/food"
    startswith(input.path, "/food/")
    input.action.name == "GET"
    food_name := get_food_name
    
    some diet in ["vegan", "vegetarian", "omnivorous", "pescatarian", "fruitarian"]
    user_has_diet(diet)
    is_food_compatible_with_diet(food_name, diet)
}
```

**Test Case**: `EnhancedFoodControllerSecurityTest.testVeganCannotAccessDairy()`

### 3. Resource Ownership

**Example**: Recipe modification by owner

```rego
allow if {
    startswith(input.path, "/recipes/")
    input.action.name == "PUT"
    is_recipe_owner
}

is_recipe_owner if {
    input.resource.createdBy == input.subject.name
}
```

**Test Case**: `RecipeControllerSecurityTest.testOwnerCanUpdateOwnRecipe()`

### 4. Time-Based Policies

**Example**: Meal planning hours (6 AM - 10 PM)

```rego
allow if {
    startswith(input.path, "/meal-plans/")
    input.action.name == "POST"
    is_own_meal_plan
    is_within_planning_hours
}

is_within_planning_hours if {
    current_hour := input.context.currentHour
    current_hour >= 6
    current_hour < 22
}
```

**Test Case**: `MealPlanControllerSecurityTest.testUserCanCreateOwnMealPlanDuringPlanningHours()`

### 5. Data Filtering

**Example**: Filter recipes by visibility

```rego
allow if {
    input.path == "/recipes"
    input.action.name == "GET"
    count(input.subject.authorities) > 0
}
```

Application-level filtering ensures users only see:
- Public recipes
- Their own private recipes
- All recipes (if admin)

**Test Case**: `RecipeControllerSecurityTest.testListPublicRecipesOnly()`

### 6. Conditional Access

**Example**: Public vs private recipe access

```rego
allow if {
    startswith(input.path, "/recipes/")
    input.action.name == "GET"
    is_recipe_public
}

allow if {
    startswith(input.path, "/recipes/")
    input.action.name == "GET"
    is_recipe_owner
}
```

**Test Case**: `RecipeControllerSecurityTest.testNonOwnerCannotViewPrivateRecipe()`

### 7. Cross-Resource Validation

**Example**: Adding ingredients compatible with chef's diet

```rego
allow if {
    contains(input.path, "/ingredients/")
    input.action.name == "POST"
    is_recipe_owner
    input.resource.foodCategory
    is_ingredient_compatible_with_chef_diet(input.resource.foodCategory)
}

is_ingredient_compatible_with_chef_diet(food_category) if {
    has_role("vegan")
    food_category in ["VEGETABLE", "FRUIT", "GRAIN", "LEGUME", "SPICE"]
}
```

**Test Case**: `RecipeControllerSecurityTest.testVeganChefCannotAddMeatToRecipe()`

### 8. Role Hierarchies

**Example**: Nutritionist can override ownership for meal plans

```rego
allow if {
    startswith(input.path, "/meal-plans/")
    input.action.name == "GET"
    has_any_role(["nutritionist", "admin"])
}
```

**Test Case**: `MealPlanControllerSecurityTest.testNutritionistCanViewAnyUserMealPlans()`

### 9. Policy Composition

**Example**: Combined policy imports multiple modules

```rego
package combined_enhanced

import data.food_security
import data.food_enhanced_security
import data.recipe_security
import data.meal_plan_security
import data.user_profile_security

allow if {
    food_enhanced_security.allow
}

allow if {
    recipe_security.allow
}
```

This demonstrates modular policy organization and reusability.

## User Roles

### Dietary Roles
- `vegan`: Plant-based foods only
- `vegetarian`: No meat/fish, yes dairy/eggs
- `omnivorous`: All foods
- `pescatarian`: No meat, yes fish
- `fruitarian`: Fruits only
- `keto`: Low-carb, high-fat
- `paleo`: Whole foods, no processed

### Health Roles
- `gluten_free`: No gluten-containing foods
- `lactose_intolerant`: No dairy
- `nut_allergy`: No nuts
- `shellfish_allergy`: No shellfish

### System Roles
- `admin`: Full system access
- `nutritionist`: Manage client meal plans and profiles
- `chef`: Create and manage recipes
- `user`: Basic authenticated user

## Testing Workflow

### 1. Write Test Cases First (TDD Approach)

```java
@Test
void testVeganCannotAccessDairy() throws Exception {
    String token = authenticator.getAccessToken("client_vegan_user1");
    
    mockMvc.perform(get("/food/milk")
            .header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden());
}
```

### 2. Run Tests (They Should Fail)

```bash
mvn test
```

### 3. Ask AI to Generate OPA Policy

**Prompt**: "Generate OPA policy for GET /food/{foodName} endpoint that allows vegans to access only plant-based foods"

### 4. Update Policy File

Copy the generated policy to `src/main/policies/food_enhanced_security.rego`

### 5. Upload Policy to OPA

```bash
curl -X PUT --data-binary @src/main/policies/food_enhanced_security.rego \
  http://localhost:8181/v1/policies/food_enhanced_security
```

### 6. Run Tests Again (They Should Pass)

```bash
mvn test
```

## OPA Policy Testing

You can test policies directly with OPA:

```bash
# Run OPA tests
opa test src/main/policies/ src/test/policies/

# Test specific policy
opa test src/main/policies/food_enhanced_security.rego \
  src/test/policies/test_enhanced_policies.rego
```

## Input Structure for OPA

### Basic Request

```json
{
  "path": "/food/carrot",
  "action": {
    "name": "GET"
  },
  "subject": {
    "name": "vegan_user1",
    "authorities": ["ROLE_vegan"]
  }
}
```

### Request with Resource Context

```json
{
  "path": "/recipes/550e8400-e29b-41d4-a716-446655440000",
  "action": {
    "name": "PUT"
  },
  "subject": {
    "name": "chef1",
    "authorities": ["ROLE_chef"]
  },
  "resource": {
    "createdBy": "chef1",
    "isPublic": false
  }
}
```

### Request with Time Context

```json
{
  "path": "/meal-plans/user1",
  "action": {
    "name": "POST"
  },
  "subject": {
    "name": "user1",
    "authorities": ["ROLE_vegan"]
  },
  "context": {
    "currentHour": 14,
    "currentDate": "2024-01-15"
  }
}
```

## Best Practices Learned

### 1. Modular Policy Organization

Separate policies by domain:
- `food_enhanced_security.rego`: Food-related rules
- `recipe_security.rego`: Recipe-related rules
- `meal_plan_security.rego`: Meal planning rules
- `combined_enhanced.rego`: Composition layer

### 2. Reusable Helper Functions

```rego
has_role(role) if {
    some i
    input.subject.authorities[i] == concat("", ["ROLE_", role])
}

has_any_role(roles) if {
    some role in roles
    has_role(role)
}
```

### 3. Clear Default Deny

```rego
default allow := false
```

Always start with deny-by-default for security.

### 4. Descriptive Rule Names

```rego
# Good
is_recipe_owner if { ... }
is_within_planning_hours if { ... }

# Bad
check1 if { ... }
validate if { ... }
```

### 5. Test Coverage

Write tests for:
- ✅ Positive cases (should allow)
- ✅ Negative cases (should deny)
- ✅ Edge cases (boundary conditions)
- ✅ Role combinations

## Next Steps for Learning

1. **Add More Complex Time Logic**: Implement business hours, holidays, time zones
2. **Implement Data Masking**: Filter sensitive fields based on roles
3. **Add Rate Limiting**: Implement request quotas per user
4. **Create Policy Bundles**: Package policies for different environments
5. **Implement Partial Evaluation**: Pre-compile policies for performance
6. **Add Policy Versioning**: Manage policy changes over time

## Useful OPA Commands

```bash
# Start OPA server
opa run --server --log-level debug

# Upload policy
curl -X PUT --data-binary @policy.rego http://localhost:8181/v1/policies/my_policy

# Query policy
curl -X POST http://localhost:8181/v1/data/my_package/allow \
  -H 'Content-Type: application/json' \
  -d @input.json

# List policies
curl http://localhost:8181/v1/policies

# Delete policy
curl -X DELETE http://localhost:8181/v1/policies/my_policy

# Run tests
opa test path/to/policies/ path/to/tests/

# Format policies
opa fmt -w path/to/policies/

# Check policy syntax
opa check path/to/policy.rego
```

## Resources

- [OPA Documentation](https://www.openpolicyagent.org/docs/latest/)
- [Rego Language Reference](https://www.openpolicyagent.org/docs/latest/policy-reference/)
- [OPA Playground](https://play.openpolicyagent.org/)
- [Policy Testing Guide](https://www.openpolicyagent.org/docs/latest/policy-testing/)

## Contributing

When adding new endpoints:

1. Design the endpoint with clear authorization requirements
2. Write test cases covering all scenarios
3. Generate or write OPA policies
4. Test policies with OPA test framework
5. Document the OPA features demonstrated
6. Update this documentation
