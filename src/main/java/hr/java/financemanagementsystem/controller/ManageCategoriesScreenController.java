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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Controller for the Manage Categories screen.
 * Shows all categories in a table and allows the user to filter, edit or delete them.
 */
public class ManageCategoriesScreenController {
    private static final Logger logger = LoggerFactory.getLogger(ManageCategoriesScreenController.class);

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

    /**
     * Sets up the table columns and loads all categories when the screen opens.
     */
    public void initialize() {
        setupTableColumns();
        loadAllCategories();
    }

    /**
     * Called when the user types in the filter field and clicks search.
     * Filters the table by category name, or reloads all categories if the field is empty.
     */
    public void filterCategories() {
        String categoryName = categoryNameTextField.getText().trim();
        logger.debug("Filtering categories by name: '{}'", categoryName);

        if (categoryName.isEmpty()) {
            loadAllCategories();
            return;
        }

        Optional<Category> category = CategoryDatabaseRepository.getInstance().findCategoryByName(categoryName, UserService.getLoggedInUser());

        if (category.isPresent()) {
            categoriesTableView.setItems(FXCollections.observableArrayList(category.get()));
            logger.debug("Category filter found a match for: '{}'", categoryName);
        } else {
            logger.debug("No category found with name: '{}'", categoryName);
            DialogService.error("Category not found", "No category found with the name '" + categoryName + "'.");
        }
    }

    /**
     * Configures cell value factories and button columns for the categories table.
     */
    private void setupTableColumns() {
        categoryNameTableColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getName())
        );

        categoryEditTableColumn.setCellFactory(TableUtils.createButtonColumn("Edit", (Category category) -> {
            logger.debug("Edit requested for category: '{}'", category.getName());
            CategoryService.setCategoryToManage(category);
            SceneManager.openEditCategoryScreen();
        }));

        categoryDeleteTableColumn.setCellFactory(TableUtils.createButtonColumn("Delete", (Category category) -> {
            if (DialogService.confirmation("Delete Category", "Are you sure you want to delete '" + category.getName() + "'?" +
                    "\nAll transactions linked to this category will also be deleted.")) {
                logger.info("Deleting category: '{}' and its transactions.", category.getName());
                TransactionService.deleteTransactionsByCategory(category);
                CategoryService.deleteCategory(category);
                categoriesTableView.getItems().remove(category);
            }
        }));
    }

    /**
     * Loads all categories from the database and displays them in the table.
     */
    private void loadAllCategories() {
        categoriesTableView.setItems(FXCollections.observableArrayList(
                CategoryDatabaseRepository.getInstance().findAll()));
    }
}