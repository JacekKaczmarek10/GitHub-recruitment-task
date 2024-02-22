# GitHub Repository Listing API

This is a Kotlin Spring Boot application that retrieves a list of GitHub repositories for a given username and returns information about each repository, including its branches and last commit SHA. The application is built using Kotlin and Spring Boot 3.

## Endpoint Description

- **GET /{username}/get-repos**: Retrieves a list of GitHub repositories for the specified username. Returns information about each repository, including its name, owner's login, and branches with their corresponding last commit SHA.
## API Responses

- **Success Response (200 OK)**:
[
{
  "repositoryName": "example-repo",
  "ownerLogin": "example-owner",
  "branches": [
{
  "name": "master",
  "sha": "8eb120a5ef29"
},
{
  "name": "develop",
  "sha": "abc123def456"
}
]
},
{
"repositoryName": "another-repo",
"ownerLogin": "example-owner",
"branches": [
{
"name": "main",
"sha": "123abc456def"
}
]
}

- **Error Response (404 Not Found)**:
{
"status": 404,
"message": "User not found"
}

## Dependencies

- Spring Boot 3
- Kotlin
- RestTemplate

