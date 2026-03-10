package hr.java.financemanagementsystem.service;

import hr.java.financemanagementsystem.database.TransactionDatabaseRepository;
import hr.java.financemanagementsystem.dto.TransactionFilterForm;
import hr.java.financemanagementsystem.model.Category;
import hr.java.financemanagementsystem.model.Transaction;
import hr.java.financemanagementsystem.util.ScreenManager;
import hr.java.financemanagementsystem.validation.TransactionValidator;

import java.util.List;

public class TransactionService {
    private TransactionService() {}

    private static Transaction transactionToManage;

    public static Transaction getTransactionToManage() {
        return transactionToManage;
    }

    public static void setTransactionToManage(Transaction transactionToManage) {
        TransactionService.transactionToManage = transactionToManage;
    }

    public static void createNewTransaction(Transaction transaction) {
        TransactionValidator.validateTransaction(transaction);

        TransactionDatabaseRepository.getInstance().save(transaction);

        DialogService.information("Transaction added", "You have successfully added the transaction.");

        ScreenManager.openManageTransactionsScreen();
    }

    public static List<Transaction> filterTransactions(TransactionFilterForm transactionFilterForm) {
        return TransactionDatabaseRepository.getInstance().findByFilters(transactionFilterForm);
    }

    public static void updateTransaction(Transaction transaction) {
        TransactionValidator.validateTransaction(transaction);

        TransactionDatabaseRepository.getInstance().update(transaction);

        DialogService.information("Transaction updated", "You have successfully updated the transaction.");

        ScreenManager.openManageTransactionsScreen();
    }

    public static void deleteTransaction(Transaction transaction) {
        TransactionDatabaseRepository.getInstance().delete(transaction);
    }

    public static void deleteTransactionsByCategory(Category category) {
        TransactionDatabaseRepository.getInstance().deleteTransactionsByCategory(category);
    }
}
