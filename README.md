# OPA Policies for Spring Boot Application

## Overview

This project demonstrates how to integrate Open Policy Agent (OPA) with a Spring Boot application to enforce
comprehensive access control policies. It serves as a learning playground for Java developers to use AI-assisted tools (
like GitHub Copilot) to generate OPA policies and test cases from English descriptions.

## Use Case

1. Learning about GenAI and OPA policies
2. Using AI to generate OPA policies from test cases
3. Exploring production-like authorization scenarios
4. Understanding advanced OPA features through practical examples

## Before You Start

Start the dependencies with Podman Compose:

```bash
podman-compose -f auth-server/podman-compose.yml up
```

OPA loads all policies on startup from `src/main/policies/` (mounted into the OPA container). Make sure this folder
contains the policies you want to evaluate.

## New Features

### 1. Enhanced Food Management

- View food details with nutritional information
- List foods with filtering (category, allergen-free, season)
- Rate food items
- Admin-only food deletion
- Allergen information queries
- Food flagging for review

### 2. Recipe Management

- Create recipes (chef/admin only)
- Public and private recipe visibility
- Resource ownership (users own their recipes)
- Add/remove ingredients with dietary validation
- Recipe compatibility checking
- Filter recipes by difficulty, cuisine, prep time

### 3. Meal Planning

- Create meal plans with time restrictions (6 AM - 10 PM)
- Update meal plans before meal time
- Delete future meal plans only
- Nutritionist can manage client meal plans
- Filter by date and meal type

### 4. User Profiles

- View dietary restrictions and preferences
- Nutritionist and admin can view any profile
- Users can view their own profile

## OPA Features Demonstrated

| Feature                       | Description                          | Example Endpoint                                  |
|-------------------------------|--------------------------------------|---------------------------------------------------|
| **Basic RBAC**                | Role-based access control            | `DELETE /food/{foodName}` (admin only)            |
| **ABAC**                      | Attribute-based decisions            | `GET /food/{foodName}` (dietary restrictions)     |
| **Resource Ownership**        | Users own their resources            | `PUT /recipes/{recipeId}` (owner only)            |
| **Time-Based Policies**       | Access restricted by time            | `POST /meal-plans/{userId}` (6 AM - 10 PM)        |
| **Data Filtering**            | Filter results by permissions        | `GET /recipes` (public + own private)             |
| **Conditional Access**        | Different rules for different states | `GET /recipes/{recipeId}` (public vs private)     |
| **Cross-Resource Validation** | Validate across multiple resources   | `POST /recipes/{recipeId}/ingredients/{foodName}` |
| **Role Hierarchies**          | Admin/nutritionist override          | `GET /meal-plans/{userId}`                        |
| **Policy Composition**        | Modular policy organization          | `combined_enhanced.rego`                          |

## User Roles

### Dietary Roles

- `vegan`: Plant-based foods only
- `vegetarian`: No meat/fish, yes dairy/eggs
- `omnivorous`: All foods
- `pescatarian`: No meat, yes fish
- `fruitarian`: Fruits only

### System Roles

- `admin`: Full system access
- `nutritionist`: Manage client meal plans and profiles
- `chef`: Create and manage recipes
- `user`: Basic authenticated user

### Health Roles

- `gluten_free`: No gluten-containing foods
- `lactose_intolerant`: No dairy
- `nut_allergy`: No nuts
- `shellfish_allergy`: No shellfish

## Prerequisites

- Java 17
- Maven
- Podman or Docker
- OPA (Open Policy Agent)
- Keycloak (for authentication)

## Setup

### 1. Clone the repository

```bash
git clone <repository-url>
cd opa-policies
```

### 2. Start OPA and Keycloak

```bash
podman-compose -f auth-server/podman-compose.yml up
```

OPA will load policies from `src/main/policies/` when it starts.

### 3. Upload policies to OPA (optional if you use the mounted policies)

```bash
# Upload original policy
curl -X PUT --data-binary @src/main/policies/policy.rego \
  http://localhost:8181/v1/policies/food_security

# Upload enhanced policies
curl -X PUT --data-binary @src/main/policies/food_enhanced_security.rego \
  http://localhost:8181/v1/policies/food_enhanced_security

curl -X PUT --data-binary @src/main/policies/recipe_security.rego \
  http://localhost:8181/v1/policies/recipe_security

curl -X PUT --data-binary @src/main/policies/meal_plan_security.rego \
  http://localhost:8181/v1/policies/meal_plan_security

curl -X PUT --data-binary @src/main/policies/user_profile_security.rego \
  http://localhost:8181/v1/policies/user_profile_security

# Upload combined policy
curl -X PUT --data-binary @src/main/policies/combined_enhanced.rego \
  http://localhost:8181/v1/policies/combined_enhanced
```

