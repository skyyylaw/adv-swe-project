package com.ontracked.dto.user;

import java.time.LocalDate;

/**
 * DTO for getting user info returned by API.
 */
public class UserResponse {
    private int id;
    private String fullName;
    private String email;
    private String role;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    /**
     * Full constructor for UserResponse with all user details.
     * 
     * @param id        user unique id
     * @param fullName  user full name
     * @param email     user email
     * @param role      user role (STUDENT, TEACHER, or COUNSELOR)
     * @param createdAt timestamp when user created
     * @param updatedAt timestamp when user updated
     */
    public UserResponse(int id, String fullName, String email, String role, LocalDate createdAt, LocalDate updatedAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Getters.
     */
    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }
}
