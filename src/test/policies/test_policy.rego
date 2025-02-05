package food_security

test_allow_omnivorous_access_to_beef {
    allow with input as {
        "path": "/food/beef",
        "subject": {
            "authorities": ["ROLE_omnivorous"]
        }
    }
}

test_deny_non_omnivorous_access_to_beef {
    not allow with input as {
        "path": "/food/beef",
        "subject": {
            "authorities": ["ROLE_vegetarian"]
        }
    }
}

test_allow_omnivorous_and_vegetarian_access_to_milk {
    allow with input as {
        "path": "/food/milk",
        "subject": {
            "authorities": ["ROLE_omnivorous"]
        }
    }
    allow with input as {
        "path": "/food/milk",
        "subject": {
            "authorities": ["ROLE_vegetarian"]
        }
    }
}

test_deny_non_omnivorous_and_non_vegetarian_access_to_milk {
    not allow with input as {
        "path": "/food/milk",
        "subject": {
            "authorities": ["ROLE_vegan"]
        }
    }
}

test_allow_omnivorous_vegetarian_and_vegan_access_to_onion {
    allow with input as {
        "path": "/food/onion",
        "subject": {
            "authorities": ["ROLE_omnivorous"]
        }
    }
    allow with input as {
        "path": "/food/onion",
        "subject": {
            "authorities": ["ROLE_vegetarian"]
        }
    }
    allow with input as {
        "path": "/food/onion",
        "subject": {
            "authorities": ["ROLE_vegan"]
        }
    }
}

test_deny_non_omnivorous_vegetarian_and_vegan_access_to_onion {
    not allow with input as {
        "path": "/food/onion",
        "subject": {
            "authorities": ["ROLE_pescatarian"]
        }
    }
}

test_allow_omnivorous_and_vegetarian_access_to_egg {
    allow with input as {
        "path": "/food/egg",
        "subject": {
            "authorities": ["ROLE_omnivorous"]
        }
    }
    allow with input as {
        "path": "/food/egg",
        "subject": {
            "authorities": ["ROLE_vegetarian"]
        }
    }
}

test_deny_non_omnivorous_and_non_vegetarian_access_to_egg {
    not allow with input as {
        "path": "/food/egg",
        "subject": {
            "authorities": ["ROLE_vegan"]
        }
    }
}