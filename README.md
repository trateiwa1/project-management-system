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
