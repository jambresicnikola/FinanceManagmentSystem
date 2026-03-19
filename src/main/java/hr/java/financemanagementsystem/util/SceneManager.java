package hr.java.financemanagementsystem.util;

import hr.java.financemanagementsystem.app.FinanceManagementSystemApplication;
import hr.java.financemanagementsystem.exception.ScreenLoadingException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Utility class for navigating between screens in the app.
 * All screen transitions go through this class.
 */
public class SceneManager {
    private static final Logger logger = LoggerFactory.getLogger(SceneManager.class);

    private SceneManager() {
    }

    private static final String APP_TITLE = "Finance Management System";

    private static Stage primaryStage;
    private static Stage passwordStage;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        SceneManager.primaryStage = primaryStage;
    }

    public static Stage getPasswordStage() {
        return passwordStage;
    }

    public static void setPasswordStage(Stage passwordStage) {
        SceneManager.passwordStage = passwordStage;
    }

    /**
     * Loads an FXML file and sets it as the current scene on the primary stage.
     * @param title the window title to set
     * @param fxml the path to the FXML file
     * @throws ScreenLoadingException if the FXML file cannot be loaded
     */
    private static void openScreen(String title, String fxml) {
        logger.debug("Navigating to screen: {}", fxml);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    FinanceManagementSystemApplication.class.getResource(fxml));
            Scene scene = new Scene(fxmlLoader.load(), 1366, 768);
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new ScreenLoadingException(e);
        }
    }

    /** Navigates to the Welcome screen. */
    public static void openWelcomeScreen() {
        openScreen(APP_TITLE, "/hr/java/financemanagementsystem/welcomeScreen.fxml");
    }

    /** Navigates to the Sign Up screen. */
    public static void openSignUpScreen() {
        openScreen(APP_TITLE, "/hr/java/financemanagementsystem/signUpScreen.fxml");
    }

    /** Navigates to the Home screen. */
    public static void openHomeScreen() {
        openScreen(APP_TITLE, "/hr/java/financemanagementsystem/homeScreen.fxml");
    }

    /** Navigates to the Add Category screen. */
    public static void openAddCategoryScreen() {
        openScreen(APP_TITLE, "/hr/java/financemanagementsystem/addCategoryScreen.fxml");
    }

    /** Navigates to the Manage Account screen. */
    public static void openManageAccountScreen() {
        openScreen(APP_TITLE, "/hr/java/financemanagementsystem/manageAccountScreen.fxml");
    }

    /**
     * Opens the Change Password screen in a new window.
     * @throws ScreenLoadingException if the FXML file cannot be loaded
     */
    public static void openChangePasswordScreen() {
        logger.debug("Opening Change Password screen in a new window.");
        FXMLLoader fxmlLoader = new FXMLLoader(
                FinanceManagementSystemApplication.class.getResource("/hr/java/financemanagementsystem/changePasswordScreen.fxml"));

        try {
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = new Stage();
            stage.setTitle("Change Password");
            stage.setScene(scene);
            stage.show();
            setPasswordStage(stage);
        } catch (IOException e) {
            throw new ScreenLoadingException(e);
        }
    }

    /** Navigates to the Manage Categories screen. */
    public static void openManageCategoriesScreen() {
        openScreen(APP_TITLE, "/hr/java/financemanagementsystem/manageCategoriesScreen.fxml");
    }

    /** Navigates to the Edit Category screen. */
    public static void openEditCategoryScreen() {
        openScreen(APP_TITLE, "/hr/java/financemanagementsystem/editCategoryScreen.fxml");
    }

    /** Navigates to the Add Transaction screen. */
    public static void openAddTransactionScreen() {
        openScreen(APP_TITLE, "/hr/java/financemanagementsystem/addTransactionScreen.fxml");
    }

    /** Navigates to the Manage Transactions screen. */
    public static void openManageTransactionsScreen() {
        openScreen(APP_TITLE, "/hr/java/financemanagementsystem/manageTransactionsScreen.fxml");
    }

    /** Navigates to the Edit Transaction screen. */
    public static void openEditTransactionsScreen() {
        openScreen(APP_TITLE, "/hr/java/financemanagementsystem/editTransactionScreen.fxml");
    }

    /** Navigates to the Charts screen. */
    public static void openChartsScreen() {
        openScreen(APP_TITLE, "/hr/java/financemanagementsystem/chartsScreen.fxml");
    }
}
