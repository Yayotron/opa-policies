package management

default allow = false

# Allow access to Actuator endpoints only for users with the "operator" role
allow {
    input.subject.authorities[_] == "ROLE_operator"
    input.path == "/management/health"
    input.action.name == "GET"
}

allow {
    input.subject.authorities[_] == "ROLE_operator"
    input.path == "/management/info"
    input.action.name == "GET"
}