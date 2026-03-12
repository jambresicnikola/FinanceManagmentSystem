package hr.java.financemanagementsystem.app;

import hr.java.financemanagementsystem.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class FinanceManagementSystemApplication extends Application {
    @Override
    public void start(Stage stage) {
        SceneManager.setPrimaryStage(stage);
        SceneManager.openWelcomeScreen();
    }
}
