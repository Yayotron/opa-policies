package food_security

import rego.v1

# By default, deny requests.
default allow := false

allow if {
    input.path == "/food/beef"
    input.action.name == "GET"
    some i
    input.subject.authorities[i] in ["ROLE_omnivorous"]
}

allow if {
    input.path == "/food/milk"
    input.action.name == "GET"
    some i
    input.subject.authorities[i] in ["ROLE_omnivorous", "ROLE_vegetarian"]
}

allow if {
    input.path == "/food/onion"
    input.action.name == "GET"
    some i
    some input.subject.authorities[i] in ["ROLE_omnivorous", "ROLE_vegetarian", "ROLE_vegan"]
}

allow if {
    input.path == "/food/egg"
    input.action.name == "GET"
    some i
    input.subject.authorities[i] in ["ROLE_omnivorous", "ROLE_vegetarian"]
}