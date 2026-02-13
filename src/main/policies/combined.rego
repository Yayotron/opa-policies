package combined

import rego.v1
import data.food_security
import data.recipe_security
import data.meal_plan_security
import data.user_profile_security
import data.management

# By default, deny requests
default allow := false

# ========== Policy Composition ==========
# A request is allowed if ANY of the imported policies allow it

allow if { food_security.allow }
allow if { recipe_security.allow }
allow if { meal_plan_security.allow }
allow if { user_profile_security.allow }
allow if { management.allow }

# ========== Audit Logging ==========
decision := {
    "allow": allow,
    "policies_evaluated": {
        "food_security": food_security.allow,
        "recipe_security": recipe_security.allow,
        "meal_plan_security": meal_plan_security.allow,
        "user_profile_security": user_profile_security.allow,
        "management": management.allow
    },
    "subject": input.subject.name,
    "path": input.path,
    "action": input.action.name
}
