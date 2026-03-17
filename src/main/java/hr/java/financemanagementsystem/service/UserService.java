package hr.java.financemanagementsystem.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import hr.java.financemanagementsystem.database.CategoryDatabaseRepository;
import hr.java.financemanagementsystem.database.TransactionDatabaseRepository;
import hr.java.financemanagementsystem.database.UserDatabaseRepository;
import hr.java.financemanagementsystem.dto.UserEditProfileForm;
import hr.java.financemanagementsystem.dto.UserRegistrationForm;
import hr.java.financemanagementsystem.exception.PasswordChangeException;
import hr.java.financemanagementsystem.exception.UserValidationException;
import hr.java.financemanagementsystem.model.User;
import hr.java.financemanagementsystem.validation.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Handles business logic for user management including registration,
 * sign in, profile editing, password changes and account deletion.
 */
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private UserService() {
    }

    private static User loggedInUser;

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User loggedInUser) {
        UserService.loggedInUser = loggedInUser;
    }

    /**
     * Validates and registers a new user.
     * Hashes the password before saving.
     * @param userRegistrationForm the registration form data
     * @throws UserValidationException if validation fails or the username is already taken
     */
    public static void register(UserRegistrationForm userRegistrationForm) {
        logger.debug("Attempting to register new user with username: '{}'", userRegistrationForm.getUsername());
        UserValidator.validateUserRegistration(userRegistrationForm);

        if (UserDatabaseRepository.getInstance().findUserByUsername(userRegistrationForm.getUsername()).isPresent()) {
            throw new UserValidationException("Username '" + userRegistrationForm.getUsername() + "' is already taken.");
        }

        String hashedPassword = BCrypt.withDefaults().hashToString(12, userRegistrationForm.getPassword().toCharArray());

        User user = new User.Builder().
                withFirstName(userRegistrationForm.getFirstName())
                .withLastName(userRegistrationForm.getLastName())
                .withUsername(userRegistrationForm.getUsername())
                .withPassword(hashedPassword)
                .build();

        UserDatabaseRepository.getInstance().save(user);
        logger.info("User '{}' successfully registered.", userRegistrationForm.getUsername());
    }

    /**
     * Signs in a user with the given credentials.
     * Sets the logged in user on success.
     * @param username the username to sign in with
     * @param password the password to verify
     * @throws UserValidationException if credentials are invalid or the user does not exist
     */
    public static void signIn(String username, String password) {
        logger.debug("Sign in attempt for username: '{}'", username);
        UserValidator.validateUserSignIn(username, password);
        Optional<User> user = UserDatabaseRepository.getInstance().findUserByUsername(username);

        if (user.isEmpty()) {
            throw new UserValidationException("No account found with that username.");
        } else {
            BCrypt.Result validPassword = BCrypt.verifyer().verify(password.toCharArray(), user.get().getPassword());

            if (!validPassword.verified) {
                throw new UserValidationException("\nIncorrect password.");
            }
        }

        setLoggedInUser(user.get());
        logger.info("User '{}' successfully signed in.", username);
    }

    /**
     * Validates and updates the logged in user's profile.
     * Requires the current password to confirm changes.
     * @param userEditProfileForm the updated profile data
     * @throws UserValidationException if validation fails, the username is taken, or the password is incorrect
     */
    public static void editProfile(UserEditProfileForm userEditProfileForm) {
        logger.debug("Attempting to update profile for user with id: {}", loggedInUser.getId());
        UserValidator.validateUserEditProfile(userEditProfileForm);

        if (UserDatabaseRepository.getInstance().findUserByUsername(userEditProfileForm.getUsername()).isPresent()
                && !userEditProfileForm.getUsername().equals(loggedInUser.getUsername())) {
            throw new UserValidationException("Username '" + userEditProfileForm.getUsername() + "' is already taken.");
        }

        if (userEditProfileForm.getPassword().isEmpty()) {
            throw new UserValidationException("Please confirm your changes by entering your current password.");
        } else {
            BCrypt.Result validPassword = BCrypt.verifyer().verify(userEditProfileForm.getPassword().toCharArray(), loggedInUser.getPassword());

            if (!validPassword.verified) {
                throw new UserValidationException("\nIncorrect password.");
            }
        }

        User updatedLoggedInUser = new User.Builder()
                .withId(loggedInUser.getId())
                .withFirstName(userEditProfileForm.getFirstName())
                .withLastName(userEditProfileForm.getLastName())
                .withUsername(userEditProfileForm.getUsername())
                .withPassword(loggedInUser.getPassword())
                .build();

        setLoggedInUser(updatedLoggedInUser);
        UserDatabaseRepository.getInstance().update(loggedInUser);
        logger.info("Profile successfully updated for user with id: {}", loggedInUser.getId());
    }

    /**
     * Changes the logged in user's password.
     * Verifies the current password before applying the change.
     * @param currentPassword the user's current password
     * @param newPassword the new password to set
     * @param confirmNewPassword must match the new password
     * @throws PasswordChangeException if validation fails or the current password is incorrect
     */
    public static void changePassword(String currentPassword, String newPassword, String confirmNewPassword)
            throws PasswordChangeException {
        logger.debug("Attempting to change password for user with id: {}", loggedInUser.getId());

        UserValidator.validateUserChangePassword(currentPassword, newPassword, confirmNewPassword);

        BCrypt.Result validPassword = BCrypt.verifyer().verify(currentPassword.toCharArray(), loggedInUser.getPassword());

        if (!validPassword.verified) {
            throw new PasswordChangeException("\nIncorrect password.");
        }

        String hashedNewPassword = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray());

        loggedInUser.setPassword(hashedNewPassword);
        UserDatabaseRepository.getInstance().update(loggedInUser);
        logger.info("Password successfully changed for user with id: {}", loggedInUser.getId());
    }

    /**
     * Permanently deletes the logged in user's account.
     * Also deletes all their transactions and categories.
     */
    public static void deleteUserAccount() {
        logger.info("Deleting account for user with id: {}", loggedInUser.getId());
        TransactionDatabaseRepository.getInstance().deleteTransactionsByUser(loggedInUser);
        CategoryDatabaseRepository.getInstance().deleteCategoriesByUser(loggedInUser);
        UserDatabaseRepository.getInstance().delete(loggedInUser);
        logger.info("Account successfully deleted for user with id: {}", loggedInUser.getId());

        loggedInUser = null;
    }
}