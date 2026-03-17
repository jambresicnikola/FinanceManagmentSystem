package hr.java.financemanagementsystem.controller;

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
 * Controller for the Welcome screen.
 * Handles user sign in and navigation to the Sign-Up screen.
 */
public class WelcomeScreenController {
    private static final Logger logger = LoggerFactory.getLogger(WelcomeScreenController.class);

    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;

    /**
     * Called when the user clicks the sign-in button.
     * Attempts to sign in with the provided credentials.
     * Shows an error dialog if the credentials are invalid.
     */
    public void signIn() {
        String username = usernameTextField.getText();
        String password = passwordField.getText();

        try {
            UserService.signIn(username, password);

            logger.info("User '{}' successfully signed in.", username);

            SceneManager.openHomeScreen();
        } catch (UserValidationException e) {
            logger.warn("Sign in failed for username: '{}' - {}", username, e.getMessage());
            DialogService.error("Sign in failed", e.getMessage());
        }
    }

    /** Navigates to the Sign Up screen. */
    public void signUp() {
        SceneManager.openSignUpScreen();
    }
}
