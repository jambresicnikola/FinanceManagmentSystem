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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Base controller for screens that contain a transaction form.
 * Holds shared form fields and logic used by both
 * {@link AddTransactionScreenController} and {@link EditTransactionScreenController}.
 */
public class TransactionFormIncludeController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionFormIncludeController.class);

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

    /**
     * Sets up the shared form fields with default values.
     * Should be called from the {@code initialize()} method of each subclass.
     */
    protected void initializeForm() {
        transactionAmountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100));

        transactionCategoryComboBox.setItems(
                FXCollections.observableArrayList(
                        CategoryDatabaseRepository.getInstance().findAll().stream()
                                .map(Category::getName)
                                .toList()));

        transactionTypeComboBox.setItems(FXCollections.observableArrayList(TransactionType.values()));

        logger.debug("Transaction form initialized.");
    }

    /**
     * Reads all form fields and builds a {@link Transaction} object from them.
     * Returns a transaction with null fields if the user left something empty —
     * validation is handled by the caller.
     *
     * @return a {@link Transaction} built from the current form input
     */
    protected Transaction buildTransactionFromForm() {
        String description = transactionDescriptionTextField.getText();
        Integer amount = transactionAmountSpinner.getValue();

        String priceString = transactionPriceTextField.getText().trim();
        BigDecimal price = NumberValidator.validatePositiveBigDecimal(priceString);

        String categoryName = transactionCategoryComboBox.getSelectionModel().getSelectedItem();
        Category category = CategoryDatabaseRepository.getInstance()
                .findCategoryByName(categoryName, UserService.getLoggedInUser()).orElse(null);

        LocalDate date = transactionDatePicker.getValue();
        TransactionType transactionType = transactionTypeComboBox.getSelectionModel().getSelectedItem();

        logger.debug("Building transaction from form - description: '{}', amount: {}, price: {}, " +
                        "category: '{}', date: {}, type: {}",
                description, amount, price, categoryName, date, transactionType);

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
