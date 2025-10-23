package com.ontracked.dto.user;

/**
 * DTO for creating new user.
 */
public class CreateUserRequest {
    private String fullName;
    private String email;
    private String role;

    /**
     * Simple constructor for CreateUerRequest.
     * 
     * @param fullName user full name
     * @param email    user email
     * @param role     user role (STUDENT, TEACHER, or COUNSELOR)
     */
    public CreateUserRequest(String fullName, String email, String role) {
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    /**
     * Getters.
     */
    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
