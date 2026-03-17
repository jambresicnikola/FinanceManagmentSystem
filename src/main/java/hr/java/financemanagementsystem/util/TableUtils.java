package hr.java.financemanagementsystem.util;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.util.function.Consumer;

/**
 * Utility class for building reusable TableView components.
 */
public class TableUtils {
    private TableUtils() {}

    /**
     * Creates a table column cell factory that renders a button in each row.
     * When the button is clicked, the action is called with the row's data object.
     * @param <T> the type of the table row object
     * @param buttonText the label to display on the button
     * @param action the action to run when the button is clicked
     * @return a cell factory that can be passed to {@link TableColumn#setCellFactory}
     */
    public static <T> Callback<TableColumn<T, Void>, TableCell<T, Void>> createButtonColumn(String buttonText, Consumer<T> action) {
        return col -> new TableCell<>() {
            private final Button button = new Button(buttonText);

            {
                button.setOnAction(event -> {
                    T item = getTableView().getItems().get(getIndex());

                    action.accept(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                }
            }
        };
    }
}
