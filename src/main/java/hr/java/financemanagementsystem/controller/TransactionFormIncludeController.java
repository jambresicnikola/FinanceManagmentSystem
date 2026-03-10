package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.database.CategoryDatabaseRepository;
import hr.java.financemanagementsystem.model.Category;
import hr.java.financemanagementsystem.model.Transaction;
import hr.java.financemanagementsystem.model.TransactionType;
import hr.java.financemanagementsystem.service.UserService;
import hr.java.financemanagementsystem.validation.NumberValidator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionFormIncludeController {
    @FXML
    protected TextField transactionDescriptionTextField;
    @FXML
    protected Spinner<Integer> transactionAmountSpinner;
    @FXML
    protected TextField transactionPriceTextField;
    @FXML
    protected ComboBox<String> transactionCategoryComboBox;
    @FXML
    protected ComboBox<TransactionType> transactionTypeComboBox;
    @FXML
    protected DatePicker transactionDatePicker;

    protected void initializeForm() {
        transactionAmountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100));

        transactionCategoryComboBox.setItems(
                FXCollections.observableArrayList(
                        CategoryDatabaseRepository.getInstance().findAll().stream()
                                .map(Category::getName)
                                .toList()));

        transactionTypeComboBox.setItems(FXCollections.observableArrayList(TransactionType.values()));
    }

    protected Transaction buildTransactionFromForm() {
        String description = transactionDescriptionTextField.getText();
        Integer amount = transactionAmountSpinner.getValue();

        String priceString = transactionPriceTextField.getText().trim();
        BigDecimal price = NumberValidator.validatePositiveBigDecimal(priceString);

        String categoryString = transactionCategoryComboBox.getSelectionModel().getSelectedItem();
        Category category = CategoryDatabaseRepository.getInstance()
                .findCategoryByName(categoryString, UserService.getLoggedInUser()).orElse(null);

        LocalDate date = transactionDatePicker.getValue();
        TransactionType transactionType = transactionTypeComboBox.getSelectionModel().getSelectedItem();

        return new Transaction.Builder()
                .withDescription(description)
                .withAmount(amount)
                .withPrice(price)
                .withCategory(category)
                .withDate(date)
                .withTransactionType(transactionType)
                .withUser(UserService.getLoggedInUser())
                .build();
    }
}
