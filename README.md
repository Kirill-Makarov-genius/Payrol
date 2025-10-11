# Payrol (Spring Boot REST API with HATEOAS)

A Spring Boot 3 application exposing a hypermedia-driven REST API for managing Employees and Orders. It uses Spring Web, Spring Data JPA, and Spring HATEOAS. Persistence is configured for PostgreSQL by default, with H2 available as an alternative runtime dependency.

Note: The project name is intentionally spelled "Payrol" to match the codebase and artifactId.


## Tech stack
- Java 17
- Spring Boot 3.5.6
- Spring Web (REST)
- Spring Data JPA (Hibernate)
- Spring HATEOAS
- PostgreSQL driver (runtime)
- H2 database (runtime, optional)
- Maven Wrapper


## Modules overview
- Employee domain: Employee entity + CRUD endpoints
- Order domain: Order entity + workflow endpoints (create, cancel, complete) with Problem Details error responses when actions are invalid
- HATEOAS link assemblers for both domains


## Project structure (key parts)
- src/main/java/com/example/Payrol
  - advices: Exception-to-HTTP mapping (404)
  - components: HATEOAS assemblers
  - config: Optional data loader (commented)
  - controllers: REST controllers for Employees and Orders
  - entities: JPA entities (Employee, Order)
  - enums: Status enum for orders
  - exceptions: Domain exceptions
  - repositories: Spring Data JPA repositories
  - PayrolApplication.java: Spring Boot entrypoint
- src/main/resources/application.properties: App configuration


## Prerequisites
- Java 17 installed and on PATH (java -version)
- Maven Wrapper is included; no standalone Maven required
- PostgreSQL server (if using Postgres; see H2 quick start below for an alternative)


## Configuration
Default datasource configuration (src/main/resources/application.properties):

```
spring.application.name=Payrol
spring.datasource.url= jdbc:postgresql://localhost:5432/postgres
spring.datasource.username= postgres
spring.datasource.password= 123

spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation= true
spring.jpa.properties.hibernate.dialect= org.hibernate.dialect.PostgreSQLDialect

# Hibernate ddl auto (create, create-drop, validate, update)
spring.jpa.hibernate.ddl-auto= update
```

You can override these with environment variables or JVM system properties following Spring Boot’s relaxed binding, e.g.:

- Environment variables
  - SPRING_DATASOURCE_URL
  - SPRING_DATASOURCE_USERNAME
  - SPRING_DATASOURCE_PASSWORD
  - SPRING_JPA_HIBERNATE_DDL_AUTO

- JVM properties
  - -Dspring.datasource.url=...
  - -Dspring.datasource.username=...
  - -Dspring.datasource.password=...


### Quick start using H2 (in-memory)
If you prefer to run without PostgreSQL, you can switch to H2 quickly. One option is to temporarily comment out the Postgres properties and add H2 settings such as:

```
# Example H2 config (replace the Postgres config during local dev)
spring.datasource.url=jdbc:h2:mem:payrol;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
# Optional H2 console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

Note: The H2 dependency is already present with scope runtime.


## Build and run
Using the Maven Wrapper from the project root:

- Run the app (dev)
  - Windows: `mvnw.cmd spring-boot:run`
  - Linux/macOS: `./mvnw spring-boot:run`

- Run tests
  - Windows: `mvnw.cmd test`
  - Linux/macOS: `./mvnw test`

- Package as an executable jar
  - Windows: `mvnw.cmd -DskipTests package`
  - Linux/macOS: `./mvnw -DskipTests package`
  - Run: `java -jar target/Payrol-0.0.1-SNAPSHOT.jar`

Default server port is 8080.


## Domain model

### Employee
- id: Long (generated)
- firstName: String
- lastName: String
- role: String
- Derived property: name ("firstName lastName")

### Order
- id: Long (generated)
- description: String
- status: Enum (IN_PROGRESS, COMPLETED, CANCELLED)
- Table name: CUSTOMER_ORDER


## API reference
All endpoints return JSON. HATEOAS responses include `_links`.

### Employees

- GET /employees
  - Returns a collection of employees with links.

- GET /employees/{id}
  - Returns a single employee with links.

- POST /employees
  - Body example:
    ```json
    {
      "firstName": "Jane",
      "lastName": "Doe",
      "role": "Engineer"
    }
    ```
  - Alternatively, you may supply `name` in place of `firstName/lastName`:
    ```json
    {
      "name": "Jane Doe",
      "role": "Engineer"
    }
    ```
  - Response: 201 Created with Location header and HATEOAS links.

- PUT /employees/{id}
  - Updates an existing employee if found; otherwise creates a new one from the request body.
  - Body format same as POST.
  - Response: 201 Created with updated state and links.

- DELETE /employees/{id}
  - Response: 204 No Content

Example response (GET /employees/1):
```json
{
  "id": 1,
  "firstName": "Jane",
  "lastName": "Doe",
  "name": "Jane Doe",
  "role": "Engineer",
  "_links": {
    "self": { "href": "http://localhost:8080/employees/1" },
    "employees": { "href": "http://localhost:8080/employees" }
  }
}
```

cURL examples:
- Create: `curl -X POST http://localhost:8080/employees -H "Content-Type: application/json" -d '{"firstName":"Jane","lastName":"Doe","role":"Engineer"}'`
- List: `curl http://localhost:8080/employees`
- Get by id: `curl http://localhost:8080/employees/1`
- Update: `curl -X PUT http://localhost:8080/employees/1 -H "Content-Type: application/json" -d '{"name":"Jane Smith","role":"Lead Engineer"}'`
- Delete: `curl -X DELETE http://localhost:8080/employees/1`


