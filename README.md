# OPA Policies for Spring Boot Application

# Use case

1. I am learning about GenAI related topics.
2. I had a project interview where I was asked about OPA policies, which I never heard about before.
3. I wanted to learn about both topics, and I decided to create a project that would help me learn about both topics.

## Description

This project demonstrates how to integrate Open Policy Agent (OPA) with a Spring Boot application to enforce access
control policies. The application manages different types of users (e.g., omnivorous, vegetarian, vegan) and their
access to various food items.

Moreover, this project serves as a playground for java developers to use GitHub Copilot, or other AI-assisted tools, to
generate OPA policies and test cases off an english description.

I had no prior experience with OPA, I still don't understand clearly this new language, however I do understand Java and
the integration tests. It's a huge
productivity increase for me to be able to develop new policies using TDD following this approach.

Here's an example for an unimplemented feature:

1. Create a new endpoint/change authorization setup in an existing one.
2. Ask Copilot to generate test cases for what was change (e.g. help me generate test cases for GET /salmon/ endpoint
   that includes positive and negative tests)
3. Verify the generated code, and run the tests.
4. Ask again Copilot to update `policy.rego` with the new rules.
5. Update `policy.rego`, send update to OPA service.
6. Run the tests again.

## Project Structure

```
auth-server/
└── keycloak/
    └── realm-export.json
└── podman-compose.yml
src/
├── main/
│   ├── java/
│       └── ...
│   └── policies/
│       └── policy.rego
└── test/
    ├── java/
    └── policies/
         └── policy_test.rego
```

## Prerequisites

- Java 17
- Maven
- Podman (for running OPA and Keycloak)

## Setup

1. **Clone the repository:**

   ```sh
   git clone <repository-url>
   cd opa-policies
   ```

2. **Start OPA and Keycloak:**

   ```sh
   cd auth-server
   podman-compose up
   ```

(Optional): to add, change or remove users and/or roles you can edit
file [realm-export](auth-server/keycloak/realm-export.json),
then restart keycloak container.

```sh
   podman-compose restart keycloak
```

It's also possible to use a volume to have the settings in keycloak, and then maintain
the roles and users somewhere else as it would happen in a real environment.

3. Upload the policy to OPA:

   ```sh
   curl -X PUT --data-binary ./src/main/policies/combined.rego http://localhost:8181/v1/policies/combined
   ```

4. **Run the tests:**

   ```sh
   mvn test
   ```

## Usage

The application exposes several endpoints under `/food` that are protected by OPA policies. Here are some examples:

- `GET /food/onion` - Accessible by roles: vegan, vegetarian, omnivorous
- `GET /food/milk` - Accessible by roles: vegetarian, omnivorous
- `GET /food/beef` - Accessible by role: omnivorous

The application exposes two endpoints under `/management` that are protected by OPA policies.

- `GET /management/health` - Accessible by roles: operator
- `GET /management/info` - Accessible by roles: operator

## Testing

To run the tests, use the following command:

```sh
mvn test
```