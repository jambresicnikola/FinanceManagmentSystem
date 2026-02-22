package hr.java.financemanagementsystem.service;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

import java.util.Optional;

public class DialogService {
    private DialogService() {}

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

    public static void error(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        showAlert(title, message, alert);
    }

    public static void information(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        showAlert(title, message, alert);
    }

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
