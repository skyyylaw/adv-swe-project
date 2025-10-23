package com.ontracked.service;

import java.io.*;
import java.util.*;
import java.time.LocalDate;
import com.ontracked.model.User;
import org.springframework.stereotype.Service;


/**
 * User Service class. 
 * Provides useful methods for getting, creating and updating 
 * user information.
 */
@Service
public class UserService {
    private static final String FILE_PATH = "users.csv";
    private ArrayList<User> users;

    public UserService() {
    }

    /**
     * Saves a list of users to persistent storage.
     * 
     * @param users the list of users to save
     */
    public static void saveUsers(List<User> users) {
        saveUsersToCsv(users);
    }

    /**
     * Loads all users from persistent storage.
     * 
     * @return list of all users, empty list if no users exist
     */
    public static List<User> loadUsers() {
        return loadUsersFromCsv();
    }

    /**
     * Retrieves a specific user by their unique ID.
     * 
     * @param id the unique identifier of the user
     * @return the user if found, null otherwise
     */
    public static User retrieveUser(int id) {
        List<User> allUsers = loadUsers();
        for (User u : allUsers) {
            if (u.getUserId() == id) {
                return u;
            }
        }
        return null;
    }
    
    /**
     * Creates a new user with the provided information.
     * Validates that the email is unique before creating the user.
     * 
     * @param fullName the full name of the user
     * @param email the email address of the user (must be unique)
     * @param role the role of the user (STUDENT, TEACHER, or COUNSELOR)
     * @return the newly created user
     * @throws IllegalArgumentException if email already exists or role is invalid
     */
    public static User createUser(String fullName, String email, String role) {
        List<User> existingUsers = loadUsers();
        
        for (User u : existingUsers) {
            if (u.getEmail().equals(email)) {
                throw new IllegalArgumentException("User with this email already exists");
            }
        }
        
        User newUser = new User(fullName, email, role);
        existingUsers.add(newUser);
        saveUsers(existingUsers);
        return newUser;
    }

    /**
     * Updates an existing user's information.
     * Validates email uniqueness if email is being changed.
     * Updates the user's updatedAt timestamp.
     * 
     * @param userId the unique identifier of the user to update
     * @param fullName the new full name (ignored if null or empty)
     * @param email the new email address (ignored if null or empty)
     * @param role the new role (ignored if null or empty)
     * @return the updated user
     * @throws IllegalArgumentException if user not found, email already exists, or role is invalid
     */
    public static User updateUser(int userId, String fullName, String email, String role) {
        List<User> existingUsers = loadUsers();
        User userToUpdate = null;
        
        for (User u : existingUsers) {
            if (u.getUserId() == userId) {
                userToUpdate = u;
                break;
            }
        }
        
        if (userToUpdate == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        if (!userToUpdate.getEmail().equals(email)) {
            for (User u : existingUsers) {
                if (u.getEmail().equals(email) && u.getUserId() != userId) {
                    throw new IllegalArgumentException("Email already exists for another user");
                }
            }
        }
        
        if (fullName != null && !fullName.trim().isEmpty()) {
            userToUpdate.setFullName(fullName);
        }
        if (email != null && !email.trim().isEmpty()) {
            userToUpdate.setEmail(email);
        }
        if (role != null && !role.trim().isEmpty()) {
            userToUpdate.setRole(role);
        }
        
        userToUpdate.setUserUpdatedAt(LocalDate.now());
        
        saveUsers(existingUsers);
        return userToUpdate;
    }

    /**
     * Saves a list of users to a CSV file.
     * Overwrites the existing file with the provided user data.
     * 
     * @param users the list of users to save to CSV
     * @throws RuntimeException if file writing fails
     */
    public static void saveUsersToCsv(List<User> users) {
        File file = new File(FILE_PATH);

        try (PrintWriter writer = new PrintWriter(new FileWriter(file, false))) {
            writer.println("userId,fullName,email,role,userCreatedAt,userUpdatedAt");

            for (User u : users) {
                writer.printf("%d,%s,%s,%s,%s,%s%n",
                        u.getUserId(),
                        escapeCsv(u.getFullName()),
                        escapeCsv(u.getEmail()),
                        u.getRole().toString(),
                        u.getUserCreatedAt(),
                        u.getUserUpdatedAt());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write users CSV", e);
        }
    }

    /**
     * Loads users from a CSV file.
     * Parses each line of the CSV and creates User objects.
     * 
     * @return list of users loaded from CSV, empty list if file doesn't exist
     * @throws RuntimeException if CSV data is invalid or corrupted
     */
    public static List<User> loadUsersFromCsv() {
        List<User> users = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        if (!file.exists()) {
            return users; 
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = splitCsv(line);
                if (parts.length < 6) continue;

                User u = new User(parts[1], parts[2], parts[3]);
                users.add(u);
            }
        } catch (IOException e) {
            return List.of();
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid user data in CSV", e);
        }
        return users;
    }

    /**
     * Helper function that escapes special characters in CSV field values.
     * Wraps values in quotes and escapes existing quotes by doubling them.
     * 
     * @param s the string to escape for CSV format
     * @return the escaped string safe for CSV storage
     */
    private static String escapeCsv(String s) {
        if (s == null) return "";
        return '"' + s.replace("\"", "\"\"") + '"';
    }

     /**
     * Helper function that splits a CSV line into individual field values.
     * Handles quoted fields that may contain commas or quotes.
     * 
     * @param line the CSV line to split
     * @return array of field values from the CSV line
     */
    private static String[] splitCsv(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == '"') inQuotes = !inQuotes;
            else if (c == ',' && !inQuotes) {
                result.add(sb.toString().replace("\"\"", "\""));
                sb.setLength(0);
            } else sb.append(c);
        }
        result.add(sb.toString().replace("\"\"", "\""));
        return result.toArray(new String[0]);
    }
}