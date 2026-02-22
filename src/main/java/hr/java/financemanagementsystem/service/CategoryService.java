package hr.java.financemanagementsystem.service;

import hr.java.financemanagementsystem.database.CategoryDatabaseRepository;
import hr.java.financemanagementsystem.exception.CategoryValidationException;
import hr.java.financemanagementsystem.model.Category;
import hr.java.financemanagementsystem.util.ScreenManager;
import hr.java.financemanagementsystem.validation.CategoryValidator;

public class CategoryService {
    private CategoryService() {}

    private static Category categoryToManage;

    public static Category getCategoryToManage() {
        return categoryToManage;
    }

    public static void setCategoryToManage(Category categoryToManage) {
        CategoryService.categoryToManage = categoryToManage;
    }

    public static void createNewCategory(Category category) throws CategoryValidationException {
        CategoryValidator.validateCategory(category);

        checkIfCategoryExists(category);

        CategoryDatabaseRepository.getInstance().save(category);

        DialogService.information("Category created", "Category has been successfully created.");
    }

    public static void editCategory() throws CategoryValidationException {
        CategoryValidator.validateCategory(categoryToManage);

        checkIfCategoryExists(categoryToManage);

        CategoryDatabaseRepository.getInstance().update(categoryToManage);

        DialogService.information("Changes successful", "Category has been successfully updated.");

        ScreenManager.openManageCategoriesScreen();
    }

    private static void checkIfCategoryExists(Category category) throws CategoryValidationException {
        if (CategoryDatabaseRepository.getInstance().findCategoryByName(category.getName(), UserService.getLoggedInUser()).isPresent()) {
            throw new CategoryValidationException("\nCategory already exists.");
        }
    }

    public static void deleteCategory(Category category) {
        CategoryDatabaseRepository.getInstance().delete(category);
    }
}
