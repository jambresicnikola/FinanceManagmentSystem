package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.exception.CategoryValidationException;
import hr.java.financemanagementsystem.model.Category;
import hr.java.financemanagementsystem.service.CategoryService;
import hr.java.financemanagementsystem.service.DialogService;
import hr.java.financemanagementsystem.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the Add Category screen.
 * Works together with {@link CategoryService} to save new categories.
 */
public class AddCategoryScreenController {
    private static final Logger logger = LoggerFactory.getLogger(AddCategoryScreenController.class);

    @FXML
    private TextField categoryNameTextField;

    /**
     * Called when the user clicks the add category button.
     * Reads the category name from the input field, validates it and saves it.
     * Shows an error dialog if something is wrong with the input.
     */
    public void addNewCategory() {
        String categoryName = categoryNameTextField.getText();
        logger.debug("Attempting to create new category with name: '{}'", categoryName);

        Category category = new Category.Builder()
                .withName(categoryName)
                .withUser(UserService.getLoggedInUser())
                .build();

        try {
            CategoryService.createNewCategory(category);

            logger.info("Category '{}' successfully created for user with id: {}",
                    categoryName, UserService.getLoggedInUser().getId());

            DialogService.information("Category created",
                    "Category '" + categoryName + "' has been successfully created.");
        } catch (CategoryValidationException e) {
            DialogService.error("Category not created", e.getMessage());
            logger.warn("Category creation failed: {}", e.getMessage());
        }
    }
}
