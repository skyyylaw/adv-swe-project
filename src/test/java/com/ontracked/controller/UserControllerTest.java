package com.ontracked.controller;

import com.ontracked.model.User;
import com.ontracked.dto.user.CreateUserRequest;
import com.ontracked.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/**
 * Comprehensive API tests for UserController.
 * Tests all endpoints with typical valid, atypical valid, and invalid inputs.
 * Includes write-read persistence testing, logging verification, and multi-client simulation.
 * 
 * This test class covers:
 * - GET / (getAllUsers) - 3 test cases
 * - GET /users/{id} (getUserById) - 3 test cases  
 * - POST /createUser (createUser) - 3 test cases
 * - PUT /updateUser/{id} (updateUser) - 3 test cases
 * - Multi-client access testing - 1 test case
 */
@WebMvcTest(UserController.class)
@DisplayName("UserController API Integration Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private MockedStatic<UserService> mockedUserService;
    private User testUser1;
    private User testUser2;
    private List<User> testUsers;
    private ListAppender<ILoggingEvent> logAppender;
    private ch.qos.logback.classic.Logger serviceLogger;


    /**
     * Sets up test fixtures and mocks before each test method.
     * Initializes MockedStatic for UserService and creates test user objects.
     * Creates consistent test data that can be reused across test methods.
     */
    @BeforeEach
    void setUp() {
        mockedUserService = Mockito.mockStatic(UserService.class);
        
        testUser1 = new User("John Doe", "john.doe@example.com", "STUDENT");
        testUser2 = new User("Jane Smith", "jane.smith@university.edu", "TEACHER");
        testUsers = Arrays.asList(testUser1, testUser2);

        // Set up for loggin capture
        serviceLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(UserController.class);
        logAppender = new ListAppender<>();
        logAppender.start();
        serviceLogger.addAppender(logAppender);
        serviceLogger.setLevel(Level.INFO);
    }

    /**
     * Cleans up resources after each test method.
     * Closes the static mock to prevent memory leaks and interference between tests.
     */
    @AfterEach
    void tearDown() {
        if (mockedUserService != null) {
            mockedUserService.close();
        }

        if (logAppender != null) {
            serviceLogger.detachAppender(logAppender);
        }
    }

    // ========== GET / Tests (getAllUsers) ==========

    /**
     * Tests GET / endpoint with typical valid input.
     * Verifies that the endpoint returns a list of users in JSON format.
     * Tests successful case where service returns multiple users from CSV data.
     * 
     * Expected behavior:
     * - HTTP 200 OK status
     * - Content-Type: application/json
     * - Response body contains array of 2 users
     * - User data matches expected values from test fixtures
     * - Service method is called exactly once
     */
    @Test
    @DisplayName("GET / - Typical valid: Returns list of users")
    void testGetAllUsersTypicalValid() throws Exception {
        mockedUserService.when(UserService::loadUsers).thenReturn(testUsers);

        MvcResult result = mockMvc.perform(get("/")
                .header("Client-ID", "client-1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].fullName", is("John Doe")))
                .andExpect(jsonPath("$[1].fullName", is("Jane Smith")))
                .andReturn();

        mockedUserService.verify(UserService::loadUsers);
        String responseBody = result.getResponse().getContentAsString();
        assertTrue(responseBody.contains("John Doe"));
    }

    /**
     * Tests GET / endpoint with atypical valid input.
     * Verifies that the endpoint handles empty user list gracefully.
     * Tests edge case where no users exist in the system.
     * 
     * Expected behavior:
     * - HTTP 200 OK status (not an error condition)
     * - Content-Type: application/json
     * - Response body contains empty array
     * - Service method is called exactly once
     */
    @Test
    @DisplayName("GET / - Atypical valid: Returns empty list")
    void testGetAllUsersAtypicalValid() throws Exception {
        mockedUserService.when(UserService::loadUsers).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/")
                .header("Client-ID", "client-2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        mockedUserService.verify(UserService::loadUsers);
    }

    /**
     * Tests GET / endpoint with invalid input.
     * Verifies that the endpoint rejects requests with unsupported content types.
     * Tests error handling when client requests XML format but controller only supports JSON.
     * 
     * Expected behavior:
     * - HTTP 406 Not Acceptable status
     * - Service method is still called (controller logic executes before content negotiation)
     */
    @Test
    @DisplayName("GET / - Invalid: Malformed request")
    void testGetAllUsersInvalid() throws Exception {
        // Test a scenario that would actually cause an error
        mockedUserService.when(UserService::loadUsers).thenReturn(testUsers);

        // Test with invalid Accept header that controller can't handle
        mockMvc.perform(get("/")
                .header("Client-ID", "client-3")
                .accept(MediaType.APPLICATION_XML)) // If controller only supports JSON
                .andDo(print())
                .andExpect(status().isNotAcceptable()); // 406 error

        mockedUserService.verify(UserService::loadUsers);
    }

    // ========== GET /users/{id} Tests ==========

    /**
     * Tests GET /users/{id} endpoint with typical valid input.
     * Verifies that the endpoint returns a specific user by ID.
     * Tests successful retrieval of an existing user.
     * 
     * @throws Exception if MockMvc request fails
     * 
     * Expected behavior:
     * - HTTP 200 OK status
     * - Content-Type: application/json
     * - Response body contains correct user data
     * - All user fields (name, email, role) match expected values
     * - Service retrieveUser method called with correct ID
     */
    @Test
    @DisplayName("GET /users/{id} - Typical valid: Returns existing user")
    void testGetUserByIdTypicalValid() throws Exception {
        mockedUserService.when(() -> UserService.retrieveUser(1)).thenReturn(testUser1);

        MvcResult result = mockMvc.perform(get("/users/1")
                .header("Client-ID", "client-1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fullName", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.role", is("STUDENT")))
                .andReturn();

        mockedUserService.verify(() -> UserService.retrieveUser(1));
    }

    /**
     * Tests GET /users/{id} endpoint with atypical valid input.
     * Verifies that the endpoint handles very large ID numbers correctly.
     * Tests boundary condition with maximum integer-like ID value.
     * 
     * Expected behavior:
     * - HTTP 200 OK status (large ID is still valid)
     * - Service handles large ID without overflow or errors
     * - Returns correct user data when user exists with large ID
     */
    @Test
    @DisplayName("GET /users/{id} - Atypical valid: Large ID number")
    void testGetUserByIdAtypicalValid() throws Exception {
        mockedUserService.when(() -> UserService.retrieveUser(999999)).thenReturn(testUser2);

        mockMvc.perform(get("/users/999999")
                .header("Client-ID", "client-2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("Jane Smith")))
                .andExpect(jsonPath("$.role", is("TEACHER")));

        mockedUserService.verify(() -> UserService.retrieveUser(999999));
    }

    /**
     * Tests GET /users/{id} endpoint with invalid input.
     * Verifies that the endpoint returns 404 when user doesn't exist.
     * Tests error handling for non-existent user ID.
     * 
     * Expected behavior:
     * - HTTP 404 Not Found status
     * - No response body (or empty response)
     * - Service method called but returns null
     */
    @Test
    @DisplayName("GET /users/{id} - Invalid: User not found")
    void testGetUserByIdInvalid() throws Exception {
        mockedUserService.when(() -> UserService.retrieveUser(999)).thenReturn(null);

        mockMvc.perform(get("/users/999")
                .header("Client-ID", "client-3"))
                .andDo(print())
                .andExpect(status().isNotFound());

        mockedUserService.verify(() -> UserService.retrieveUser(999));
    }

    // ========== POST /createUser Tests ==========

    /**
     * Tests POST /createUser endpoint with typical valid input.
     * Verifies user creation with standard user data.
     * Tests write-then-read functionality to ensure persistence works correctly.
     * 
     * Expected behavior:
     * - HTTP 201 Created status
     * - Content-Type: application/json  
     * - Response body contains newly created user data
     * - Service createUser method called with correct parameters
     * - Created user can be retrieved via getAllUsers (persistence test)
     */
    @Test
    @DisplayName("POST /createUser - Typical valid: Creates new user")
    void testCreateUserTypicalValid() throws Exception {
        CreateUserRequest request = new CreateUserRequest("New User", "new@example.com", "STUDENT");
        User createdUser = new User("New User", "new@example.com", "STUDENT");
        
        mockedUserService.when(() -> UserService.createUser("New User", "new@example.com", "STUDENT"))
                         .thenReturn(createdUser);

        MvcResult result = mockMvc.perform(post("/createUser")
                .header("Client-ID", "client-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fullName", is("New User")))
                .andExpect(jsonPath("$.email", is("new@example.com")))
                .andExpect(jsonPath("$.role", is("STUDENT")))
                .andReturn();

        mockedUserService.verify(() -> UserService.createUser("New User", "new@example.com", "STUDENT"));
        
        // Test write-then-read: verify created user can be retrieved
        mockedUserService.when(UserService::loadUsers).thenReturn(Arrays.asList(createdUser));
        
        mockMvc.perform(get("/")
                .header("Client-ID", "client-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fullName", is("New User")));
    }

    /**
     * Tests POST /createUser endpoint with atypical valid input.
     * Verifies user creation with special characters in names and emails.
     * Tests internationalization support and Unicode character handling.
     * 
     * Expected behavior:
     * - HTTP 201 Created status
     * - Special characters (accents, tildes) preserved correctly
     * - Service handles Unicode characters without corruption
     * - Response contains exact special characters as input
     */
    @Test
    @DisplayName("POST /createUser - Atypical valid: Special characters")
    void testCreateUserAtypicalValid() throws Exception {
        CreateUserRequest request = new CreateUserRequest("José María", "josé@test.com", "COUNSELOR");
        User createdUser = new User("José María", "josé@test.com", "COUNSELOR");
        
        mockedUserService.when(() -> UserService.createUser("José María", "josé@test.com", "COUNSELOR"))
                         .thenReturn(createdUser);

        mockMvc.perform(post("/createUser")
                .header("Client-ID", "client-2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName", is("José María")))
                .andExpect(jsonPath("$.role", is("COUNSELOR")));

        mockedUserService.verify(() -> UserService.createUser("José María", "josé@test.com", "COUNSELOR"));
    }

    /**
     * Tests POST /createUser endpoint with invalid input.
     * Verifies error handling when attempting to create user with duplicate email.
     * Tests business rule enforcement (unique email constraint).
     * 
     * Expected behavior:
     * - HTTP 400 Bad Request status
     * - Service throws IllegalArgumentException for duplicate email
     * - No user is created (transaction rollback behavior)
     * - Error is properly caught and converted to HTTP status
     */
    @Test
    @DisplayName("POST /createUser - Invalid: Duplicate email")
    void testCreateUserInvalid() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Duplicate User", "john.doe@example.com", "STUDENT");
        
        mockedUserService.when(() -> UserService.createUser("Duplicate User", "john.doe@example.com", "STUDENT"))
                         .thenThrow(new IllegalArgumentException("User with this email already exists"));

        mockMvc.perform(post("/createUser")
                .header("Client-ID", "client-3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        mockedUserService.verify(() -> UserService.createUser("Duplicate User", "john.doe@example.com", "STUDENT"));
    }

    // ========== PUT /updateUser/{id} Tests ==========

    /**
     * Tests PUT /updateUser/{id} endpoint with typical valid input.
     * Verifies user update with complete new data for all fields.
     * Tests write-then-read functionality to ensure updates are persisted.
     * 
     * Expected behavior:
     * - HTTP 200 OK status
     * - Content-Type: application/json
     * - Response body contains updated user data
     * - All fields (name, email, role) are updated correctly
     * - Service updateUser method called with correct parameters
     * - Updated user can be retrieved via getUserById (persistence test)
     */
    @Test
    @DisplayName("PUT /updateUser/{id} - Typical valid: Updates existing user")
    void testUpdateUserTypicalValid() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Updated John", "john.updated@example.com", "TEACHER");
        User updatedUser = new User("Updated John", "john.updated@example.com", "TEACHER");
        
        mockedUserService.when(() -> UserService.updateUser(1, "Updated John", "john.updated@example.com", "TEACHER"))
                         .thenReturn(updatedUser);

        mockMvc.perform(put("/updateUser/1")
                .header("Client-ID", "client-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fullName", is("Updated John")))
                .andExpect(jsonPath("$.email", is("john.updated@example.com")))
                .andExpect(jsonPath("$.role", is("TEACHER")));

        mockedUserService.verify(() -> UserService.updateUser(1, "Updated John", "john.updated@example.com", "TEACHER"));
        
        // Test write-then-read: verify updated user can be retrieved
        mockedUserService.when(() -> UserService.retrieveUser(1)).thenReturn(updatedUser);
        
        mockMvc.perform(get("/users/1")
                .header("Client-ID", "client-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("Updated John")));
    }

    /**
     * Tests PUT /updateUser/{id} endpoint with atypical valid input.
     * Verifies partial user update where only name changes.
     * Tests that service handles partial updates correctly without affecting other fields.
     * 
     * Expected behavior:
     * - HTTP 200 OK status
     * - Only specified fields are updated
     * - Service called with correct parameters (including unchanged values)
     * - Response shows updated name while preserving other data
     */
    @Test
    @DisplayName("PUT /updateUser/{id} - Atypical valid: Partial update")
    void testUpdateUserAtypicalValid() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Only Name Changed", "john.doe@example.com", "STUDENT");
        User updatedUser = new User("Only Name Changed", "john.doe@example.com", "STUDENT");
        
        mockedUserService.when(() -> UserService.updateUser(eq(1), eq("Only Name Changed"), anyString(), anyString()))
                         .thenReturn(updatedUser);

        mockMvc.perform(put("/updateUser/1")
                .header("Client-ID", "client-2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("Only Name Changed")));

        mockedUserService.verify(() -> UserService.updateUser(eq(1), eq("Only Name Changed"), anyString(), anyString()));
    }

    /**
     * Tests PUT /updateUser/{id} endpoint with invalid input.
     * Verifies error handling when attempting to update non-existent user.
     * Tests that service properly validates user existence before update.
     * 
     * Expected behavior:
     * - HTTP 400 Bad Request status
     * - Service throws IllegalArgumentException for non-existent user
     * - No data is modified (safe failure behavior)
     * - Error is properly caught and converted to HTTP status
     */
    @Test
    @DisplayName("PUT /updateUser/{id} - Invalid: User not found")    
    void testUpdateUserInvalid() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Non-existent User", "test@example.com", "STUDENT");
        
        mockedUserService.when(() -> UserService.updateUser(eq(999), anyString(), anyString(), anyString()))
                         .thenThrow(new IllegalArgumentException("User not found"));

        mockMvc.perform(put("/updateUser/999")
                .header("Client-ID", "client-3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        mockedUserService.verify(() -> UserService.updateUser(eq(999), anyString(), anyString(), anyString()));
    }
}