package hr.java.financemanagementsystem.service;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

import java.util.Optional;

/**
 * Utility class for showing dialog boxes throughout the app.
 */
public class DialogService {
    private DialogService() {}


    /**
     * Builds and displays an alert dialog with a scrollable text area for the message.
     * @param title the header text of the dialog
     * @param message the message to display in the text area
     * @param alert the alert instance to configure and show
     */
    private static void showAlert(String title, String message, Alert alert) {
        alert.setTitle("Finance Management System");
        alert.setHeaderText(title);

        TextArea textArea = new TextArea(message);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(10);

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    /**
     * Shows an error dialog.
     * @param title the header text of the dialog
     * @param message the message to display
     */
    public static void error(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        showAlert(title, message, alert);
    }

    /**
     * Shows an information dialog.
     * @param title the header text of the dialog
     * @param message the message to display
     */
    public static void information(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        showAlert(title, message, alert);
    }

    /**
     * Shows a confirmation dialog and returns the user's choice.
     * @param title the header text of the dialog
     * @param message the message to display
     * @return true if the user clicked OK, false otherwise
     */
    public static boolean confirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Finance Management System");
        alert.setHeaderText(title);

        TextArea textArea = new TextArea(message);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(10);

        alert.getDialogPane().setContent(textArea);

        Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
