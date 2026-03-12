package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.database.CategoryDatabaseRepository;
import hr.java.financemanagementsystem.model.Category;
import hr.java.financemanagementsystem.service.CategoryService;
import hr.java.financemanagementsystem.service.DialogService;
import hr.java.financemanagementsystem.service.TransactionService;
import hr.java.financemanagementsystem.service.UserService;
import hr.java.financemanagementsystem.util.SceneManager;
import hr.java.financemanagementsystem.util.TableUtils;
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

        categoryEditTableColumn.setCellFactory(TableUtils.createButtonColumn("Edit", (Category category) -> {
            CategoryService.setCategoryToManage(category);
            SceneManager.openEditCategoryScreen();
        }));

        categoryDeleteTableColumn.setCellFactory(TableUtils.createButtonColumn("Delete", (Category category) -> {
            if (DialogService.confirmation("Delete Category", "Are you sure you want to delete this category?" +
                    "\nNOTE: You will delete all transactions from that category!")) {
                TransactionService.deleteTransactionsByCategory(category);
                CategoryService.deleteCategory(category);
                categoriesTableView.getItems().remove(category);
            }
        }));

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