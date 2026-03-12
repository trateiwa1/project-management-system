# Project Management System REST API

A Spring Boot REST API for managing projects, tasks, comments, and team collaboration. This backend service implements JWT authentication, role-based authorization, project membership management, and task lifecycle tracking.

The API is designed following clean architecture principles using DTOs, services, repositories, and controllers.

## Features

- JWT Authentication (Register & Login)

- Role-based authorization (Admin / User)

- Project creation and management

- Project membership system

- Task creation and assignment

- Task status management (To Do → In Progress → Done)

- Comment system for tasks

- Pagination support

- Global exception handling

- Request validation

- Swagger API documentation
  

## Tech Stack

### Backend

- Java 17

- Spring Boot

- Spring Security

- Spring Data JPA

- Hibernate

- JWT Authentication

### Database

MySQL / PostgreSQL (configurable)

### Documentation

Swagger / OpenAPI

### Build Tool

Maven


## Project Architecture

The project follows a layered architecture:

```
Controller Layer
       ↓
 Service Layer
       ↓
Repository Layer
       ↓
    Database
```

### Structure

```
src/main/java/com/example/pms

config/        → Security & Swagger configuration
controller/    → REST API endpoints
dto/           → Request/Response objects
enums/         → Application enums
exception/     → Global exception handling
model/         → JPA entities
repository/    → Spring Data repositories
security/      → JWT authentication & filters
service/       → Business logic
```

## Authentication

The API uses JWT Bearer Tokens.

### Register
POST /auth/register

### Login
POST /auth/login

Returns:
```
{
  "id": 1,
  "token": "JWT_TOKEN",
  "email": "user@email.com",
  "role": "USER"
}
```

All protected endpoints require:

Authorization: Bearer `<token>`


## API Endpoints

### Authentication
| **Method** | **Endpoint** | **Description** |
|------------|--------------|-----------------|
| POST | `/auth/register` | Register new user |
| POST | `/auth/login` | Login user |

### Projects
| **Method** | **Endpoint** | **Description** |
|------------|--------------|-----------------|
| POST | `/projects` | Create project |
| GET | `/projects` | Get user's projects |
| GET | `/projects/{id}` | Get project details |
| DELETE | `/projects/{id}` | Delete project |
| POST | `/projects/{projectId}/members/{userId}` | Add member to project |

### Tasks
| **Method** | **Endpoint** | **Description** |
|------------|--------------|-----------------|
| POST | `/projects/{projectId}/tasks` | Create task |
| GET | `/projects/{projectId}/tasks` | Get project tasks |
| PUT | `/tasks/{taskId}/assign/{userId}` | Assign task |
| PUT | `/tasks/{taskId}/status` | Update task status |
| DELETE | `/tasks/{taskId}` | Delete task |

### Comments
| **Method** | **Endpoint** | **Description** |
|------------|--------------|-----------------|
| POST | `/tasks/{taskId}/comments` | Add comment |
| GET | `/tasks/{taskId}/comments` | Get task comments |
| DELETE | `/comments/{commentId}` | Delete comment |


## Running the Project
### 1. Clone the repository
git clone https://github.com/trateiwa1/project-management-system.git

### 2. Navigate to project
cd project-management-system-api

### 3. Configure database

Update application.properties:

```
spring.datasource.url=jdbc:mysql://localhost:3306/pms
spring.datasource.username=your_username
spring.datasource.password=your_password
```
### 4. Run application
```
mvn spring-boot:run
```
Application runs on:
```
http://localhost:8080
```
## API Documentation

Swagger UI:
```
http://localhost:8080/swagger-ui.html
```
OpenAPI Docs:
```
http://localhost:8080/v3/api-docs
```

## Security Features

- JWT Authentication
- BCrypt password hashing
- Stateless sessions
- Role-based authorization
- Project membership validation
- Ownership checks for critical actions

## Error Handling

Global exception handling returns consistent error responses:

Example:
```
{
  "error": "VALIDATION_FAILED",
  "message": "Validation failed",
  "timestamp": "2025-01-01T10:00:00"
}
```

## Docker (Planned)

## ⚠️ This section will be updated once Docker support is added

Future Docker setup will include:

- Dockerfile for Spring Boot application
- Docker Compose configuration
- Database container
- Environment variable configuration

TODO:

- [ ] Create Dockerfile
- [ ] Create docker-compose.yml
- [ ] Configure database container
- [ ] Add environment variables

### Author

Takundanashe Rateiwa

Computer Engineering Student
Backend Development | Java | Spring Boot | REST APIs

GitHub:

https://github.com/YOUR_USERNAME
License

This project is open-source and available for educational and portfolio purposes.

