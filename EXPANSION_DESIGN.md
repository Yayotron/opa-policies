# OPA Policies Expansion Design

## Overview

This document outlines the expansion of the food domain with production-like endpoints designed to explore advanced OPA policy features.

## Design Principles

1. **Realistic Scenarios**: Model real-world food management use cases
2. **OPA Feature Coverage**: Demonstrate ABAC, time-based policies, ownership, data filtering, and policy composition
3. **RESTful Design**: Use path variables and query parameters; minimize request bodies
4. **Multiple HTTP Methods**: GET, POST, PUT, DELETE operations
5. **Learning-Focused**: Each endpoint teaches specific OPA concepts

## New Domain Concepts

### 1. Food Items (Enhanced)
- **Attributes**: name, category, allergens, nutritional info, season availability
- **Categories**: vegetable, fruit, meat, fish, dairy, grain, legume, nut, spice
- **Allergens**: gluten, dairy, nuts, shellfish, soy, eggs

### 2. Recipes
- **Attributes**: id, name, ingredients, difficulty, prepTime, cuisine, createdBy, isPublic
- **Difficulty Levels**: beginner, intermediate, advanced
- **Ownership**: Users can create and manage their own recipes

### 3. Meal Plans
- **Attributes**: id, userId, date, mealType, recipeId, servings
- **Meal Types**: breakfast, lunch, dinner, snack
- **Time-Based**: Access restricted by business hours or meal times

### 4. User Profiles (Implicit via Roles)
- **Dietary Roles**: vegan, vegetarian, omnivorous, pescatarian, fruitarian, keto, paleo
- **Health Roles**: gluten_free, lactose_intolerant, nut_allergy, shellfish_allergy
- **System Roles**: admin, nutritionist, chef, user

## Endpoint Design

### Food Management Endpoints

#### 1. GET /food/{foodName}
- **Purpose**: Retrieve food item details
- **Authorization**: Based on dietary restrictions
- **OPA Features**: Basic RBAC, attribute-based filtering

#### 2. GET /food
- **Query Params**: category, allergen-free, season
- **Purpose**: List foods with filtering
- **Authorization**: Filter results based on user dietary restrictions
- **OPA Features**: Data filtering, partial evaluation

#### 3. POST /food/{foodName}/rate
- **Query Params**: rating (1-5)
- **Purpose**: Rate a food item
- **Authorization**: Authenticated users only
- **OPA Features**: Simple authentication check

#### 4. DELETE /food/{foodName}
- **Purpose**: Remove a food item (admin only)
- **Authorization**: Admin role required
- **OPA Features**: Role-based access control

### Recipe Management Endpoints

#### 5. GET /recipes/{recipeId}
- **Purpose**: Retrieve recipe details
- **Authorization**: Public recipes accessible to all; private recipes only by owner or admin
- **OPA Features**: Resource ownership, conditional access

#### 6. GET /recipes
- **Query Params**: difficulty, cuisine, max-prep-time, public-only
- **Purpose**: List recipes with filtering
- **Authorization**: Filter to show only accessible recipes
- **OPA Features**: Data filtering based on ownership and visibility

#### 7. POST /recipes
- **Query Params**: name, difficulty, cuisine, is-public
- **Purpose**: Create a new recipe
- **Authorization**: Chef or admin roles
- **OPA Features**: Role-based creation rights

#### 8. PUT /recipes/{recipeId}
- **Query Params**: difficulty, is-public
- **Purpose**: Update recipe metadata
- **Authorization**: Owner or admin only
- **OPA Features**: Resource ownership validation

#### 9. DELETE /recipes/{recipeId}
- **Purpose**: Delete a recipe
- **Authorization**: Owner or admin only
- **OPA Features**: Resource ownership validation

#### 10. POST /recipes/{recipeId}/ingredients/{foodName}
- **Query Params**: quantity, unit
- **Purpose**: Add ingredient to recipe
- **Authorization**: Owner or admin; food must be compatible with recipe creator's dietary restrictions
- **OPA Features**: Complex ABAC with cross-resource validation

#### 11. DELETE /recipes/{recipeId}/ingredients/{foodName}
- **Purpose**: Remove ingredient from recipe
- **Authorization**: Owner or admin only
- **OPA Features**: Resource ownership

### Meal Planning Endpoints

#### 12. GET /meal-plans/{userId}
- **Query Params**: date, meal-type
- **Purpose**: Retrieve user's meal plans
- **Authorization**: Own meal plans or nutritionist/admin roles
- **OPA Features**: Resource ownership, role-based override

#### 13. POST /meal-plans/{userId}
- **Query Params**: date, meal-type, recipe-id, servings
- **Purpose**: Create a meal plan entry
- **Authorization**: Own meal plans or nutritionist role; must be within planning hours (6 AM - 10 PM)
- **OPA Features**: Time-based policies, ownership, role-based access

#### 14. PUT /meal-plans/{userId}/{mealPlanId}
- **Query Params**: servings
- **Purpose**: Update meal plan servings
- **Authorization**: Own meal plans; must be before meal time
- **OPA Features**: Time-based validation, ownership

#### 15. DELETE /meal-plans/{userId}/{mealPlanId}
- **Purpose**: Delete a meal plan entry
- **Authorization**: Own meal plans or nutritionist; must be future meal
- **OPA Features**: Time-based validation, ownership

### Food Restrictions Endpoints

#### 16. GET /food/{foodName}/allergens
- **Purpose**: Get allergen information for a food
- **Authorization**: All authenticated users
- **OPA Features**: Simple authentication

