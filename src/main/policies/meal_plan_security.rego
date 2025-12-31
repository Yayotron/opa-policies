package meal_plan_security

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

# Get user ID from path
get_user_id_from_path if {
    parts := split(input.path, "/")
    count(parts) >= 3
    result := parts[2]
} else := ""

# Check if user is accessing their own meal plan
is_own_meal_plan if {
    user_id := get_user_id_from_path
    user_id == input.subject.name
}

# Check if current time is within planning hours (6 AM - 10 PM)
is_within_planning_hours if {
    # Parse current time from input
    current_hour := input.context.currentHour
    current_hour >= 6
    current_hour < 22
}

# Check if meal plan is in the future
is_future_meal if {
    # This would compare input.resource.mealDate and input.resource.mealTime
    # with current date/time from input.context
    input.resource.isFuture == true
}

# Check if current time is before scheduled meal time
is_before_meal_time if {
    input.resource.isBeforeMealTime == true
}

# ========== GET /meal-plans/{userId} ==========
# User can view their own meal plans
# Nutritionist and admin can view any user's meal plans

allow if {
    startswith(input.path, "/meal-plans/")
    not contains(input.path, "/meal-plans/") or count(split(input.path, "/")) == 3
    input.action.name == "GET"
    is_own_meal_plan
}

allow if {
    startswith(input.path, "/meal-plans/")
    not contains(input.path, "/meal-plans/") or count(split(input.path, "/")) == 3
    input.action.name == "GET"
    has_any_role(["nutritionist", "admin"])
}

# ========== POST /meal-plans/{userId} ==========
# User can create their own meal plans during planning hours (6 AM - 10 PM)
# Nutritionist can create meal plans for clients anytime

allow if {
    startswith(input.path, "/meal-plans/")
    count(split(input.path, "/")) == 3
    input.action.name == "POST"
    is_own_meal_plan
    is_within_planning_hours
}

allow if {
    startswith(input.path, "/meal-plans/")
    count(split(input.path, "/")) == 3
    input.action.name == "POST"
    has_role("nutritionist")
}

allow if {
    startswith(input.path, "/meal-plans/")
    count(split(input.path, "/")) == 3
    input.action.name == "POST"
    has_role("admin")
}

# ========== PUT /meal-plans/{userId}/{mealPlanId} ==========
# User can update their own meal plans before meal time
# Nutritionist can update client meal plans anytime

allow if {
    startswith(input.path, "/meal-plans/")
    count(split(input.path, "/")) == 4
    input.action.name == "PUT"
    is_own_meal_plan
    is_before_meal_time
}

allow if {
    startswith(input.path, "/meal-plans/")
    count(split(input.path, "/")) == 4
    input.action.name == "PUT"
    has_role("nutritionist")
}

allow if {
    startswith(input.path, "/meal-plans/")
    count(split(input.path, "/")) == 4
    input.action.name == "PUT"
    has_role("admin")
}

# ========== DELETE /meal-plans/{userId}/{mealPlanId} ==========
# User can delete their own future meal plans
# Nutritionist can delete client meal plans if they're in the future

allow if {
    startswith(input.path, "/meal-plans/")
    count(split(input.path, "/")) == 4
    input.action.name == "DELETE"
    is_own_meal_plan
    is_future_meal
}

allow if {
    startswith(input.path, "/meal-plans/")
    count(split(input.path, "/")) == 4
    input.action.name == "DELETE"
    has_role("nutritionist")
    is_future_meal
}

allow if {
    startswith(input.path, "/meal-plans/")
    count(split(input.path, "/")) == 4
    input.action.name == "DELETE"
    has_role("admin")
}