### Orders

- GET /orders
  - Returns a collection of orders with links.

- GET /orders/{id}
  - Returns a single order with links.

- POST /orders
  - Creates a new order with status automatically set to `IN_PROGRESS`.
  - Body example:
    ```json
    { "description": "Coffee" }
    ```
  - Response: 201 Created with links including available state transitions.

- DELETE /orders/{id}/cancel
  - Cancels an order that is in `IN_PROGRESS`.
  - If the order is not in `IN_PROGRESS`, returns 405 with a Problem Details body.

- PUT /orders/{id}/complete
  - Completes an order that is in `IN_PROGRESS`.
  - If the order is not in `IN_PROGRESS`, returns 405 with a Problem Details body.

Example response (POST /orders):
```json
{
  "id": 1,
  "description": "Coffee",
  "status": "IN_PROGRESS",
  "_links": {
    "self": { "href": "http://localhost:8080/orders/1" },
    "orders": { "href": "http://localhost:8080/orders" },
    "Cansel": { "href": "http://localhost:8080/orders/1/cancel" },
    "Complete": { "href": "http://localhost:8080/orders/1/complete" }
  }
}
```

Example error (trying to cancel a non IN_PROGRESS order):
```json
{
  "title": "Method not allowed",
  "detail": "You can't cancel an order that is in the COMPLETED status!"
}
```
Response headers include `Content-Type: application/problem+json`.

cURL examples:
- Create: `curl -X POST http://localhost:8080/orders -H "Content-Type: application/json" -d '{"description":"Coffee"}'`
- List: `curl http://localhost:8080/orders`
- Get by id: `curl http://localhost:8080/orders/1`
- Cancel: `curl -X DELETE http://localhost:8080/orders/1/cancel`
- Complete: `curl -X PUT http://localhost:8080/orders/1/complete`


## Error handling
- Not found (Employees/Orders): HTTP 404 with plain text message.
- Invalid order operation (cancel/complete when not allowed): HTTP 405 with Problem Details (RFC 7807) JSON body and `application/problem+json` content type.


## Data initialization
A `LoadDatabase` class is present but commented out. If you want to preload demo data, you can uncomment and customize it to insert sample Employees and Orders on startup.


## Notes and known limitations
- The link relation names for order actions in the HATEOAS response are capitalized and include a typo ("Cansel").
- `PUT /orders/{id}/complete` updates the status in-memory and returns the updated representation, but does not persist the change to the database in the current implementation.
- `PUT /employees/{id}` will create a new employee when the id doesn’t exist; the new entity’s id will be generated and not set to the requested `{id}`.
- PostgreSQL credentials in `application.properties` are placeholders for local development; adjust them for your environment or switch to H2 for quick testing.


## Running in production
- Configure a managed Postgres instance and secure credentials.
- Set `spring.jpa.hibernate.ddl-auto=validate` (or manage schema with migrations such as Flyway or Liquibase).
- Build the jar with the Maven wrapper and run it under a supported Java 17 runtime.



