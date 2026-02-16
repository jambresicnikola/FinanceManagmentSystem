package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.exception.CategoryValidationException;
import hr.java.financemanagementsystem.service.CategoryService;
import hr.java.financemanagementsystem.service.DialogService;
import hr.java.financemanagementsystem.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AddCategoryScreenController {
    @FXML
    private TextField categoryNameTextField;

    public void addNewCategory() {
        String categoryName = categoryNameTextField.getText();

        try {
            CategoryService.createNewCategory(categoryName, UserService.getLoggedInUser());
        } catch (CategoryValidationException e) {
            DialogService.error("Category not created", e.getMessage());
        }
    }
}
