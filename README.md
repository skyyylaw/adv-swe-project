# OnTrackED - Educational Progress Tracking Service

OnTrackED is a Spring Boot-based REST API service designed for tracking educational progress through goals, check-ins, and user management. The service provides endpoints for managing users, goals, and progress check-ins with persistent storage using CSV files.

## Table of Contents
- [API Documentation](#api-documentation)
- [Build and Run Instructions](#build-and-run-instructions)
- [Testing](#testing)
- [Configuration](#configuration)
- [Third-Party Dependencies](#third-party-dependencies)
- [Project Structure](#project-structure)
- [Project Management](#project-management)

## API Documentation

### User Management Endpoints

#### GET `/users`
**Description**: Retrieves all users from the system.

**Input**: None

**Output**: 
- **Success (200 OK)**: Array of User objects
- **Error (500 Internal Server Error)**: Error message if loading fails

**User Object Structure**:
```json
{
  "userId": 1,
  "fullName": "John Doe",
  "email": "john@example.com",
  "role": "STUDENT",
  "userCreatedAt": "2024-01-01",
  "userUpdatedAt": "2024-01-01"
}
```

#### GET `/users/{id}`
**Description**: Retrieves a specific user by their unique identifier.

**Input**: 
- `id` (path parameter): Integer - unique user ID

**Output**:
- **Success (200 OK)**: User object
- **Error (404 Not Found)**: If user doesn't exist

#### POST `/createUser`
**Description**: Creates a new user with the provided information.

**Input**: CreateUserRequest object
```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "role": "STUDENT"
}
```

**Output**:
- **Success (201 Created)**: Created User object
- **Error (400 Bad Request)**: If email already exists or role is invalid

**Valid Roles**: `STUDENT`, `TEACHER`, `COUNSELOR`

#### PUT `/updateUser/{id}`
**Description**: Updates an existing user's information.

**Input**: 
- `id` (path parameter): Integer - user ID to update
- CreateUserRequest object (same structure as POST)

**Output**:
- **Success (200 OK)**: Updated User object
- **Error (400 Bad Request)**: If user not found, email already exists, or role is invalid
- **Error (404 Not Found)**: If user doesn't exist

### Goal Management Endpoints

#### GET `/goal/` or `/goal/index`
**Description**: Returns a simple welcome message for the goal controller.

**Input**: None

**Output**: 
- **Success (200 OK)**: String "Goal Controller"

#### GET `/goal/retrieveOneGoal`
**Description**: Retrieves a specific goal by its ID.

**Input**: 
- `id` (query parameter): String - goal UUID

**Output**:
- **Success (200 OK)**: Goal object
- **Error (404 Not Found)**: If goal doesn't exist

**Goal Object Structure**:
```json
{
  "id": "uuid-string",
  "ownerId": "user-id",
  "parentId": "parent-goal-id",
  "childrenId": ["child-goal-1", "child-goal-2"],
  "title": "Goal Title",
  "description": "Goal Description",
  "dueDate": "2024-12-31",
  "status": "ACTIVE",
  "latestPercentage": 75,
  "createdAt": "2024-01-01T00:00:00Z",
  "updatedAt": "2024-01-01T00:00:00Z",
  "versionNumber": 1
}
```

#### GET `/goal/getAllGoals`
**Description**: Retrieves all goals from the system.

**Input**: None

**Output**:
- **Success (200 OK)**: Array of Goal objects
- **Error (500 Internal Server Error)**: Error message if loading fails

#### POST `/goal/saveOneGoal`
**Description**: Saves a single goal to the system.

**Input**: Goal object (same structure as above)

**Output**:
- **Success (200 OK)**: Array containing the saved goal

#### POST `/goal/saveMultipleGoals`
**Description**: Saves multiple goals to the system.

**Input**: Array of Goal objects

**Output**:
- **Success (200 OK)**: Array of saved goals

### Check-In Management Endpoints

#### GET `/checkins`
**Description**: Retrieves all check-ins from the system.

**Input**: None

**Output**:
- **Success (200 OK)**: Array of CheckInResponse objects
- **Error (500 Internal Server Error)**: Error message if retrieval fails

#### GET `/checkins/{id}`
**Description**: Retrieves a specific check-in by its ID.

**Input**: 
- `id` (path parameter): Long - check-in ID

**Output**:
- **Success (200 OK)**: CheckInResponse object
- **Error (404 Not Found)**: If check-in doesn't exist

#### POST `/checkins`
**Description**: Creates a new check-in.

**Input**: CheckInRequest object
```json
{
  "goalId": 123,
  "checkInDate": "2024-01-01T10:00:00",
  "notes": "Progress update notes"
}
```

**Output**:
- **Success (201 Created)**: CheckInResponse object
- **Error (500 Internal Server Error)**: Error message if creation fails

#### PATCH `/checkins/{id}`
**Description**: Updates an existing check-in.

**Input**: 
- `id` (path parameter): Long - check-in ID to update
- CheckInRequest object (same structure as POST)

**Output**:
- **Success (200 OK)**: Updated CheckInResponse object
- **Error (404 Not Found)**: If check-in doesn't exist
- **Error (500 Internal Server Error)**: Error message if update fails

#### GET `/checkins/index`
**Description**: Returns a welcome message for the check-in API.

**Input**: None

**Output**: 
- **Success (200 OK)**: String "Welcome to the CheckIn API! Use /checkins to view all or POST to create new check-ins."

**CheckInResponse Object Structure**:
```json
{
  "id": 123,
  "goalId": 456,
  "checkInDate": "2024-01-01T10:00:00",
  "notes": "Progress update notes",
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00",
  "version": 1
}
```

### API Ordering and Dependencies

The API endpoints are designed to be stateless and can be called in any order. However, for optimal usage:

1. **User Management**: Create users first before creating goals or check-ins
2. **Goal Management**: Goals can be created independently but may reference user IDs
3. **Check-In Management**: Check-ins should reference existing goal IDs

No specific ordering is required between different endpoint categories, but logical dependencies exist (e.g., check-ins should reference existing goals).

## Build and Run Instructions

### Prerequisites
- Java 21 or higher
- Maven 3.6 or higher
- Git (for cloning the repository)

### Building the Application

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd OnTrackED
   ```

2. **Build the project**:
   ```bash
   mvn clean compile
   ```

3. **Run tests**:
   ```bash
   mvn test
   ```

4. **Package the application**:
   ```bash
   mvn package
   ```

### Running the Application

1. **Run with Maven**:
   ```bash
   mvn spring-boot:run
   ```

2. **Run the JAR file**:
   ```bash
   java -jar target/ontracked-1.0.0-SNAPSHOT.jar
   ```

3. **Run with specific profile**:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

The application will start on `http://localhost:8080` by default.

### Configuration Files

The application uses the following configuration files:

- **`src/main/resources/application.properties`**: Main configuration file
  - Sets web application type to servlet
  - Configures logging to write to `logs/ontracked.log`
  - Sets root logging level to INFO

- **`pom.xml`**: Maven configuration file
  - Defines project dependencies and build configuration
  - Uses Spring Boot 3.3.4
  - Includes Spring Web, Validation, and Test dependencies

## Testing

### Running Tests

1. **Run all tests**:
   ```bash
   mvn test
   ```

2. **Run specific test class**:
   ```bash
   mvn test -Dtest=UserControllerTest
   ```

3. **Run tests with coverage**:
   ```bash
   mvn test jacoco:report
   ```

### Test Structure

The project includes comprehensive tests for:
- **Controller Tests**: `GoalControllerTest`, `UserControllerTest`
- **Model Tests**: `GoalTest`, `ProgressUpdateTest`, `UserClassTest`
- **DTO Tests**: `ProgressUpdateRequestTest`, `ProgressUpdateResponseTest`
- **Integration Tests**: `OnTrackEDApplicationTests`, `CheckInTests`

### Test Reports

Test reports are generated in the `target/surefire-reports/` directory after running tests.

## Third-Party Dependencies

The project uses the following third-party libraries:

### Spring Boot Framework
- **spring-boot-starter-web**: Provides web application capabilities and embedded Tomcat server
- **spring-boot-starter-validation**: Provides validation annotations (@Valid, @NotNull, etc.)
- **spring-boot-starter-test**: Provides testing framework integration with JUnit 5, Mockito, and Spring Test

### JSON Processing
- **gson (2.11.0)**: Google's JSON library for Java object serialization/deserialization

### Testing Framework
- **junit-jupiter (5.10.1)**: JUnit 5 testing framework for unit testing

### Optional Dependencies
- **lombok**: Code generation library (commented out in pom.xml, can be uncommented if needed)

**Note**: All dependencies are managed through Maven and are automatically resolved during the build process. No manual installation of third-party libraries is required.

## Project Structure

```
OnTrackED/
├── src/
│   ├── main/
│   │   ├── java/com/ontracked/
│   │   │   ├── controller/          # REST API controllers
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── model/               # Domain models
│   │   │   ├── service/             # Business logic services
│   │   │   └── OnTrackEDApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/ontracked/      # Test classes
├── target/                          # Build output directory
├── logs/                           # Application logs
├── goals.csv                       # Goals data storage
├── users.csv                       # Users data storage
├── pom.xml                         # Maven configuration
└── README.md                       # This file
```

### Key Components

- **Controllers**: Handle HTTP requests and responses
- **Services**: Implement business logic and data persistence
- **Models**: Define domain entities (User, Goal, CheckIn)
- **DTOs**: Define request/response data structures
- **Tests**: Comprehensive test coverage for all components

### Data Storage

The application uses CSV files for data persistence:
- `users.csv`: Stores user information
- `goals.csv`: Stores goal data
- `localGoalDB.csv`: Stores check-in data

All data is automatically loaded on application startup and persisted on data modifications.

## Project Management

This project uses Trello for project management and task tracking. You can access the project board at:

**[OnTrackED Project Board](https://trello.com/invite/b/68fabdb4b9fc1fd0165fc511/ATTI5e23863e65bfb41d848bf2c42a8714da475C4120/ontracked)**

The Trello board contains:
- Project tasks and milestones
- Feature development tracking
- Bug reports and issue management
- Sprint planning and progress monitoring
- Team collaboration and communication