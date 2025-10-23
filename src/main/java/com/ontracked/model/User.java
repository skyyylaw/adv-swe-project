package com.ontracked.model;

import java.time.LocalDate;

/**
 * User class.
 */
public class User {
    /**
     * Role can only take these three values.
     */
    public enum Role {
        STUDENT, TEACHER, COUNSELOR
    }

    /**
     * Automaticaly sets uniqueId for each user.
     */
    private static int uniqueId = 1;

    private int id;
    private String fullName;
    private String email;
    private Role role;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    /**
     * Basic constructor for User class.
     * 
     * @param fullName full name of user
     * @param email    email address of user
     * @param role     role of user as string
     * @throws IllegalArgumentException if role string is not valid
     */
    public User(String fullName, String email, String role) {
        this.id = uniqueId++;
        this.fullName = fullName;
        this.email = email;
        this.role = Role.valueOf(role);
        this.createdAt = LocalDate.now();
    }

    /**
     * Getters and setters.
     */
    public int getUserId() {
        return this.id;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = Role.valueOf(role);
    }

    public LocalDate getUserCreatedAt() {
        return this.createdAt;
    }

    public LocalDate getUserUpdatedAt() {
        return this.updatedAt;
    }
}