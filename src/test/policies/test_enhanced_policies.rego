package food_enhanced_security

import rego.v1

# ========== Test GET /food/{foodName} ==========

test_vegan_can_access_vegetables if {
    allow with input as {
        "path": "/food/carrot",
        "action": {"name": "GET"},
        "subject": {
            "name": "vegan_user1",
            "authorities": ["ROLE_vegan"]
        }
    }
}

test_vegan_cannot_access_meat if {
    not allow with input as {
        "path": "/food/beef",
        "action": {"name": "GET"},
        "subject": {
            "name": "vegan_user1",
            "authorities": ["ROLE_vegan"]
        }
    }
}

test_omnivorous_can_access_all_foods if {
    allow with input as {
        "path": "/food/beef",
        "action": {"name": "GET"},
        "subject": {
            "name": "omni_user1",
            "authorities": ["ROLE_omnivorous"]
        }
    }
}

test_pescatarian_can_access_fish if {
    allow with input as {
        "path": "/food/salmon",
        "action": {"name": "GET"},
        "subject": {
            "name": "pesc_user1",
            "authorities": ["ROLE_pescatarian"]
        }
    }
}

test_pescatarian_cannot_access_meat if {
    not allow with input as {
        "path": "/food/beef",
        "action": {"name": "GET"},
        "subject": {
            "name": "pesc_user1",
            "authorities": ["ROLE_pescatarian"]
        }
    }
}

# ========== Test POST /food/{foodName}/rate ==========

test_authenticated_user_can_rate_food if {
    allow with input as {
        "path": "/food/carrot/rate",
        "action": {"name": "POST"},
        "subject": {
            "name": "user1",
            "authorities": ["ROLE_vegan"]
        }
    }
}

test_unauthenticated_user_cannot_rate_food if {
    not allow with input as {
        "path": "/food/carrot/rate",
        "action": {"name": "POST"},
        "subject": {
            "name": "anonymous",
            "authorities": []
        }
    }
}

# ========== Test DELETE /food/{foodName} ==========

test_admin_can_delete_food if {
    allow with input as {
        "path": "/food/carrot",
        "action": {"name": "DELETE"},
        "subject": {
            "name": "admin1",
            "authorities": ["ROLE_admin"]
        }
    }
}

test_regular_user_cannot_delete_food if {
    not allow with input as {
        "path": "/food/carrot",
        "action": {"name": "DELETE"},
        "subject": {
            "name": "user1",
            "authorities": ["ROLE_vegan"]
        }
    }
}

# ========== Test GET /food/{foodName}/allergens ==========

test_authenticated_user_can_view_allergens if {
    allow with input as {
        "path": "/food/milk/allergens",
        "action": {"name": "GET"},
        "subject": {
            "name": "user1",
            "authorities": ["ROLE_vegan"]
        }
    }
}

# ========== Test POST /food/{foodName}/flag ==========

test_nutritionist_can_flag_food if {
    allow with input as {
        "path": "/food/beef/flag",
        "action": {"name": "POST"},
        "subject": {
            "name": "nutritionist1",
            "authorities": ["ROLE_nutritionist"]
        }
    }
}

test_admin_can_flag_food if {
    allow with input as {
        "path": "/food/beef/flag",
        "action": {"name": "POST"},
        "subject": {
            "name": "admin1",
            "authorities": ["ROLE_admin"]
        }
    }
}

test_regular_user_cannot_flag_food if {
    not allow with input as {
        "path": "/food/beef/flag",
        "action": {"name": "POST"},
        "subject": {
            "name": "user1",
            "authorities": ["ROLE_vegan"]
        }
    }
}