### 4. Run the application

```bash
mvn spring-boot:run
```

### 5. Run tests

```bash
mvn test
```

## Postman Collection

A Postman collection is included to exercise Keycloak, the application endpoints, and OPA:

- File: `OPA Playground.postman_collection.json`
- What it does:
    - Fetches tokens from Keycloak
    - Calls application endpoints with the token
    - Calls OPA endpoints for policy checks

### Importing the collection

1. Open Postman.
2. Click **Import**.
3. Select `OPA Playground.postman_collection.json`.

### Using the collection

1. Ensure an environment or globals are set for these variables (defaults are in the collection):
    - `app_base_url`, `keycloak_base_url`, `keycloak_realm`, `opa_base_url`, `opa_policy`
2. Run **Keycloak → Obtain Token** to populate the global `token`.
3. Run any endpoint that uses Bearer auth (e.g., **Food (Enhanced) → GET Foods (Filtered)**).

## Practice Tests (Intentionally Failing)

There are failing tests you can use out of the box to practice writing policies. Examples include:

- `testPescatarianCanAccessFishButNoMeat`
- `testUnauthenticatedUserCannotRate`

These are meant to guide policy implementation and debugging.

## TDD Workflow with AI

This project is designed to support a Test-Driven Development workflow with AI assistance:

### Step 1: Create/Modify Endpoint

```java

@GetMapping("/recipes/{recipeId}")
public ResponseEntity<Recipe> getRecipe(@PathVariable UUID recipeId) {
    Recipe recipe = recipeService.getRecipe(recipeId);
    return ResponseEntity.ok(recipe);
}
```

### Step 2: Ask AI to Generate Test Cases

**Prompt**: "Generate test cases for GET /recipes/{recipeId} endpoint that includes:

- Public recipes accessible to all
- Private recipes accessible only to owner or admin
- Non-owner cannot access private recipes"

### Step 3: Verify Generated Tests

```java

@Test
void testNonOwnerCannotViewPrivateRecipe() throws Exception {
    String token = authenticator.getAccessToken("client_vegan_user1");

    mockMvc.perform(get("/recipes/{recipeId}", "550e8400-e29b-41d4-a716-446655440001")
                    .header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden());
}
```

### Step 4: Run Tests (They Should Fail)

```bash
mvn test
```

### Step 5: Ask AI to Generate OPA Policy

**Prompt**: "Generate OPA policy for GET /recipes/{recipeId} that allows:

- All users to view public recipes
- Only owner to view private recipes
- Admin to view all recipes"

### Step 6: Update Policy File

Copy the generated policy to `src/main/policies/recipe_security.rego`

### Step 7: Upload Policy to OPA

```bash
curl -X PUT --data-binary @src/main/policies/recipe_security.rego \
  http://localhost:8181/v1/policies/recipe_security
```

### Step 8: Run Tests Again (They Should Pass)

```bash
mvn test
```

## API Endpoints

### Original Endpoints

- `GET /food/onion` - Accessible by: vegan, vegetarian, omnivorous
- `GET /food/milk` - Accessible by: vegetarian, omnivorous
- `GET /food/beef` - Accessible by: omnivorous

### New Food Endpoints

- `GET /food/{foodName}` - Get food details (dietary restrictions apply)
- `GET /food` - List foods with filters
- `POST /food/{foodName}/rate` - Rate food (authenticated users)
- `DELETE /food/{foodName}` - Delete food (admin only)
- `GET /food/{foodName}/allergens` - Get allergen info
- `GET /food/safe-for/{allergyType}` - List safe foods
- `POST /food/{foodName}/flag` - Flag food (nutritionist/admin)

### Recipe Endpoints

- `GET /recipes/{recipeId}` - Get recipe (public or owned)
- `GET /recipes` - List recipes with filters
- `POST /recipes` - Create recipe (chef/admin)
- `PUT /recipes/{recipeId}` - Update recipe (owner/admin)
- `DELETE /recipes/{recipeId}` - Delete recipe (owner/admin)
- `POST /recipes/{recipeId}/ingredients/{foodName}` - Add ingredient
- `DELETE /recipes/{recipeId}/ingredients/{foodName}` - Remove ingredient
- `GET /recipes/{recipeId}/compatibility` - Check compatibility

