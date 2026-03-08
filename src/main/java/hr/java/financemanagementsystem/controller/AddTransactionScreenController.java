package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.database.CategoryDatabaseRepository;
import hr.java.financemanagementsystem.exception.TransactionValidationException;
import hr.java.financemanagementsystem.model.Category;
import hr.java.financemanagementsystem.model.Transaction;
import hr.java.financemanagementsystem.model.TransactionType;
import hr.java.financemanagementsystem.service.DialogService;
import hr.java.financemanagementsystem.service.TransactionService;
import hr.java.financemanagementsystem.service.UserService;
import hr.java.financemanagementsystem.validation.NumberValidator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AddTransactionScreenController {
    @FXML
    private TextField transactionDescriptionTextField;
    @FXML
    private Spinner<Integer> transactionAmountSpinner;
    @FXML
    private TextField transactionPriceTextField;
    @FXML
    private ComboBox<String> transactionCategoryComboBox;
    @FXML
    private ComboBox<TransactionType> transactionTypeComboBox;
    @FXML
    private DatePicker transactionDatePicker;

    public void initialize() {
        transactionAmountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100));

        transactionCategoryComboBox.setItems(
                FXCollections.observableArrayList(
                CategoryDatabaseRepository.getInstance().findAll().stream()
                .map(Category::getName)
                .toList()));

        transactionTypeComboBox.setItems(FXCollections.observableArrayList(TransactionType.values()));
    }

    public void addNewTransaction() {
        String description = transactionDescriptionTextField.getText();
        Integer amount = transactionAmountSpinner.getValue();

        String priceString = transactionPriceTextField.getText().trim();
        BigDecimal price = NumberValidator.validatePositiveBigDecimal(priceString);

        String categoryString = transactionCategoryComboBox.getSelectionModel().getSelectedItem();
        Category category = CategoryDatabaseRepository.getInstance()
                .findCategoryByName(categoryString, UserService.getLoggedInUser()).orElse(null);

        LocalDate date = transactionDatePicker.getValue();
        TransactionType transactionType = transactionTypeComboBox.getSelectionModel().getSelectedItem();

        Transaction transaction = new Transaction.Builder()
                .withDescription(description)
                .withAmount(amount)
                .withPrice(price)
                .withCategory(category)
                .withDate(date)
                .withTransactionType(transactionType)
                .withUser(UserService.getLoggedInUser())
                .build();

        try {
            TransactionService.createNewTransaction(transaction);
        } catch (TransactionValidationException e) {
            DialogService.error("Transaction not created", e.getMessage());
        }
    }
}
