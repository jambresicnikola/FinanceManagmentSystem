package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.dto.UserRegistrationForm;
import hr.java.financemanagementsystem.exception.UserValidationException;
import hr.java.financemanagementsystem.service.DialogService;
import hr.java.financemanagementsystem.service.UserService;
import hr.java.financemanagementsystem.util.ScreenManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignUpScreenController {
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;

    public void signUp() {
        UserRegistrationForm userRegistrationForm = new UserRegistrationForm(
                firstNameTextField.getText(),
                lastNameTextField.getText(),
                usernameTextField.getText(),
                passwordField.getText(),
                confirmPasswordField.getText()
        );

        try {
            UserService.register(userRegistrationForm);
        } catch (UserValidationException e) {
            DialogService.error("Account not created", e.getMessage());
        }
    }

    public void back() {
        ScreenManager.openWelcomeScreen();
    }
}