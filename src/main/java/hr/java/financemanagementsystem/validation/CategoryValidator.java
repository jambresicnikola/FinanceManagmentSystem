package hr.java.financemanagementsystem.validation;

import hr.java.financemanagementsystem.exception.CategoryValidationException;
import hr.java.financemanagementsystem.model.Category;

public class CategoryValidator {
    private  CategoryValidator() {}

    public static void validateCategory(Category category) throws CategoryValidationException {
        if (category.getName().isEmpty()) {
            throw new CategoryValidationException("\nCategory name cannot be empty.");
        }
    }
}
