package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.dto.UserEditProfileForm;
import hr.java.financemanagementsystem.exception.UserValidationException;
import hr.java.financemanagementsystem.service.DialogService;
import hr.java.financemanagementsystem.service.UserService;
import hr.java.financemanagementsystem.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the Manage Account screen.
 * Allows the user to update their profile, change their password or delete their account.
 */
public class ManageAccountScreenController {
    private static final Logger logger = LoggerFactory.getLogger(ManageAccountScreenController.class);

    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;

    /**
     * Pre-fills the form with the current user's profile data when the screen loads.
     */
    public void initialize() {
        firstNameTextField.setText(UserService.getLoggedInUser().getFirstName());
        lastNameTextField.setText(UserService.getLoggedInUser().getLastName());
        usernameTextField.setText(UserService.getLoggedInUser().getUsername());

        logger.debug("Manage account screen loaded for user with id: {}",
                UserService.getLoggedInUser().getId());
    }

    /**
     * Called when the user clicks the save button.
     * Reads the form fields and attempts to update the user's profile.
     * Shows an error dialog if the input is invalid.
     */
    public void saveChanges() {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String username = usernameTextField.getText();
        String password = passwordField.getText();

        logger.debug("Attempting to update profile for user with id: {}", UserService.getLoggedInUser().getId());

        UserEditProfileForm userEditProfileForm = new UserEditProfileForm(firstName, lastName, username, password);

        try {
            UserService.editProfile(userEditProfileForm);

            logger.info("Profile successfully updated for user with id: {}", UserService.getLoggedInUser().getId());

            DialogService.information("Account updated", "You have successfully updated your account.");

            SceneManager.openManageAccountScreen();
        } catch (UserValidationException e) {
            logger.warn("Profile update failed for user with id: {} - {}",
                    UserService.getLoggedInUser().getId(), e.getMessage());
            DialogService.error("Account not updated",  e.getMessage());
        }
    }

    /**
     * Called when the user clicks the delete account button.
     * Asks for confirmation before permanently deleting the account.
     * Navigates to the Welcome screen if the account is successfully deleted.
     */
    public void deleteAccount() {
        if (DialogService.confirmation("Delete account",
                "Are you sure you want to delete your account? This action cannot be undone.")) {
            logger.info("User with id: {} confirmed account deletion.", UserService.getLoggedInUser().getId());

            UserService.deleteUserAccount();

            DialogService.information("Account deleted", "You have successfully deleted your account.");

            SceneManager.openWelcomeScreen();
        } else {
            logger.debug("User with id: {} cancelled account deletion.", UserService.getLoggedInUser().getId());
        }
    }

    /**
     * Navigates to the Change Password screen.
     */
    public void changePassword() {
        SceneManager.openChangePasswordScreen();
    }
}
