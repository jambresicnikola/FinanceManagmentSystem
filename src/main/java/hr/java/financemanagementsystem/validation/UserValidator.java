package hr.java.financemanagementsystem.validation;

import hr.java.financemanagementsystem.dto.UserEditProfileForm;
import hr.java.financemanagementsystem.dto.UserRegistrationForm;
import hr.java.financemanagementsystem.exception.PasswordChangeException;
import hr.java.financemanagementsystem.exception.UserValidationException;

public class UserValidator {
    private UserValidator() {}

    public static void validateUserRegistration(UserRegistrationForm userRegistrationForm) throws UserValidationException {
        StringBuilder errorMessage = new StringBuilder();

        if (userRegistrationForm.getFirstName().isEmpty()) {
            errorMessage.append("\nFirst name cannot be empty or contain more than 100 characters.");
        }

        if (userRegistrationForm.getLastName().isEmpty()) {
            errorMessage.append("\nLast name cannot be empty or contain more than 150 characters.");
        }

        if (userRegistrationForm.getUsername().isEmpty()) {
            errorMessage.append("\nUsername cannot be empty or contain more than 100 characters.");
        }

        if (userRegistrationForm.getPassword().isEmpty() || userRegistrationForm.getConfirmPassword().isEmpty()) {
            errorMessage.append("\nPasswords cannot be empty.");
        } else if (!userRegistrationForm.getPassword().equals(userRegistrationForm.getConfirmPassword())) {
            errorMessage.append("\nPasswords do not match.");
        }

        if (!errorMessage.isEmpty()) {
            throw new UserValidationException(errorMessage.toString());
        }
    }

    public static void validateUserSignIn(String username, String password) {
        StringBuilder errorMessage = new StringBuilder();

        if (username.isEmpty()) {
            errorMessage.append("\nPlease enter your username.");
        }

        if (password.isEmpty()) {
            errorMessage.append("\nPlease enter your password.");
        }

        if (!errorMessage.isEmpty()) {
            throw new UserValidationException(errorMessage.toString());
        }
    }

    public static void validateUserEditProfile(UserEditProfileForm userEditProfileForm) {
        StringBuilder errorMessage = new StringBuilder();

        if (userEditProfileForm.getFirstName().isEmpty()) {
            errorMessage.append("\nFirst name cannot be empty or contain more than 100 characters.");
        }

        if (userEditProfileForm.getLastName().isEmpty()) {
            errorMessage.append("\nLast name cannot be empty or contain more than 150 characters.");
        }

        if (userEditProfileForm.getUsername().isEmpty()) {
            errorMessage.append("\nUsername cannot be empty or contain more than 100 characters.");
        }

        if (!errorMessage.isEmpty()) {
            throw new UserValidationException(errorMessage.toString());
        }
    }

    public static void validateUserChangePassword(String currentPassword, String newPassword, String confirmNewPassword)
    throws PasswordChangeException {
        StringBuilder errorMessage = new StringBuilder();

        if (currentPassword.isEmpty()) {
            errorMessage.append("\nPlease enter your current password to confirm changes.");
        }

        if (newPassword.isEmpty() || !newPassword.equals(confirmNewPassword)) {
            errorMessage.append("\nPasswords do not match.");
        }

        if (!errorMessage.isEmpty()) {
            throw new PasswordChangeException(errorMessage.toString());
        }
    }
}