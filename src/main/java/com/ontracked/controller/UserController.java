package com.ontracked.controller;

import com.ontracked.model.User;
import com.ontracked.dto.user.CreateUserRequest;
import com.ontracked.dto.user.UserResponse;
import com.ontracked.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * REST Controller for managing user-related operations.
 * Provides endpoints for creating, retrieving, and updating user information.
 */
@RestController
public class UserController {

    /**
     * Retrieves all users from the system.
     * 
     * @return ResponseEntity containing a list of all users and HTTP 200 OK status
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = UserService.loadUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Retrieves a specific user by their unique identifier.
     * 
     * @param id unique id of searched user
     * @return ResponseEntity containing the user and HTTP 200 OK status if found,
     *         or HTTP 404 NOT FOUND status if user doesn't exist
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        User user = UserService.retrieveUser(id);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {
        try {
            User newUser = UserService.createUser(request.getFullName(), request.getEmail(), request.getRole());
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody CreateUserRequest request) {
        try {
            User updatedUser = UserService.updateUser(id, request.getFullName(), request.getEmail(), request.getRole());
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}