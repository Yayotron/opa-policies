package recipe_security

import rego.v1

# By default, deny requests
default allow := false

# ========== Helper Functions ==========

# Check if user has a specific role
has_role(role) if {
    some i
    input.subject.authorities[i] == concat("", ["ROLE_", role])
}

# Check if user has any of the specified roles
has_any_role(roles) if {
    some role in roles
    has_role(role)
}

# Check if user is the owner of the recipe
is_recipe_owner if {
    input.resource.createdBy == input.subject.name
}

# Check if recipe is public
is_recipe_public if {
    input.resource.isPublic == true
}

# Get recipe ID from path
get_recipe_id if {
    parts := split(input.path, "/")
    count(parts) >= 3
    result := parts[2]
} else := ""

# Check if food is compatible with chef's diet
is_ingredient_compatible_with_chef_diet(food_category) if {
    has_role("vegan")
    food_category in ["VEGETABLE", "FRUIT", "GRAIN", "LEGUME", "SPICE"]
}

is_ingredient_compatible_with_chef_diet(food_category) if {
    has_role("vegetarian")
    food_category in ["VEGETABLE", "FRUIT", "DAIRY", "GRAIN", "LEGUME", "SPICE"]
}

is_ingredient_compatible_with_chef_diet(food_category) if {
    has_role("omnivorous")
    # Omnivorous chefs can use any ingredient
}

is_ingredient_compatible_with_chef_diet(food_category) if {
    has_role("pescatarian")
    food_category in ["VEGETABLE", "FRUIT", "DAIRY", "GRAIN", "LEGUME", "FISH", "SEAFOOD", "SPICE"]
}

is_ingredient_compatible_with_chef_diet(food_category) if {
    has_role("admin")
    # Admin can add any ingredient
}

# ========== GET /recipes/{recipeId} ==========
# Public recipes: accessible to all authenticated users
# Private recipes: accessible only to owner or admin

allow if {
    startswith(input.path, "/recipes/")
    not contains(input.path, "/ingredients/")
    not contains(input.path, "/compatibility")
    input.action.name == "GET"
    count(input.subject.authorities) > 0
    
    # Either recipe is public, or user is owner, or user is admin
    is_recipe_public
}

allow if {
    startswith(input.path, "/recipes/")
    not contains(input.path, "/ingredients/")
    not contains(input.path, "/compatibility")
    input.action.name == "GET"
    is_recipe_owner
}

allow if {
    startswith(input.path, "/recipes/")
    not contains(input.path, "/ingredients/")
    not contains(input.path, "/compatibility")
    input.action.name == "GET"
    has_role("admin")
}

# ========== GET /recipes (List recipes) ==========
# All authenticated users can list recipes
# Filtering happens at application level

allow if {
    input.path == "/recipes"
    input.action.name == "GET"
    count(input.subject.authorities) > 0
}

# ========== POST /recipes (Create recipe) ==========
# Only chef or admin can create recipes

allow if {
    input.path == "/recipes"
    input.action.name == "POST"
    has_any_role(["chef", "admin"])
}

# ========== PUT /recipes/{recipeId} (Update recipe) ==========
# Only owner or admin can update recipe

allow if {
    startswith(input.path, "/recipes/")
    not contains(input.path, "/ingredients/")
    input.action.name == "PUT"
    is_recipe_owner
}

allow if {
    startswith(input.path, "/recipes/")
    not contains(input.path, "/ingredients/")
    input.action.name == "PUT"
    has_role("admin")
}

# ========== DELETE /recipes/{recipeId} (Delete recipe) ==========
# Only owner or admin can delete recipe

allow if {
    startswith(input.path, "/recipes/")
    not contains(input.path, "/ingredients/")
    input.action.name == "DELETE"
    is_recipe_owner
}

allow if {
    startswith(input.path, "/recipes/")
    not contains(input.path, "/ingredients/")
    input.action.name == "DELETE"
    has_role("admin")
}

# ========== POST /recipes/{recipeId}/ingredients/{foodName} ==========
# Owner or admin can add ingredients
# Ingredient must be compatible with chef's dietary restrictions

allow if {
    contains(input.path, "/ingredients/")
    input.action.name == "POST"
    is_recipe_owner
    
    # Check if ingredient is compatible with chef's diet
    input.resource.foodCategory
    is_ingredient_compatible_with_chef_diet(input.resource.foodCategory)
}

allow if {
    contains(input.path, "/ingredients/")
    input.action.name == "POST"
    has_role("admin")
}

# ========== DELETE /recipes/{recipeId}/ingredients/{foodName} ==========
# Owner or admin can remove ingredients

allow if {
    contains(input.path, "/ingredients/")
    input.action.name == "DELETE"
    is_recipe_owner
}

allow if {
    contains(input.path, "/ingredients/")
    input.action.name == "DELETE"
    has_role("admin")
}

# ========== GET /recipes/{recipeId}/compatibility ==========
# User can check their own compatibility
# Nutritionist and admin can check any user's compatibility

allow if {
    contains(input.path, "/compatibility")
    input.action.name == "GET"
    input.resource.userId == input.subject.name
}

allow if {
    contains(input.path, "/compatibility")
    input.action.name == "GET"
    has_any_role(["nutritionist", "admin"])
}
