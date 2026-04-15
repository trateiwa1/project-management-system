# Project Management System REST API

A Spring Boot REST API for managing projects, tasks, comments, and team collaboration. This backend service implements JWT authentication, role-based authorization, project membership management, and task lifecycle tracking.

The API is designed following clean architecture principles using DTOs, services, repositories, and controllers.

## Table of Contents
- Features

- Tech Stack
  
- Project Architecture
  
- Getting Started (Running locally or with Docker)
  
- API Documentation
  
- Authentication
  
- API Endpoints

- Testing

## Features

- JWT Authentication (Secure login & registration)
  
- Role-Based Access Control (USER, ADMIN)
  
- Project Management (Create, view, and delete projects)
- Project Membership System (Add users to projects with roles)
- Task Management (Create, assign, update, and delete tasks)
- Task Status Tracking (TO_DO → IN_PROGRESS → DONE)
- Comment System (Add and manage task discussions)
- Pagination Support for scalable data retrieval
- Global Exception Handling (Consistent API errors)
- Request Validation (Input validation)
- Swagger API Documentation
- **Complete Unit Testing** (Success & Failure cases for all services)

---

## Tech Stack

| Technology | Version |
|------------|---------|
| Java | 21 |
| Spring Boot | 3.4.4 |
| Spring Security | 3.4.4 |
| Spring Data JPA | 3.4.4 |
| H2 Database | In-memory |
| JWT | 0.11.5 |
| Maven | 3.11.0 |
| Docker | Latest
| Swagger/OpenAPI | 2.7.0 |
| Lombok | 1.18.32 |

---

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

```bash
src/main/java/com/example/pms/
├── config/        → Security & Swagger configuration
├── controller/    → REST API endpoints
├── dto/           → Request/Response objects
├── enums/         → Application enums
├── exception/     → Global exception handling
├── model/         → JPA entities
├── repository/    → Spring Data repositories
├── security/      → JWT authentication & filters
└── service/       → Business logic

src/test/java/com/example/pms/
└── service/       → Unit tests for service layer
```

---

## Getting Started
### Prerequisites
- Java 21+
- Maven
- Docker (optional)

## Option 1: Run Locally
```
git clone https://github.com/trateiwa1/project-management-system.git

cd project-management-system

mvn clean package

mvn spring-boot:run
```
Application runs at:
```
http://localhost:8080
```
## Option 2: Run with Docker
Build the application:
```
mvn clean package
```

Build the Docker image:
```
docker build -t project-management-system .
```

Run the container:
```
docker run -p 8080:8080 project-management-system
```
## API Documentation
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI Docs: http://localhost:8080/v3/api-docs
- H2 Console: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
  - Username: sa
  - Password: (leave empty)
 
---

## Authentication

All endpoints (except register/login) require a JWT token.

### Register
```
POST /auth/register
```
### Login
```
POST /auth/login
```
Response:
```
{
  "id": 1,
  "token": "JWT_TOKEN",
  "email": "user@email.com",
  "role": "USER"
}
```

Use token in Swagger **(Click the Authorize button in the top right corner of Swagger UI)**:
```
Authorization: Bearer YOUR_TOKEN
```
----

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
| GET | `/projects` | Get user projects |
| GET | `/projects/{id}` | Get project details |
| DELETE | `/projects/{id}` | Delete project |
| POST | `/projects/{projectId}/members/{userId}` | Add member to project |

### Tasks
| **Method** | **Endpoint** | **Description** |
|------------|--------------|-----------------|
| POST | `/projects/{projectId}/tasks` | Create task |
| GET | `/projects/{projectId}/tasks` | Get project tasks |
| PUT | `/tasks/{taskId}/assign/{userId}` | Assign task |
| PUT | `/tasks/status` | Update task status |
| DELETE | `/tasks/{taskId}` | Delete task |

### Comments
| **Method** | **Endpoint** | **Description** |
|------------|--------------|-----------------|
| POST | `/tasks/{taskId}/comments` | Add comment |
| GET | `/tasks/{taskId}/comments` | Get task comments |
| DELETE | `/comments/{commentId}` | Delete comment |

---

## Testing

The project includes **complete unit testing for the service classes using JUnit 5 and Mockito.**

### Test Coverage

Unit tests cover **both success and failure** scenarios for all service classes:

#### 1) ProjectService
- Project creation, retrieval, and membership management.

#### 2) TaskService
- Task creation, assignment, status updates, and deletion.

#### 3) CommentService
- Comment creation, retrieval, and deletion.

### Running Tests

Run all tests using Maven:

```
mvn clean install
```
or
```
mvn test
```
---

**Takundanashe Rateiwa**  
Computer Engineering Student | Vistula University  

GitHub: [@trateiwa1](https://github.com/trateiwa1)

## License

This project is open-source and available for educational and portfolio purposes.

