# Copilot Instructions for Simian Testing Project

## Project Overview
This project is a Java-based application using Spring and Hibernate for managing users, roles, and access permissions. The architecture is designed around a layered approach, separating concerns into DAO, service, and entity layers.

## Key Components
- **Entities**: `User`, `Role`, and `Access` represent the core data models.
- **DAOs**: `UserDAO`, `RoleDAO`, and `AccessDAO` handle database operations for their respective entities.
- **Services**: Business logic is encapsulated in service classes, such as `UserService` and `RoleService`.

## Data Flow
1. **User Creation**: A user is created through the `UserService`, which calls `UserDAO` to persist the `User` entity.
2. **Role Management**: Roles are managed via `RoleService`, which interacts with `RoleDAO` to perform CRUD operations.
3. **Access Control**: Access permissions are defined in the `Access` entity and linked to roles, allowing for flexible permission management.

## Developer Workflows
- **Building the Project**: Use `mvn clean install` to build the project and run tests.
- **Running Tests**: Execute `mvn test` to run all unit tests located in the `test` directory.
- **Debugging**: Use your IDE's debugging tools to set breakpoints in service or DAO classes to inspect the flow of data and application state.

## Project Conventions
- **Naming Conventions**: Classes are named in PascalCase (e.g., `UserService`), while methods use camelCase (e.g., `createUser`).
- **Transactional Management**: DAO methods are annotated with `@Transactional` to manage database transactions automatically.

## Integration Points
- **Database**: The application uses a MySQL database, with schema defined in `schema.sql`. Ensure the database is set up before running the application.
- **Spring Framework**: The project is configured to use Spring for dependency injection and transaction management.

## Examples of Usage
- To create a new user, call `userService.create(new User(...))` where `...` represents user details.
- To fetch a role by name, use `roleService.findByName("Admin")`.

## Additional Resources
- Refer to the `README.md` for setup instructions and additional context on the project.
- Review the `schema.sql` for understanding the database structure and relationships between entities.

---

This document serves as a guide for AI coding agents to understand the structure and workflows of the Simian Testing Project. Please provide feedback on any unclear or incomplete sections for further iteration.