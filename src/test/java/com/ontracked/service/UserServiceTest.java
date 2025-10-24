package com.ontracked.service;

import com.ontracked.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserService Tests")
class UserServiceTest {

    private final String testFilePath = "test_users.csv";

    /**
     * Sets up clean test environment before each test method.
     * Removes any existing CSV files to ensure isolated test conditions.
     */
    @BeforeEach
    void setUp() {
        // Clean up any existing test file
        cleanupTestFile();
    }

    /**
     * Cleans up test environment after each test method.
     * Ensures no test artifacts remain between test runs.
     */
    @AfterEach
    void tearDown() {
        cleanupTestFile();
    }

    /**
     * Helper method to delete test CSV files.
     * Removes both test-specific and default CSV files to prevent interference.
     */
    private void cleanupTestFile() {
        File file = new File(testFilePath);
        if (file.exists()) {
            file.delete();
        }
        // Also clean up the default CSV file
        File defaultFile = new File("users.csv");
        if (defaultFile.exists()) {
            defaultFile.delete();
        }
    }

    /**
     * Tests loadUsers method when no CSV file exists.
     * Verifies that an empty list is returned gracefully.
     */
    @Test
    @DisplayName("loadUsers - Returns empty list when file doesn't exist")
    void testLoadUsersEmptyFile() {
        List<User> users = UserService.loadUsers();
        assertTrue(users.isEmpty());
    }

    /**
     * Tests loadUsers method with existing CSV data.
     * Verifies that user data is correctly parsed and loaded from CSV file.
     */
    @Test
    @DisplayName("loadUsers - Loads existing users from CSV")
    void testLoadUsersWithData() throws Exception {
        // Create test CSV file
        String csvContent = "userId,fullName,email,role,userCreatedAt,userUpdatedAt\n" +
                           "1,\"John Doe\",\"john@test.com\",STUDENT,2024-01-01,\n";
        Files.writeString(Path.of("users.csv"), csvContent);

        List<User> users = UserService.loadUsers();
        assertEquals(1, users.size());
        assertEquals("John Doe", users.get(0).getFullName());
        assertEquals("john@test.com", users.get(0).getEmail());
    }

    /**
     * Tests saveUsers method creating a new CSV file.
     * Verifies that user data is successfully written to a new file.
     */
    @Test
    @DisplayName("saveUsers - Creates new CSV file with user data")
    void testSaveUsersNewFile() {
        User user = new User("Jane Smith", "jane@test.com", "TEACHER");
        List<User> users = Arrays.asList(user);

        UserService.saveUsers(users);

        File file = new File("users.csv");
        assertTrue(file.exists());
    }

    /**
     * Tests saveUsers method overwriting an existing CSV file.
     * Verifies that old data is replaced with new user data.
     */
    @Test
    @DisplayName("saveUsers - Overwrites existing CSV file")
    void testSaveUsersOverwrite() throws Exception {
        // Create initial file
        Files.writeString(Path.of("users.csv"), "old,data\n");
        
        User user = new User("New User", "new@test.com", "STUDENT");
        UserService.saveUsers(Arrays.asList(user));

        String content = Files.readString(Path.of("users.csv"));
        assertTrue(content.contains("New User"));
        assertFalse(content.contains("old,data"));
    }

    /**
     * Tests retrieveUser method with non-existent user ID.
     * Verifies that null is returned when user is not found.
     */
    @Test
    @DisplayName("retrieveUser - Returns null when user not found")
    void testRetrieveUserNotFound() {
        User result = UserService.retrieveUser(999);
        assertNull(result);
    }

    /**
     * Tests createUser method with valid input data.
     * Verifies that new user is created successfully with correct attributes.
     */
    @Test
    @DisplayName("createUser - Creates user with valid data")
    void testCreateUserValid() {
        User result = UserService.createUser("New User", "new@test.com", "COUNSELOR");
        
        assertNotNull(result);
        assertEquals("New User", result.getFullName());
        assertEquals("new@test.com", result.getEmail());
        assertEquals(User.Role.COUNSELOR, result.getRole());
    }

    /**
     * Tests createUser method with duplicate email address.
     * Verifies that IllegalArgumentException is thrown for email conflicts.
     */
    @Test
    @DisplayName("createUser - Throws exception for duplicate email")
    void testCreateUserDuplicateEmail() {
        UserService.createUser("First User", "duplicate@test.com", "STUDENT");
        
        assertThrows(IllegalArgumentException.class, () -> {
            UserService.createUser("Second User", "duplicate@test.com", "TEACHER");
        });
    }

    /**
     * Tests updateUser method with non-existent user ID.
     * Verifies that IllegalArgumentException is thrown when user not found.
     */
    @Test
    @DisplayName("updateUser - Throws exception when user not found")
    void testUpdateUserNotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            UserService.updateUser(999, "Test", "test@test.com", "STUDENT");
        });
    }    
}