#### 17. GET /food/safe-for/{allergyType}
- **Purpose**: List foods safe for specific allergy
- **Authorization**: All authenticated users
- **OPA Features**: Data filtering based on attributes

#### 18. GET /recipes/{recipeId}/compatibility
- **Query Params**: user-id
- **Purpose**: Check if recipe is compatible with user's dietary restrictions
- **Authorization**: Nutritionist, admin, or checking own compatibility
- **OPA Features**: Complex ABAC with multi-attribute validation

### Nutritionist/Admin Endpoints

#### 19. GET /users/{userId}/dietary-profile
- **Purpose**: View user's dietary restrictions and preferences
- **Authorization**: Nutritionist, admin, or own profile
- **OPA Features**: Role-based access with ownership

#### 20. POST /food/{foodName}/flag
- **Query Params**: reason
- **Purpose**: Flag a food item for review
- **Authorization**: Nutritionist or admin only
- **OPA Features**: Role-based access

## OPA Policy Structure

### Policy Modules

1. **food_security.rego**: Basic food access policies
2. **recipe_security.rego**: Recipe ownership and visibility policies
3. **meal_plan_security.rego**: Meal planning with time-based policies
4. **allergen_security.rego**: Allergen and dietary restriction policies
5. **admin_security.rego**: Administrative operation policies
6. **combined.rego**: Import and compose all policies

### Key OPA Features Demonstrated

| Feature | Endpoints | Description |
|---------|-----------|-------------|
| **Basic RBAC** | GET /food/{foodName}, DELETE /food/{foodName} | Role-based access control |
| **ABAC** | POST /recipes/{recipeId}/ingredients/{foodName} | Attribute-based decisions using food properties |
| **Resource Ownership** | PUT /recipes/{recipeId}, DELETE /recipes/{recipeId} | Users can only modify their own resources |
| **Time-Based Policies** | POST /meal-plans/{userId}, PUT /meal-plans/{userId}/{mealPlanId} | Access restricted by time of day or meal timing |
| **Data Filtering** | GET /recipes, GET /food | Filter results based on user permissions |
| **Conditional Access** | GET /recipes/{recipeId} | Different access rules for public vs private resources |
| **Cross-Resource Validation** | GET /recipes/{recipeId}/compatibility | Validate against multiple resource attributes |
| **Role Hierarchies** | GET /meal-plans/{userId} | Admin/nutritionist can override ownership |
| **Policy Composition** | combined.rego | Import and combine multiple policy modules |

## User Roles and Permissions Matrix

| Role | Food Access | Recipe Create | Recipe View | Meal Plan Own | Meal Plan Others | Admin Ops |
|------|-------------|---------------|-------------|---------------|------------------|-----------|
| **vegan** | Plant-based only | ✓ (if chef) | Public + Own | ✓ | ✗ | ✗ |
| **vegetarian** | No meat/fish | ✓ (if chef) | Public + Own | ✓ | ✗ | ✗ |
| **omnivorous** | All foods | ✓ (if chef) | Public + Own | ✓ | ✗ | ✗ |
| **pescatarian** | No meat, yes fish | ✓ (if chef) | Public + Own | ✓ | ✗ | ✗ |
| **gluten_free** | No gluten | ✓ (if chef) | Public + Own | ✓ | ✗ | ✗ |
| **chef** | Based on diet | ✓ | Public + Own | ✓ | ✗ | ✗ |
| **nutritionist** | All | ✓ | All | ✓ | ✓ (read/write) | Limited |
| **admin** | All | ✓ | All | ✓ | ✓ (read/write) | ✓ |

## Implementation Plan

### Phase 1: Core Models and Controllers
- Create domain models (Recipe, MealPlan, FoodItem)
- Implement controllers with all endpoints
- Add DTOs for request/response

### Phase 2: Test Cases
- Write comprehensive integration tests for each endpoint
- Cover positive and negative scenarios
- Test all role combinations

### Phase 3: OPA Policies
- Create modular policy files
- Implement each feature incrementally
- Test policies with OPA test framework

### Phase 4: Documentation
- Update README with new endpoints
- Add API documentation
- Create policy explanation guide

## Example Scenarios for Testing

### Scenario 1: Recipe Creation and Sharing
1. Chef creates a private recipe
2. Chef adds ingredients (must be compatible with their diet)
3. Chef makes recipe public
4. Other users can now view the recipe

### Scenario 2: Meal Planning with Time Restrictions
1. User creates meal plan for tomorrow's breakfast (allowed during planning hours)
2. User tries to create meal plan at 2 AM (denied - outside planning hours)
3. User updates meal plan before breakfast time (allowed)
4. User tries to update meal plan after breakfast time (denied)

### Scenario 3: Allergen Safety
1. User with nut allergy queries safe foods
2. User views recipe compatibility
3. Chef tries to add nuts to recipe intended for nut-allergy users (policy consideration)

### Scenario 4: Administrative Override
1. Admin views any user's meal plans
2. Admin deletes inappropriate recipe
3. Nutritionist updates client's meal plan

## Technical Considerations

### Path Variables vs Query Parameters
- **Path Variables**: Resource identifiers (foodName, recipeId, userId)
- **Query Parameters**: Filters, options, and simple data (rating, date, servings)

### Request Bodies
- Minimize usage as requested
- Use only for complex recipe creation if absolutely necessary
- Prefer query parameters for simple data

### Response Format
- Consistent JSON structure
- Include relevant metadata
- Filter sensitive information based on authorization

## Next Steps

1. Review and approve design
2. Implement domain models
3. Create controllers with endpoints
4. Write integration tests
5. Develop OPA policies
6. Update documentation
