package hr.java.financemanagementsystem.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import hr.java.financemanagementsystem.database.UserDatabaseRepository;
import hr.java.financemanagementsystem.dto.UserEditProfileForm;
import hr.java.financemanagementsystem.dto.UserRegistrationForm;
import hr.java.financemanagementsystem.exception.PasswordChangeException;
import hr.java.financemanagementsystem.exception.UserValidationException;
import hr.java.financemanagementsystem.model.User;
import hr.java.financemanagementsystem.util.ScreenManager;
import hr.java.financemanagementsystem.validation.UserValidator;

import java.util.Optional;

public class UserService {
    private UserService() {
    }

    private static User loggedInUser;

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User loggedInUser) {
        UserService.loggedInUser = loggedInUser;
    }

    private static final UserDatabaseRepository userDatabaseRepository = new UserDatabaseRepository();

    public static void register(UserRegistrationForm userRegistrationForm) throws UserValidationException {
        UserValidator.validateUserRegistration(userRegistrationForm);

        if (userDatabaseRepository.findUserByUsername(userRegistrationForm.getUsername()).isPresent()) {
            throw new UserValidationException("\nUsername already exists.");
        }

        String hashedPassword = BCrypt.withDefaults().hashToString(12, userRegistrationForm.getPassword().toCharArray());

        User user = new User.Builder().
                withFirstName(userRegistrationForm.getFirstName())
                .withLastName(userRegistrationForm.getLastName())
                .withUsername(userRegistrationForm.getUsername())
                .withPassword(hashedPassword)
                .build();

        userDatabaseRepository.save(user);

        DialogService.information("Account created", "You have successfully registered.");

        ScreenManager.openWelcomeScreen();
    }

    public static void signIn(String username, String password) throws UserValidationException {
        UserValidator.validateUserSignIn(username, password);
        Optional<User> user = userDatabaseRepository.findUserByUsername(username);

        if (user.isEmpty()) {
            throw new UserValidationException("\nUser with that username does not exists.");
        } else {
            BCrypt.Result validPassword = BCrypt.verifyer().verify(password.toCharArray(), user.get().getPassword());

            if (!validPassword.verified) {
                throw new UserValidationException("\nIncorrect password.");
            }
        }

        setLoggedInUser(user.get());

        ScreenManager.openHomeScreen();
    }

    public static void editProfile(UserEditProfileForm userEditProfileForm) throws UserValidationException {
        UserValidator.validateUserEditProfile(userEditProfileForm);

        if (userDatabaseRepository.findUserByUsername(userEditProfileForm.getUsername()).isPresent() && !userEditProfileForm.getUsername().equals(loggedInUser.getUsername())) {
            throw new UserValidationException("\nUsername already exists.");
        }

        if (userEditProfileForm.getPassword().isEmpty()) {
            throw new UserValidationException("\nPlease confirm changes with your password.");
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
        userDatabaseRepository.update(loggedInUser);

        DialogService.information("Account updated", "You have successfully updated your account.");

        ScreenManager.openManageAccountScreen();
    }

    public static void changePassword(String currentPassword, String newPassword, String confirmNewPassword)
            throws PasswordChangeException {
        UserValidator.validateUserChangePassword(currentPassword, newPassword, confirmNewPassword);

        BCrypt.Result validPassword = BCrypt.verifyer().verify(currentPassword.toCharArray(), loggedInUser.getPassword());

        if (!validPassword.verified) {
            throw new PasswordChangeException("\nIncorrect password.");
        }

        String hashedNewPassword = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray());

        loggedInUser.setPassword(hashedNewPassword);
        userDatabaseRepository.update(loggedInUser);

        DialogService.information("Password changed", "You have successfully changed your password.");

        ScreenManager.getPasswordStage().close();
    }
}