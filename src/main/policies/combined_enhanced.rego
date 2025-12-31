package combined_enhanced

import rego.v1
import data.food_security
import data.food_enhanced_security
import data.recipe_security
import data.meal_plan_security
import data.user_profile_security

# By default, deny requests
default allow := false

# ========== Policy Composition ==========
# This policy combines all security modules
# A request is allowed if ANY of the imported policies allow it

# Check original food security policy (for backward compatibility)
allow if {
    food_security.allow
}

# Check enhanced food security policy
allow if {
    food_enhanced_security.allow
}

# Check recipe security policy
allow if {
    recipe_security.allow
}

# Check meal plan security policy
allow if {
    meal_plan_security.allow
}

# Check user profile security policy
allow if {
    user_profile_security.allow
}

# ========== Audit Logging ==========
# Provide detailed reason for decision (useful for debugging)

decision := {
    "allow": allow,
    "policies_evaluated": {
        "food_security": food_security.allow,
        "food_enhanced_security": food_enhanced_security.allow,
        "recipe_security": recipe_security.allow,
        "meal_plan_security": meal_plan_security.allow,
        "user_profile_security": user_profile_security.allow
    },
    "subject": input.subject.name,
    "path": input.path,
    "action": input.action.name
}
