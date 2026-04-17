# Project Management System REST API

A backend REST API for managing projects, tasks, comments, and team collaboration with JWT-based authentication and PostgreSQL persistence.

## Table of Contents
- Features

- Tech Stack
  
- Project Architecture

- Database
  
- Getting Started (Running locally or with Docker)
  
- API Documentation
  
- Authentication
  
- API Endpoints

- Testing

## Features

- JWT Authentication (Secure login & registration)
  
- Role-Based Access Control (USER, ADMIN)
  
- Project Management (Create, view, and delete projects)
- Project Membership System (Add users to projects)
- Task Management (Create, assign, update, and delete tasks)
- Task Status Tracking 
- Comment System (Task discussions)
- Pagination Support
- Global Exception Handling 
- Request Validation (Input validation)
- Swagger API Documentation
- Docker support for PostgreSQL database
- Unit Tests for service classes 

---

## Tech Stack

| Technology        | Version     | Description                          |
|------------------|------------|--------------------------------------|
| Java             | 21         | Core programming language            |
| Spring Boot      | 3.4.4      | Backend framework                    |
| Spring Security  | 3.4.4      | Authentication & Authorization       |
| Spring Data JPA  | 3.4.4      | ORM and database interaction         |
| PostgreSQL       | 16         | Production-grade relational database |
| Docker           | Latest     | Containerized database deployment    |
| JWT              | 0.11.5     | Secure authentication                |
| Maven            | 3.11.0     | Build tool                           |
| Swagger/OpenAPI  | 2.7.0      | API documentation                    |
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

## Database

The primary database used in this project is **PostgreSQL 16**.

- Runs in a Docker container
- Persistent storage 
- Automatically managed schema using Hibernate (JPA)
- Supports production-like environment setup
  
---

## Getting Started

### Requirements
- Java 21+
- Maven
- Docker

**Clone the repository and change the directory**
```
git clone https://github.com/trateiwa1/project-management-system.git  

cd project-management-system
```

**Option A: Run the application with Docker**

Start PostgreSQL (Docker)
```
docker run --name postgres-db \
-e POSTGRES_PASSWORD="Password&123" \
-e POSTGRES_DB=project_management_db \
-p 5432:5432 \
-d postgres:16
```
**Option B: Run the application locally**
```
mvn clean install
 
mvn spring-boot:run
```
Application URL:
```
http://localhost:8080
```
Note: This is a backend REST API - Use Swagger UI to access and test endpoints when the application is running: 
```
http://localhost:8080/swagger-ui/index.html
```

## API Documentation
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI Docs: http://localhost:8080/v3/api-docs
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

Use the generated token in Swagger **(Click the Authorize button in the top right corner of Swagger UI)**:
```
Authorization: Bearer YOUR_TOKEN
```
----

## API Endpoints

### Authentication
| **Method** | **Endpoint** | **Description** |
|------------|--------------|-----------------|
| POST | `/auth/register` | Register user |
| POST | `/auth/login` | Login user |

### Projects
| **Method** | **Endpoint** | **Description** |
|------------|--------------|-----------------|
| POST | `/projects` | Create project |
| GET | `/projects` | Get user projects |
| GET | `/projects/{id}` | Get project |
| DELETE | `/projects/{id}` | Delete project |
| POST | `/projects/{projectId}/members/{userId}` | Add member |

### Tasks
| **Method** | **Endpoint** | **Description** |
|------------|--------------|-----------------|
| POST | `/projects/{projectId}/tasks` | Create task |
| GET | `/projects/{projectId}/tasks` | Get tasks |
| PUT | `/tasks/{taskId}/assign/{userId}` | Assign task |
| PUT | `/tasks/status` | Update status |
| DELETE | `/tasks/{taskId}` | Delete task |

### Comments
| **Method** | **Endpoint** | **Description** |
|------------|--------------|-----------------|
| POST | `/tasks/{taskId}/comments` | Add comment |
| GET | `/tasks/{taskId}/comments` | Get comments |
| DELETE | `/comments/{commentId}` | Delete comment |

---

## Testing

This project includes detailed unit testing for the service layer, verifying that core logic performs correctly across different use cases.

### Testing Tools
- **JUnit 5** – used for structuring and running test cases
- **Mockito** – used to mock dependencies and isolate service logic

### Test Coverage
All major service classes are thoroughly tested, covering both successful operations and error/edge cases to ensure reliability and robustness.

1) **ProjectServiceTest** – validates project creation, retrieval, and member management logic.
2) **TaskServiceTest** – verifies task handling, including assignment, updates, and workflow rules.
3) **CommentServiceTest** – ensures correct behavior for adding, retrieving, and managing comments.

### Running Tests

Execute all tests using Maven:

```
mvn clean install
```
or
```
mvn test
```
---

## Author

**Takundanashe Rateiwa**  
Computer Engineering Student | Vistula University  

GitHub: [@trateiwa1](https://github.com/trateiwa1)

## License

This project is open-source and available for educational and portfolio purposes.

