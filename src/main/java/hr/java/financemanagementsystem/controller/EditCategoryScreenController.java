package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.exception.CategoryValidationException;
import hr.java.financemanagementsystem.service.CategoryService;
import hr.java.financemanagementsystem.service.DialogService;
import hr.java.financemanagementsystem.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the Edit Category screen.
 * Loads the selected category into the form and saves changes on confirmation.
 */
public class EditCategoryScreenController {
    private static final Logger logger = LoggerFactory.getLogger(EditCategoryScreenController.class);

    @FXML
    private TextField categoryNameTextField;

    /**
     * Pre-fills the form with the current category name when the screen loads.
     */
    public void initialize() {
        categoryNameTextField.setText(CategoryService.getCategoryToManage().getName());
        logger.debug("Edit category screen loaded for category: '{}'",
                CategoryService.getCategoryToManage().getName());
    }

    /**
     * Called when the user clicks the confirm button.
     * Saves the updated category name and goes back to the Manage Categories screen.
     * Shows an error dialog if the new name is invalid.
     */
    public void confirmCategoryEdit() {
        String categoryName = categoryNameTextField.getText();
        logger.debug("Attempting to update category to name: '{}'", categoryName);

        CategoryService.getCategoryToManage().setName(categoryName);

        try {
            CategoryService.editCategory();

            logger.info("Category successfully updated to: '{}'", categoryName);

            DialogService.information("Changes saved",
                    "Category has been successfully updated to " + categoryName + ".");

            SceneManager.openManageCategoriesScreen();
        } catch (CategoryValidationException e) {
            logger.warn("Category update failed for name: '{}' - {}", categoryName, e.getMessage());
            DialogService.error("Changes not saved", e.getMessage());
        }
    }
}
