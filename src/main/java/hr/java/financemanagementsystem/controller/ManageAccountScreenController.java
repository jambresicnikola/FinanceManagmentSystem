package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.dto.UserEditProfileForm;
import hr.java.financemanagementsystem.exception.UserValidationException;
import hr.java.financemanagementsystem.service.DialogService;
import hr.java.financemanagementsystem.service.UserService;
import hr.java.financemanagementsystem.util.ScreenManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ManageAccountScreenController {
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;

    public void initialize() {
        firstNameTextField.setText(UserService.getLoggedInUser().getFirstName());
        lastNameTextField.setText(UserService.getLoggedInUser().getLastName());
        usernameTextField.setText(UserService.getLoggedInUser().getUsername());
    }

    public void saveChanges() {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String username = usernameTextField.getText();
        String password = passwordField.getText();
        UserEditProfileForm userEditProfileForm = new UserEditProfileForm(firstName, lastName, username, password);

        try {
            UserService.editProfile(userEditProfileForm);
        } catch (UserValidationException e) {
            DialogService.error("Account not updated",  e.getMessage());
        }
    }

    public void deleteAccount() {
        UserService.deleteUserAccount();
    }

    public void changePassword() {
        ScreenManager.openChangePasswordScreen();
    }
}
