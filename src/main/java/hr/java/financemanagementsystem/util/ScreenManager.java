package hr.java.financemanagementsystem.util;

import hr.java.financemanagementsystem.app.FinanceManagementSystemApplication;
import hr.java.financemanagementsystem.exception.ScreenLoadingException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ScreenManager {
    private ScreenManager() {
    }

    private static final String APP_TITLE = "Finance Management System";

    private static Stage primaryStage;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        ScreenManager.primaryStage = primaryStage;
    }

    private static void openScreen(String title, String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(FinanceManagementSystemApplication.class.getResource(fxml));
        Scene scene = new Scene(fxmlLoader.load(), 900, 500);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void openWelcomeScreen() {
        try {
            openScreen(APP_TITLE, "/hr/java/financemanagementsystem/welcomeScreen.fxml");
        } catch (IOException e) {
            throw new ScreenLoadingException(e);
        }
    }

    public static void openSignUpScreen() {
        try {
            openScreen(APP_TITLE, "/hr/java/financemanagementsystem/signUpScreen.fxml");
        } catch (IOException e) {
            throw new ScreenLoadingException(e);
        }
    }

    public static void openHomeScreen() {
        try {
            openScreen(APP_TITLE, "/hr/java/financemanagementsystem/homeScreen.fxml");
        } catch (IOException e) {
            throw new ScreenLoadingException(e);
        }
    }

    public static void openAddCategoryScreen() {
        try {
            openScreen(APP_TITLE, "/hr/java/financemanagementsystem/addCategoryScreen.fxml");
        } catch (IOException e) {
            throw new ScreenLoadingException(e);
        }
    }

    public static void openManageAccountScreen() {
        try {
            openScreen(APP_TITLE, "/hr/java/financemanagementsystem/manageAccountScreen.fxml");
        } catch (IOException e) {
            throw new ScreenLoadingException(e);
        }
    }

    private static Stage passwordStage;

    public static Stage getPasswordStage() {
        return passwordStage;
    }

    public static void setPasswordStage(Stage passwordStage) {
        ScreenManager.passwordStage = passwordStage;
    }

    public static void openChangePasswordScreen() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                FinanceManagementSystemApplication.class.getResource("/hr/java/financemanagementsystem/changePasswordScreen.fxml"));
        Scene scene;

        try {
            scene = new Scene(fxmlLoader.load(), 600, 400);
        } catch (IOException e) {
            throw new ScreenLoadingException(e);
        }

        Stage stage = new Stage();
        stage.setTitle("Change password");
        stage.setScene(scene);
        stage.show();

        setPasswordStage(stage);
    }

    public static void openManageCategoriesScreen() {
        try {
            openScreen(APP_TITLE, "/hr/java/financemanagementsystem/manageCategoriesScreen.fxml");
        } catch (IOException e) {
            throw new ScreenLoadingException(e);
        }
    }

    public static void openEditCategoryScreen() {
        try {
            openScreen(APP_TITLE, "/hr/java/financemanagementsystem/editCategoryScreen.fxml");
        } catch (IOException e) {
            throw new ScreenLoadingException(e);
        }
    }

    public static void openAddTransactionScreen() {
        try {
            openScreen(APP_TITLE, "/hr/java/financemanagementsystem/addTransactionScreen.fxml");
        } catch (IOException e) {
            throw new ScreenLoadingException(e);
        }
    }

    public static void openManageTransactionsScreen() {
        try {
            openScreen(APP_TITLE, "/hr/java/financemanagementsystem/manageTransactionsScreen.fxml");
        } catch (IOException e) {
            throw new ScreenLoadingException(e);
        }
    }
}
