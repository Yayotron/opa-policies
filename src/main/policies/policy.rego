package food_security

import rego.v1

# By default, deny requests.
default allow := false

allow if {
    input.path == "/food/beef"
    some i
    input.subject.authorities[i] in ["ROLE_omnivorous"]
}

allow if {
    input.path == "/food/milk"
    some i
    input.subject.authorities[i] in ["ROLE_omnivorous", "ROLE_vegetarian"]
}

allow if {
    input.path == "/food/onion"
    some i
    some input.subject.authorities[i] in ["ROLE_omnivorous", "ROLE_vegetarian", "ROLE_vegan"]
}

allow if {
    input.path == "/food/egg"
    some i
    input.subject.authorities[i] in ["ROLE_omnivorous", "ROLE_vegetarian"]
}