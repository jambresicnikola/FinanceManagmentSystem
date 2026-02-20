package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.database.CategoryDatabaseRepository;
import hr.java.financemanagementsystem.model.Category;
import hr.java.financemanagementsystem.service.CategoryService;
import hr.java.financemanagementsystem.service.DialogService;
import hr.java.financemanagementsystem.service.UserService;
import hr.java.financemanagementsystem.util.ScreenManager;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;

public class ManageCategoriesScreenController {
    @FXML
    private TableView<Category> categoriesTableView;

    @FXML
    private TableColumn<Category, Void> categoryDeleteTableColumn;

    @FXML
    private TableColumn<Category, Void> categoryEditTableColumn;

    @FXML
    private TableColumn<Category, String> categoryNameTableColumn;

    @FXML
    private TextField categoryNameTextField;

    public void initialize() {
        categoryNameTableColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getName())
        );

        categoryEditTableColumn.setCellFactory(column -> new TableCell<Category, Void>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setOnAction(event -> {
                    Category category = getTableView().getItems().get(getIndex());
                    CategoryService.setCategoryToManage(category);

                    ScreenManager.openEditCategoryScreen();
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });

        categoriesTableView.setItems(FXCollections.observableArrayList(CategoryDatabaseRepository.getInstance().findAll()));
    }

    public void filterCategories() {
        String categoryName = categoryNameTextField.getText();

        if (!categoryName.isEmpty()) {
            Optional<Category> category = CategoryDatabaseRepository.getInstance().findCategoryByName(categoryName, UserService.getLoggedInUser());

            if (category.isPresent()) {
                categoriesTableView.setItems(FXCollections.observableArrayList(category.get()));
            } else {
                DialogService.error("Category not found", "Category not found");
            }
        } else {
            categoriesTableView.setItems(FXCollections.observableArrayList(CategoryDatabaseRepository.getInstance().findAll()));
        }
    }
}
