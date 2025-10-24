package com.ontracked.controller;

import com.ontracked.model.User;
import com.ontracked.dto.user.CreateUserRequest;
import com.ontracked.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    private void logRequest(HttpServletRequest request, String endpoint) {
        logger.info(
                "Timestamp: {}, Origin: {}, Method: {}, Endpoint: {}",
                java.time.Instant.now(),
                request.getRemoteAddr(),
                request.getMethod(),
                endpoint
        );
    }

    /**
     * Retrieves all users from the system.
     * 
     * @return ResponseEntity containing a list of all users and HTTP 200 OK status
     */
    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers(HttpServletRequest request) {
        logRequest(request, "/");
        try {
            List<User> users = UserService.loadUsers(); // Use static method
            logger.info("Successfully loaded {} users", users.size());
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to load users", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a specific user by their unique identifier.
     * 
     * @param id unique id of searched user
     * @return ResponseEntity containing the user and HTTP 200 OK status if found,
     *         or HTTP 404 NOT FOUND status if user doesn't exist
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id, HttpServletRequest request) {
        logRequest(request, "/users/" + id);
        try {
            User user = UserService.retrieveUser(id); // Use static method
            if (user != null) {
                logger.info("Successfully retrieved user: {} (ID: {})", user.getFullName(), id);
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                logger.warn("User not found with ID: {}", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Failed to retrieve user with ID: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Creates a new user with the provided information.
     * Validates that the email is unique before creating the user.
     * 
     * @param request the CreateUserRequest containing user information (fullName, email, role)
     * @return ResponseEntity containing the newly created user and HTTP 201 CREATED status if successful,
     *         or HTTP 400 BAD REQUEST status if email already exists or role is invalid
     */
    @PostMapping("/createUser")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request, HttpServletRequest httpRequest) {
        logRequest(httpRequest, "/createUser");
        try {
            User newUser = UserService.createUser(request.getFullName(), request.getEmail(), request.getRole()); // Use static method
            logger.info("Successfully created user: {} (ID: {})", newUser.getFullName(), newUser.getUserId());
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to create user: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Failed to create user: {}", request.getFullName(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates an existing user's information.
     * Validates email uniqueness if email is being changed.
     * Only updates fields that are provided in the request.
     * 
     * @param id unique id of updated user
     * @param request the CreateUserRequest containing updated user information
     * @return ResponseEntity containing the updated user and HTTP 200 OK status if successful,
     *         or HTTP 400 BAD REQUEST status if user not found, email already exists, or role is invalid
     */
    @PutMapping("/updateUser/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody CreateUserRequest request, HttpServletRequest httpRequest) {
        logRequest(httpRequest, "/updateUser/" + id);
        try {
            User updatedUser = UserService.updateUser(
                    id, request.getFullName(), request.getEmail(), request.getRole()
            ); // Use static method
            logger.info("Successfully updated user: {} (ID: {})", updatedUser.getFullName(), id);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to update user with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Failed to update user with ID: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}