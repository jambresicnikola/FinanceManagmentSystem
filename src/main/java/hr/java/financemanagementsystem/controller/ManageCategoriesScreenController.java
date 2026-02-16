package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.database.CategoryDatabaseRepository;
import hr.java.financemanagementsystem.model.Category;
import hr.java.financemanagementsystem.service.DialogService;
import hr.java.financemanagementsystem.service.UserService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.Optional;

public class ManageCategoriesScreenController {
    @FXML
    private TableView<Category> categoriesTableView;

    @FXML
    private TableColumn<String, String> categoryDeleteTableColumn;

    @FXML
    private TableColumn<String, String> categoryEditTableColumn;

    @FXML
    private TableColumn<Category, String> categoryNameTableColumn;

    @FXML
    private TextField categoryNameTextField;

    public void initialize() {
        categoryNameTableColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getName())
        );



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
