package food_security

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

# Get food category from path
get_food_name if {
    parts := split(input.path, "/")
    count(parts) >= 3
    result := parts[2]
} else := ""

# Check if food is compatible with dietary restriction
is_food_compatible_with_diet(food_name, diet) if {
    diet == "vegan"
    food_name in ["onion", "carrot", "apple", "bread"]
}

is_food_compatible_with_diet(food_name, diet) if {
    diet == "vegetarian"
    food_name in ["onion", "carrot", "apple", "milk", "cheese", "egg", "bread"]
}

is_food_compatible_with_diet(food_name, diet) if {
    diet == "omnivorous"
    # Omnivorous can access all foods
}

is_food_compatible_with_diet(food_name, diet) if {
    diet == "pescatarian"
    food_name in ["onion", "carrot", "apple", "milk", "cheese", "bread", "fish", "salmon"]
}

is_food_compatible_with_diet(food_name, diet) if {
    diet == "fruitarian"
    food_name in ["apple"]
}

# Check if user has dietary restriction
user_has_diet(diet) if {
    has_role(diet)
}

# ========== GET /food/{foodName} ==========
# Users can only access foods compatible with their dietary restrictions

allow if {
    input.path != "/food"
    startswith(input.path, "/food/")
    not contains(input.path, "/allergens")
    not contains(input.path, "/rate")
    not contains(input.path, "/flag")
    not startswith(input.path, "/food/safe-for/")
    input.action.name == "GET"
    food_name := get_food_name
    
    # Check if user has compatible diet
    some diet in ["vegan", "vegetarian", "omnivorous", "pescatarian", "fruitarian"]
    user_has_diet(diet)
    is_food_compatible_with_diet(food_name, diet)
}

# ========== GET /food (List foods with filters) ==========
# All authenticated users can list foods
# Data filtering happens at application level based on user's diet

allow if {
    input.path == "/food"
    input.action.name == "GET"
    # User must be authenticated (has at least one role)
    count(input.subject.authorities) > 0
}

# ========== POST /food/{foodName}/rate ==========
# Any authenticated user can rate food

allow if {
    contains(input.path, "/rate")
    input.action.name == "POST"
    count(input.subject.authorities) > 0
}

# ========== DELETE /food/{foodName} ==========
# Only admin can delete food items

allow if {
    startswith(input.path, "/food/")
    not contains(input.path, "/allergens")
    not contains(input.path, "/rate")
    not contains(input.path, "/flag")
    input.action.name == "DELETE"
    has_role("admin")
}

# ========== GET /food/{foodName}/allergens ==========
# Any authenticated user can view allergen information

allow if {
    contains(input.path, "/allergens")
    input.action.name == "GET"
    count(input.subject.authorities) > 0
}

# ========== GET /food/safe-for/{allergyType} ==========
# Any authenticated user can query safe foods

allow if {
    startswith(input.path, "/food/safe-for/")
    input.action.name == "GET"
    count(input.subject.authorities) > 0
}

# ========== POST /food/{foodName}/flag ==========
# Only nutritionist or admin can flag foods

allow if {
    contains(input.path, "/flag")
    input.action.name == "POST"
    has_any_role(["nutritionist", "admin"])
}
