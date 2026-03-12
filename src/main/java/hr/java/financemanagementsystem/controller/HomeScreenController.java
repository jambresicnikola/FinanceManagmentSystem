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

import java.math.BigDecimal;

public class HomeScreenController {
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

    public void initialize(){
        welcomeMessageLabel.setText("Hi, " + UserService.getLoggedInUser().getFirstName() + " " + UserService.getLoggedInUser().getLastName() + "!");

        descriptionTableColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getDescription())
        );
        transactionTypeTableColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTransactionType())
        );
        priceTableColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getPrice())
        );

        transactionsTableView.setItems(FXCollections.observableArrayList(TransactionDatabaseRepository.getInstance().findLatestTransactions()));

        totalIncomeLabel.setText(TransactionDatabaseRepository.getInstance().findTotalIncome() + "€");
        totalExpenseLabel.setText(TransactionDatabaseRepository.getInstance().findTotalOutcome() + "€");
    }

    public void viewAllTransactions() {
        SceneManager.openManageTransactionsScreen();
    }
}
