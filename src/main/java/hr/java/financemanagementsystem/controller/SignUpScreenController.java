package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.dto.UserRegistrationForm;
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
 * Controller for the Sign-Up screen.
 * Handles new user registration.
 */
public class SignUpScreenController {
    private static final Logger logger = LoggerFactory.getLogger(SignUpScreenController.class);

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

    /**
     * Called when the user clicks the sign-up button.
     * Reads the form fields, validates them and creates a new account.
     * Shows an error dialog if something is wrong with the input.
     */
    public void signUp() {
        String username = usernameTextField.getText();
        logger.debug("Attempting to register new user with username: '{}'", username);

        UserRegistrationForm userRegistrationForm = new UserRegistrationForm(
                firstNameTextField.getText(),
                lastNameTextField.getText(),
                username,
                passwordField.getText(),
                confirmPasswordField.getText()
        );

        try {
            UserService.register(userRegistrationForm);

            logger.info("New user successfully registered with username: '{}'", username);

            DialogService.information("Account created", "Welcome! Your account has been successfully created.");

            SceneManager.openWelcomeScreen();
        } catch (UserValidationException e) {
            logger.warn("Registration failed for username: '{}' - {}", username, e.getMessage());
            DialogService.error("Account not created", e.getMessage());
        }
    }

    /** Navigates back to the Welcome screen. */
    public void back() {
        SceneManager.openWelcomeScreen();
    }
}