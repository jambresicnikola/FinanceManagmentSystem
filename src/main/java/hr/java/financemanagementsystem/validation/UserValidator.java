package hr.java.financemanagementsystem.validation;

import hr.java.financemanagementsystem.dto.UserEditProfileForm;
import hr.java.financemanagementsystem.dto.UserRegistrationForm;
import hr.java.financemanagementsystem.exception.PasswordChangeException;
import hr.java.financemanagementsystem.exception.UserValidationException;

/**
 * Validates user input for registration, sign in, profile editing and password changes.
 */
public class UserValidator {
    private UserValidator() {}

    /**
     * Validates a user registration form.
     * Collects all validation errors and throws them together in one exception.
     * @param form the registration form to validate
     * @throws UserValidationException if any field is invalid
     */
    public static void validateUserRegistration(UserRegistrationForm form) {
        StringBuilder errorMessage = new StringBuilder();

        if (form.getFirstName() == null || form.getFirstName().isBlank()) {
            errorMessage.append("\nFirst name cannot be empty.");
        } else if (form.getFirstName().length() > 100) {
            errorMessage.append("\nFirst name cannot be longer than 100 characters.");
        }

        if (form.getLastName() == null || form.getLastName().isBlank()) {
            errorMessage.append("\nLast name cannot be empty.");
        } else if (form.getLastName().length() > 150) {
            errorMessage.append("\nLast name cannot be longer than 150 characters.");
        }

        if (form.getUsername() == null || form.getUsername().isBlank()) {
            errorMessage.append("\nUsername cannot be empty.");
        } else if (form.getUsername().length() > 100) {
            errorMessage.append("\nUsername cannot be longer than 100 characters.");
        }

        if (form.getPassword() == null || form.getPassword().isEmpty()
                || form.getConfirmPassword() == null || form.getConfirmPassword().isEmpty()) {
            errorMessage.append("\nPasswords cannot be empty.");
        } else if (!form.getPassword().equals(form.getConfirmPassword())) {
            errorMessage.append("\nPasswords do not match.");
        }

        if (!errorMessage.isEmpty()) {
            throw new UserValidationException(errorMessage.toString());
        }
    }

    /**
     * Validates sign in credentials.
     * @param username the username to validate
     * @param password the password to validate
     * @throws UserValidationException if username or password is empty
     */
    public static void validateUserSignIn(String username, String password) {
        StringBuilder errorMessage = new StringBuilder();

        if (username == null || username.isBlank()) {
            errorMessage.append("\nPlease enter your username.");
        }

        if (password == null || password.isEmpty()) {
            errorMessage.append("\nPlease enter your password.");
        }

        if (!errorMessage.isEmpty()) {
            throw new UserValidationException(errorMessage.toString());
        }
    }

    /**
     * Validates a user profile edit form.
     * Collects all validation errors and throws them together in one exception.
     * @param form the profile edit form to validate
     * @throws UserValidationException if any field is invalid
     */
    public static void validateUserEditProfile(UserEditProfileForm form) {
        StringBuilder errorMessage = new StringBuilder();

        if (form.getFirstName() == null || form.getFirstName().isBlank()) {
            errorMessage.append("\nFirst name cannot be empty.");
        } else if (form.getFirstName().length() > 100) {
            errorMessage.append("\nFirst name cannot be longer than 100 characters.");
        }

        if (form.getLastName() == null || form.getLastName().isBlank()) {
            errorMessage.append("\nLast name cannot be empty.");
        } else if (form.getLastName().length() > 150) {
            errorMessage.append("\nLast name cannot be longer than 150 characters.");
        }

        if (form.getUsername() == null || form.getUsername().isBlank()) {
            errorMessage.append("\nUsername cannot be empty.");
        } else if (form.getUsername().length() > 100) {
            errorMessage.append("\nUsername cannot be longer than 100 characters.");
        }

        if (!errorMessage.isEmpty()) {
            throw new UserValidationException(errorMessage.toString());
        }
    }

    /**
     * Validates a password change request.
     * @param currentPassword the user's current password
     * @param newPassword the new password
     * @param confirmNewPassword must match the new password
     * @throws PasswordChangeException if any field is empty or the passwords don't match
     */
    public static void validateUserChangePassword(String currentPassword, String newPassword, String confirmNewPassword)
    throws PasswordChangeException {
        StringBuilder errorMessage = new StringBuilder();

        if (currentPassword == null || currentPassword.isEmpty()) {
            errorMessage.append("\nPlease enter your current password.");
        }

        if (newPassword == null || newPassword.isEmpty()) {
            errorMessage.append("\nNew password cannot be empty.");
        } else if (!newPassword.equals(confirmNewPassword)) {
            errorMessage.append("\nPasswords do not match.");
        }

        if (!errorMessage.isEmpty()) {
            throw new PasswordChangeException(errorMessage.toString());
        }
    }
}