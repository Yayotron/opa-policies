package combined

import data.food_security.allow as food_allow
import data.management.allow as management_allow

default allow = false

allow {
    food_allow
}

allow {
    management_allow
}