package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.exception.PasswordChangeException;
import hr.java.financemanagementsystem.service.DialogService;
import hr.java.financemanagementsystem.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;

public class ChangePasswordScreenController {
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmNewPasswordField;
    @FXML
    private PasswordField currentPasswordField;

    public void confirmChanges() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmNewPassword = confirmNewPasswordField.getText();

        try {
            UserService.changePassword(currentPassword, newPassword, confirmNewPassword);
        } catch (PasswordChangeException e) {
            DialogService.error("Password not changed", e.getMessage());
        }
    }
}
