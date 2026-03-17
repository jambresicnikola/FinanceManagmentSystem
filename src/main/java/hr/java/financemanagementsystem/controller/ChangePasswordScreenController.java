package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.exception.PasswordChangeException;
import hr.java.financemanagementsystem.service.DialogService;
import hr.java.financemanagementsystem.service.UserService;
import hr.java.financemanagementsystem.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the Change Password screen.
 * Works together with {@link UserService} to validate and update the user's password.
 */
public class ChangePasswordScreenController {
    private static final Logger logger = LoggerFactory.getLogger(ChangePasswordScreenController.class);

    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmNewPasswordField;
    @FXML
    private PasswordField currentPasswordField;

    /**
     * Called when the user clicks the confirm button.
     * Reads all three password fields and attempts to change the password.
     * Shows an error dialog if the current password is wrong or the new passwords don't match.
     */
    public void confirmChanges() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmNewPassword = confirmNewPasswordField.getText();

        logger.debug("User with id: {} is attempting to change their password.",
                UserService.getLoggedInUser().getId());

        try {
            UserService.changePassword(currentPassword, newPassword, confirmNewPassword);

            logger.info("Password successfully changed for user with id: {}",
                    UserService.getLoggedInUser().getId());

            DialogService.information("Password changed", "You have successfully changed your password.");

            SceneManager.getPasswordStage().close();
        } catch (PasswordChangeException e) {
            logger.warn("Password change failed for user with id: {} - {}",
                    UserService.getLoggedInUser().getId(), e.getMessage());

            DialogService.error("Password not changed", e.getMessage());
        }
    }
}
