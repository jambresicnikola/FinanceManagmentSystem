package hr.java.financemanagementsystem.app;

import hr.java.financemanagementsystem.service.DialogService;
import hr.java.financemanagementsystem.util.SceneManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the Finance Management System application.
 */
public class FinanceManagementSystemApplication extends Application {
    private static final Logger logger = LoggerFactory.getLogger(FinanceManagementSystemApplication.class);

    @Override
    public void start(Stage stage) {
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            logger.error("Unexpected error on thread: {}", thread.getName(), throwable);

            Platform.runLater(() ->
                    DialogService.error("Unexpected error",
                            "Something went wrong. Please restart the app."));
        });

        SceneManager.setPrimaryStage(stage);
        SceneManager.openWelcomeScreen();
    }
}
