package hr.java.financemanagementsystem.app;

import hr.java.financemanagementsystem.util.ScreenManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class FinanceManagementSystemApplication extends Application {
    @Override
    public void start(Stage stage) {
        ScreenManager.setPrimaryStage(stage);
        ScreenManager.openWelcomeScreen();
    }
}
