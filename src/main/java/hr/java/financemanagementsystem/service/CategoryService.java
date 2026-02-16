package hr.java.financemanagementsystem.service;

import hr.java.financemanagementsystem.database.CategoryDatabaseRepository;
import hr.java.financemanagementsystem.exception.CategoryValidationException;
import hr.java.financemanagementsystem.model.Category;
import hr.java.financemanagementsystem.model.User;

public class CategoryService {
    private CategoryService() {}

    public static void createNewCategory(String categoryName, User user) throws CategoryValidationException {
        if (categoryName.isEmpty()) {
            throw new CategoryValidationException("\nCategory name cannot be empty.");
        }

        if (CategoryDatabaseRepository.getInstance().findCategoryByName(categoryName, user).isPresent()) {
            throw new CategoryValidationException("\nCategory already exists.");
        }

        Category category = new Category.Builder()
                .withName(categoryName)
                .withUser(user)
                .build();

        CategoryDatabaseRepository.getInstance().save(category);

        DialogService.information("Category created", "Category has been successfully created.");
    }
}
