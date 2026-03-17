package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.database.TransactionDatabaseRepository;
import hr.java.financemanagementsystem.model.Transaction;
import hr.java.financemanagementsystem.model.TransactionType;
import hr.java.financemanagementsystem.service.UserService;
import hr.java.financemanagementsystem.util.SceneManager;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Controller for the Home screen.
 * Shows a welcome message, latest transactions and a summary of total income and expenses.
 */
public class HomeScreenController {
    private static final Logger logger = LoggerFactory.getLogger(HomeScreenController.class);

    @FXML
    private Label welcomeMessageLabel;
    @FXML
    private Label totalIncomeLabel;
    @FXML
    private Label totalExpenseLabel;
    @FXML
    private TableView<Transaction> transactionsTableView;
    @FXML
    private TableColumn<Transaction, String> descriptionTableColumn;
    @FXML
    private TableColumn<Transaction, TransactionType> transactionTypeTableColumn;
    @FXML
    private TableColumn<Transaction, BigDecimal> priceTableColumn;

    /**
     * Sets up the home screen when it loads.
     * Populates the welcome message, latest transactions table, income and expense summary.
     */
    public void initialize(){
        logger.debug("Initializing home screen for user with id: {}", UserService.getLoggedInUser().getId());

        setupWelcomeMessage();
        setupTableColumns();
        loadLatestTransactions();
        loadSummary();
    }

    /**
     * Navigates to the Manage Transactions screen.
     */
    public void viewAllTransactions() {
        SceneManager.openManageTransactionsScreen();
    }

    /**
     * Sets the welcome message using the logged-in user's full name.
     */
    private void setupWelcomeMessage() {
        String fullName = UserService.getLoggedInUser().getFirstName() + " " + UserService.getLoggedInUser().getLastName();
        welcomeMessageLabel.setText("Welcome back, " + fullName + "!");
    }

    /**
     * Sets up the cell value factories for the transactions table columns.
     */
    private void setupTableColumns() {
        descriptionTableColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getDescription())
        );
        transactionTypeTableColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTransactionType())
        );
        priceTableColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getPrice())
        );
    }

    /**
     * Fetches and displays the latest transactions in the table.
     */
    private void loadLatestTransactions() {
        transactionsTableView.setItems(FXCollections.observableArrayList(
                TransactionDatabaseRepository.getInstance().findLatestTransactions()));
    }

    /**
     * Fetches and displays the total income and expense summary.
     */
    private void loadSummary() {
        totalIncomeLabel.setText(TransactionDatabaseRepository.getInstance().findTotalIncome() + " €");
        totalExpenseLabel.setText(TransactionDatabaseRepository.getInstance().findTotalOutcome() + " €");
    }
}
