package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.exception.CategoryValidationException;
import hr.java.financemanagementsystem.service.CategoryService;
import hr.java.financemanagementsystem.service.DialogService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class EditCategoryScreenController {
    @FXML
    private TextField categoryNameTextField;

    public void initialize() {
        categoryNameTextField.setText(CategoryService.getCategoryToManage().getName());
    }

    public void confirmCategoryEdit() {
        String categoryName = categoryNameTextField.getText();

        CategoryService.getCategoryToManage().setName(categoryName);

        try {
            CategoryService.editCategory();
        } catch (CategoryValidationException e) {
            DialogService.error("Changes not saved", e.getMessage());
        }
    }
}
