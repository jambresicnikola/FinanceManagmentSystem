package hr.java.financemanagementsystem.service;

import hr.java.financemanagementsystem.database.TransactionDatabaseRepository;
import hr.java.financemanagementsystem.exception.TransactionValidationException;
import hr.java.financemanagementsystem.dto.TransactionFilterForm;
import hr.java.financemanagementsystem.model.Category;
import hr.java.financemanagementsystem.model.Transaction;
import hr.java.financemanagementsystem.validation.TransactionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Handles business logic for transaction management.
 */
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private TransactionService() {}

    private static Transaction transactionToManage;

    public static Transaction getTransactionToManage() {
        return transactionToManage;
    }

    public static void setTransactionToManage(Transaction transactionToManage) {
        TransactionService.transactionToManage = transactionToManage;
    }

    /**
     * Validates and saves a new transaction.
     * @param transaction the transaction to create
     * @throws TransactionValidationException if validation fails
     */
    public static void createNewTransaction(Transaction transaction) {
        logger.debug("Creating new transaction for user with id: {}", transaction.getUser().getId());
        TransactionValidator.validateTransaction(transaction);

        TransactionDatabaseRepository.getInstance().save(transaction);
        logger.info("Transaction successfully created for user with id: {}", transaction.getUser().getId());
    }

    /**
     * Returns transactions matching the given filter criteria.
     * @param transactionFilterForm the filter criteria to apply
     * @return list of matching transactions, empty list if none found
     */
    public static List<Transaction> filterTransactions(TransactionFilterForm transactionFilterForm) {
        logger.debug("Filtering transactions.");
        return TransactionDatabaseRepository.getInstance().findByFilters(transactionFilterForm);
    }

    /**
     * Validates and updates an existing transaction.
     * @param transaction the transaction with updated values
     * @throws TransactionValidationException if validation fails
     */
    public static void updateTransaction(Transaction transaction) {
        logger.debug("Updating transaction with id: {}", transaction.getId());
        TransactionValidator.validateTransaction(transaction);

        TransactionDatabaseRepository.getInstance().update(transaction);
        logger.info("Transaction with id: {} successfully updated.", transaction.getId());
    }

    /**
     * Deletes a transaction from the database.
     * @param transaction the transaction to delete
     */
    public static void deleteTransaction(Transaction transaction) {
        logger.debug("Deleting transaction with id: {}", transaction.getId());
        TransactionDatabaseRepository.getInstance().delete(transaction);
        logger.info("Transaction with id: {} successfully deleted.", transaction.getId());
    }

    /**
     * Deletes all transactions linked to a specific category.
     * @param category the category whose transactions should be deleted
     */
    public static void deleteTransactionsByCategory(Category category) {
        logger.debug("Deleting all transactions for category: '{}'", category.getName());
        TransactionDatabaseRepository.getInstance().deleteTransactionsByCategory(category);
        logger.info("All transactions deleted for category: '{}'", category.getName());
    }
}
