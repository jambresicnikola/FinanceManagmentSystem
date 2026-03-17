package hr.java.financemanagementsystem.validation;

import hr.java.financemanagementsystem.exception.CategoryValidationException;
import hr.java.financemanagementsystem.model.Category;

/**
 * Validates {@link Category} data before saving or updating.
 */
public class CategoryValidator {
    private  CategoryValidator() {}

    /**
     * Validates a category's fields.
     * @param category the category to validate
     * @throws CategoryValidationException if the category name is empty
     */
    public static void validateCategory(Category category) {
        if (category.getName() == null || category.getName().isBlank()) {
            throw new CategoryValidationException("\nCategory name cannot be empty.");
        }
    }
}
