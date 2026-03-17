package hr.java.financemanagementsystem.service;

import hr.java.financemanagementsystem.database.CategoryDatabaseRepository;
import hr.java.financemanagementsystem.exception.CategoryValidationException;
import hr.java.financemanagementsystem.model.Category;
import hr.java.financemanagementsystem.validation.CategoryValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles business logic for category management.
 */
public class CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    private CategoryService() {}

    private static Category categoryToManage;

    public static Category getCategoryToManage() {
        return categoryToManage;
    }

    public static void setCategoryToManage(Category categoryToManage) {
        CategoryService.categoryToManage = categoryToManage;
    }

    /**
     * Validates and saves a new category.
     * @param category the category to create
     * @throws CategoryValidationException if the category is invalid or already exists
     */
    public static void createNewCategory(Category category) {
        logger.debug("Creating new category: '{}'", category.getName());

        CategoryValidator.validateCategory(category);
        checkIfCategoryExists(category);

        CategoryDatabaseRepository.getInstance().save(category);
        logger.info("Category '{}' created successfully.", category.getName());
    }

    /**
     * Validates and updates the currently managed category.
     * @throws CategoryValidationException if the category is invalid or already exists
     */
    public static void editCategory() {
        logger.debug("Editing category with id: {}", categoryToManage.getId());

        CategoryValidator.validateCategory(categoryToManage);
        checkIfCategoryExists(categoryToManage);

        CategoryDatabaseRepository.getInstance().update(categoryToManage);
        logger.info("Category '{}' updated successfully.", categoryToManage.getName());
    }

    /**
     * Checks if a category with the same name already exists for the logged in user.
     * @param category the category to check
     * @throws CategoryValidationException if a category with the same name already exists
     */
    private static void checkIfCategoryExists(Category category) throws CategoryValidationException {
        if (CategoryDatabaseRepository.getInstance().findCategoryByName(category.getName(), UserService.getLoggedInUser()).isPresent()) {
            throw new CategoryValidationException("\nA category with this name already exists.");
        }
    }

    /**
     * Deletes a category from the database.
     * @param category the category to delete
     */
    public static void deleteCategory(Category category) {
        logger.debug("Deleting category: '{}'", category.getName());
        CategoryDatabaseRepository.getInstance().delete(category);
        logger.info("Category '{}' deleted successfully.", category.getName());
    }
}
