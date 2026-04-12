# Project Management System REST API

A Spring Boot REST API for managing projects, tasks, comments, and team collaboration. This backend service implements JWT authentication, role-based authorization, project membership management, and task lifecycle tracking.

The API is designed following clean architecture principles using DTOs, services, repositories, and controllers.

## Table of Contents
- Features

- Tech Stack
  
- Project Architecture
  
- Getting Started
  
- API Documentation
  
- Authentication
  
- API Endpoints

- Testing
  
- Docker
  
- Project Structure

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
```
git clone https://github.com/trateiwa1/project-management-system.git
```
### 2. Navigate to project
```
cd project-management-system
```
### 3. Database

The project uses an **H2 in-memory database** for setup.

No external database installation is required.

The database is automatically created when the application starts.

You can access the H2 console at:

```
http://localhost:8080/h2-console
```

Use the following settings:

```
JDBC URL: jdbc:h2:mem:testdb
User Name: sa
Password: (leave empty)
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
## How to Try the API

You can quickly test the API using Swagger UI by following these steps:

**1. Open Swagger UI**

Navigate to:  
```
http://localhost:8080/swagger-ui.html
```
**2. Register a New User**  
- Go to **POST /auth/register**  
- Fill in your **email**, **password**, and set **role** to `USER`  
- Click **Execute**  

**3. Login to Get a JWT Token**  
- Go to **POST /auth/login**  
- Enter the same email and password you used to register  
- Click **Execute**  
- Copy the `token` value from the response  

**4. Authorize Swagger UI**  
- Click **Authorize** (top-right corner of Swagger UI)  
- Paste your token like this:  
  ```
  Bearer YOUR_JWT_TOKEN
  ```  
- Click **Authorize**, then **Close**  

**5. Test Protected Endpoints**  
- Now you can create projects, tasks, add comments, and perform other actions directly from Swagger UI  
- All requests will automatically include your JWT token for authorization  

> You must complete the registration and login steps first to access endpoints that require authentication.

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

## Docker

The application can be run using Docker.

### 1. Build the project

```
mvn clean package
```

This will generate the JAR file:

```
target/project-management-system-0.0.1-SNAPSHOT.jar
```

### 2. Build Docker image

```
docker build -t pms-api .
```

### 3. Run container

```
docker run -p 8080:8080 pms-api
```

The API will be available at:

```
http://localhost:8080
```

Swagger documentation:

```
http://localhost:8080/swagger-ui.html
```

### Notes

- The application uses an **H2 in-memory database**
- No external database is required
- Data resets when the container stops
## Author

**Takundanashe Rateiwa**  
Computer Engineering Student | Vistula University  

GitHub: [@trateiwa1](https://github.com/trateiwa1)

## License

This project is open-source and available for educational and portfolio purposes.

