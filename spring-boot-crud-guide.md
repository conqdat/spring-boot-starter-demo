# Spring Boot CRUD API - Quick Reference

## Project Structure

```
src/main/java/com/learing/spring_boot_starter_demo/
├── Application.java
├── controller/TodoController.java
├── model/Todo.java
├── repository/TodoRepository.java
├── service/TodoService.java
├── dto/
│   ├── TodoRequest.java
│   └── TodoResponse.java
└── exception/
    ├── ResourceNotFoundException.java
    └── GlobalExceptionHandler.java
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/todos` | Get all todos |
| GET | `/api/todos/{id}` | Get todo by ID |
| GET | `/api/todos/completed/{completed}` | Filter by completed status |
| POST | `/api/todos` | Create new todo |
| PUT | `/api/todos/{id}` | Update todo |
| DELETE | `/api/todos/{id}` | Delete todo |

## Quick Start

```bash
# Start PostgreSQL (Docker)
docker run --name postgres-todo -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=todo_db -p 5432:5432 -d postgres:15

# Run application
./gradlew bootRun

# Test endpoints
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title": "Learn Spring", "description": "Build CRUD API", "completed": false}'

curl http://localhost:8080/api/todos
```

## Validation Rules

- `title`: Required, 3-100 characters
- `description`: Max 500 characters
- `completed`: Boolean (defaults to false)

## Error Responses

All errors return consistent JSON format:

```json
{
    "timestamp": "2026-03-22T23:01:30.776",
    "status": 400,
    "error": "Bad Request",
    "message": "Error details",
    "path": "/api/todos"
}
```

| Status | Cause |
|--------|-------|
| 400 | Invalid JSON or validation failed |
| 404 | Resource not found |
| 500 | Server error |

## Gradle Commands

```bash
./gradlew bootRun    # Run application
./gradlew build      # Build project
./gradlew test       # Run tests
./gradlew clean      # Clean build
```