### Meal Plan Endpoints

- `GET /meal-plans/{userId}` - Get meal plans (own or nutritionist/admin)
- `POST /meal-plans/{userId}` - Create meal plan (6 AM - 10 PM)
- `PUT /meal-plans/{userId}/{mealPlanId}` - Update meal plan (before meal time)
- `DELETE /meal-plans/{userId}/{mealPlanId}` - Delete meal plan (future only)

### User Profile Endpoints

- `GET /users/{userId}/dietary-profile` - Get dietary profile (own or nutritionist/admin)

## Testing OPA Policies

### Run OPA Policy Tests

```bash
opa test src/main/policies/ src/test/policies/
```

### Test Specific Policy

```bash
opa test src/main/policies/recipe_security.rego
```

### Query Policy Directly

```bash
curl -X POST http://localhost:8181/v1/data/recipe_security/allow \
  -H 'Content-Type: application/json' \
  -d '{
    "input": {
      "path": "/recipes/550e8400-e29b-41d4-a716-446655440000",
      "action": {"name": "GET"},
      "subject": {
        "name": "chef1",
        "authorities": ["ROLE_chef"]
      },
      "resource": {
        "createdBy": "chef1",
        "isPublic": false
      }
    }
  }'
```

## Learning Path

1. **Start Simple**: Review original food endpoints and policies
2. **Understand RBAC**: Study admin-only deletion endpoint
3. **Learn ABAC**: Explore dietary restriction-based access
4. **Resource Ownership**: Examine recipe management policies
5. **Time-Based**: Study meal planning time restrictions
6. **Policy Composition**: Review combined policy structure
7. **Advanced**: Implement your own endpoints and policies

## Example Scenarios

### Scenario 1: Vegan Chef Creates Recipe

```bash
# Chef creates a vegan recipe
POST /recipes?name=Vegan%20Pasta&difficulty=INTERMEDIATE&cuisine=Italian&is-public=true

# Chef adds vegetables (allowed)
POST /recipes/{recipeId}/ingredients/carrot?quantity=2&unit=pieces

# Chef tries to add meat (denied by policy)
POST /recipes/{recipeId}/ingredients/beef?quantity=200&unit=grams
# Result: 403 Forbidden
```

### Scenario 2: User Plans Meals

```bash
# User creates meal plan during allowed hours (8 AM)
POST /meal-plans/user1?date=2024-01-15&meal-type=BREAKFAST&recipe-id={id}&servings=1
# Result: 201 Created

# User tries to create meal plan at 2 AM (denied)
POST /meal-plans/user1?date=2024-01-15&meal-type=SNACK&recipe-id={id}
# Result: 403 Forbidden (outside planning hours)
# Result: 403 Forbidden (outside planning hours)
```

### Scenario 3: Nutritionist Manages Client

```bash
# Nutritionist views client's dietary profile
GET /users/client1/dietary-profile
# Result: 200 OK

# Nutritionist creates meal plan for client
POST /meal-plans/client1?date=2024-01-16&meal-type=LUNCH&recipe-id={id}
# Result: 201 Created
```

## Troubleshooting

### Tests Failing

1. Ensure OPA is running: `curl http://localhost:8181/health`
2. Check policies are uploaded: `curl http://localhost:8181/v1/policies`
3. Verify Keycloak is running: `curl http://localhost:7070/health`

### Policy Not Working

1. Check policy syntax: `opa check src/main/policies/policy.rego`
2. Test policy locally: `opa test src/main/policies/`
3. View OPA logs: `docker logs opa-container`

## Contributing

When adding new endpoints:

1. Design endpoint with clear authorization requirements
2. Write test cases (positive and negative scenarios)
3. Use AI to generate OPA policies
4. Test policies with OPA test framework
5. Document OPA features demonstrated
6. Update this README

## Resources

- [OPA Documentation](https://www.openpolicyagent.org/docs/latest/)
- [Rego Language Reference](https://www.openpolicyagent.org/docs/latest/policy-reference/)
- [Spring Security with OPA](https://www.openpolicyagent.org/docs/latest/spring-security/)
- [OPA Playground](https://play.openpolicyagent.org/)

## License

[LICENSE](LICENSE)