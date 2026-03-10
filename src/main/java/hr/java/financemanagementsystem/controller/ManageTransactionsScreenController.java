package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.database.CategoryDatabaseRepository;
import hr.java.financemanagementsystem.database.TransactionDatabaseRepository;
import hr.java.financemanagementsystem.dto.TransactionFilterForm;
import hr.java.financemanagementsystem.model.Category;
import hr.java.financemanagementsystem.model.Transaction;
import hr.java.financemanagementsystem.model.TransactionType;
import hr.java.financemanagementsystem.service.DialogService;
import hr.java.financemanagementsystem.service.TransactionService;
import hr.java.financemanagementsystem.service.UserService;
import hr.java.financemanagementsystem.util.ScreenManager;
import hr.java.financemanagementsystem.util.TableUtils;
import hr.java.financemanagementsystem.validation.NumberValidator;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ManageTransactionsScreenController {
    @FXML
    private TextField transactionDescriptionTextField;
    @FXML
    private ComboBox<String> transactionCategoryComboBox;
    @FXML
    private ComboBox<TransactionType> transactionTypeComboBox;
    @FXML
    private TextField priceFromTextField;
    @FXML
    private TextField priceToTextField;
    @FXML
    private DatePicker dateFromDatePicker;
    @FXML
    private DatePicker dateToDatePicker;
    @FXML
    private TableView<Transaction> transactionsTableView;
    @FXML
    private TableColumn<Transaction, String> descriptionTableColumn;
    @FXML
    private TableColumn<Transaction, BigDecimal> priceTableColumn;
    @FXML
    private TableColumn<Transaction, LocalDate> dateTableColumn;
    @FXML
    private TableColumn<Transaction, Void> editTableColumn;
    @FXML
    private TableColumn<Transaction, Void> deleteTableColumn;

    public void initialize() {
        transactionCategoryComboBox.setItems(
                FXCollections.observableArrayList(CategoryDatabaseRepository.getInstance().findAll().stream()
                        .map(Category::getName)
                        .toList()));
        transactionTypeComboBox.setItems(FXCollections.observableArrayList(TransactionType.values()));

        descriptionTableColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getDescription()));
        priceTableColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getPrice()));
        dateTableColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getDate()));

        editTableColumn.setCellFactory(TableUtils.createButtonColumn("Edit", (Transaction transaction) -> {
            TransactionService.setTransactionToManage(transaction);
            ScreenManager.openEditTransactionsScreen();
        }));
        deleteTableColumn.setCellFactory(TableUtils.createButtonColumn("Delete", (Transaction transaction) -> {
            if (DialogService.confirmation("Delete Transaction", "Are you sure you want to delete this transaction?")) {
                TransactionService.deleteTransaction(transaction);
                transactionsTableView.getItems().remove(transaction);
            }
        }));

        transactionsTableView.setItems(FXCollections.observableArrayList(TransactionDatabaseRepository.getInstance().findAll()));
    }

    public void filterTransactions() {
        String description = transactionDescriptionTextField.getText();

        Category category = CategoryDatabaseRepository.getInstance()
                .findCategoryByName(transactionCategoryComboBox.getValue(), UserService.getLoggedInUser())
                .orElse(null);
        TransactionType transactionType = transactionTypeComboBox.getValue();
        BigDecimal priceFrom = NumberValidator.validatePositiveBigDecimal(priceFromTextField.getText());
        BigDecimal priceTo = NumberValidator.validatePositiveBigDecimal(priceToTextField.getText());
        LocalDate fromDate = dateFromDatePicker.getValue();
        LocalDate toDate = dateToDatePicker.getValue();

        TransactionFilterForm transactionFilterForm = new TransactionFilterForm(
                description, category, transactionType, priceFrom, priceTo, fromDate, toDate);

        List<Transaction> transactions = TransactionService.filterTransactions(transactionFilterForm);

        transactionsTableView.setItems(FXCollections.observableArrayList(transactions));
    }
}
