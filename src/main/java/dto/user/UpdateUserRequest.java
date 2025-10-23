package dto.user;

/**
 * DTO for updating existing user.
 */
public class UpdateUserRequest {
    private String fullName;
    private String email;
    private String role;

    /**
     * Simple constructor for UpdateUserRequest.
     * 
     * @param fullName user full name
     * @param email    user email
     * @param role     user role (STUDENT, TEACHER, or COUNSELOR)
     */
    public UpdateUserRequest(String fullName, String email, String role) {
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }
}