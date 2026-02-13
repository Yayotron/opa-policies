package user_profile_security

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

# Check if user is accessing their own profile
is_own_profile if {
    user_id := get_user_id_from_path
    user_id == input.subject.name
}

# ========== GET /users/{userId}/dietary-profile ==========
# User can view their own dietary profile
# Nutritionist and admin can view any user's dietary profile

allow if {
    contains(input.path, "/dietary-profile")
    input.action.name == "GET"
    is_own_profile
}

allow if {
    contains(input.path, "/dietary-profile")
    input.action.name == "GET"
    has_any_role(["nutritionist", "admin"])
}
