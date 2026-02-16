package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.exception.UserValidationException;
import hr.java.financemanagementsystem.service.DialogService;
import hr.java.financemanagementsystem.service.UserService;
import hr.java.financemanagementsystem.util.ScreenManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class WelcomeScreenController {
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;

    public void signIn() {
        String username = usernameTextField.getText();
        String password = passwordField.getText();

        try {
            UserService.signIn(username, password);
        } catch (UserValidationException e) {
            DialogService.error("Sign in failed", e.getMessage());
        }
    }

    public void signUp() {
        ScreenManager.openSignUpScreen();
    }
}
