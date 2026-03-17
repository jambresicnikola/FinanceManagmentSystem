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
import hr.java.financemanagementsystem.util.SceneManager;
import hr.java.financemanagementsystem.util.TableUtils;
import hr.java.financemanagementsystem.validation.NumberValidator;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the Manage Transactions screen.
 * Shows all transactions in a table and allows the user to filter, edit or delete them.
 */
public class ManageTransactionsScreenController {
    private static final Logger logger = LoggerFactory.getLogger(ManageTransactionsScreenController.class);

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

    /**
     * Sets up the filter dropdowns, table columns and loads all transactions when the screen opens.
     */
    public void initialize() {
        setupFilterDropdowns();
        setupTableColumns();
        loadAllTransactions();
    }

    /**
     * Called when the user clicks the filter button.
     * Reads the filter fields and updates the table with matching transactions.
     */
    public void filterTransactions() {
        String description = transactionDescriptionTextField.getText().trim();
        Category category = CategoryDatabaseRepository.getInstance()
                .findCategoryByName(transactionCategoryComboBox.getValue(), UserService.getLoggedInUser())
                .orElse(null);
        TransactionType transactionType = transactionTypeComboBox.getValue();
        BigDecimal priceFrom = NumberValidator.validatePositiveBigDecimal(priceFromTextField.getText());
        BigDecimal priceTo = NumberValidator.validatePositiveBigDecimal(priceToTextField.getText());
        LocalDate fromDate = dateFromDatePicker.getValue();
        LocalDate toDate = dateToDatePicker.getValue();

        logger.debug("Filtering transactions with - description: '{}', category: '{}', type: '{}', " +
                        "priceFrom: {}, priceTo: {}, fromDate: {}, toDate: {}",
                description, category, transactionType, priceFrom, priceTo, fromDate, toDate);

        TransactionFilterForm transactionFilterForm = new TransactionFilterForm(
                description, category, transactionType, priceFrom, priceTo, fromDate, toDate);

        List<Transaction> transactions = TransactionService.filterTransactions(transactionFilterForm);
        transactionsTableView.setItems(FXCollections.observableArrayList(transactions));
        logger.debug("Filter returned {} transactions.", transactions.size());
    }

    /**
     * Populates the category and transaction type dropdowns with available options.
     * Adds a null option at the top of each so the user can select "no filter".
     */
    private void setupFilterDropdowns() {
        List<String> categories = new ArrayList<>();
        categories.add(null);
        categories.addAll(CategoryDatabaseRepository.getInstance().findAll().stream()
                .map(Category::getName)
                .toList());
        transactionCategoryComboBox.setItems(FXCollections.observableArrayList(categories));

        List<TransactionType> transactionTypes = new ArrayList<>();
        transactionTypes.add(null);
        transactionTypes.addAll(List.of(TransactionType.values()));
        transactionTypeComboBox.setItems(FXCollections.observableArrayList(transactionTypes));
    }

    /**
     * Configures cell value factories and button columns for the transactions table.
     */
    private void setupTableColumns() {
        descriptionTableColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getDescription()));
        priceTableColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getPrice()));
        dateTableColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getDate()));

        editTableColumn.setCellFactory(TableUtils.createButtonColumn("Edit", (Transaction transaction) -> {
            logger.debug("Edit requested for transaction with id: {}", transaction.getId());
            TransactionService.setTransactionToManage(transaction);
            SceneManager.openEditTransactionsScreen();
        }));
        deleteTableColumn.setCellFactory(TableUtils.createButtonColumn("Delete", (Transaction transaction) -> {
            if (DialogService.confirmation("Delete Transaction",
                    "Are you sure you want to delete this transaction? This action cannot be undone.")) {
                logger.info("Deleting transaction with id: {}", transaction.getId());
                TransactionService.deleteTransaction(transaction);
                transactionsTableView.getItems().remove(transaction);
            }
        }));
    }

    /**
     * Loads all transactions from the database and displays them in the table.
     */
    private void loadAllTransactions() {
        transactionsTableView.setItems(FXCollections.observableArrayList(
                TransactionDatabaseRepository.getInstance().findAll()));
    }
}
