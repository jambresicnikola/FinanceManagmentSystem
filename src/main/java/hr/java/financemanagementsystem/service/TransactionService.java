package hr.java.financemanagementsystem.service;

import hr.java.financemanagementsystem.database.TransactionDatabaseRepository;
import hr.java.financemanagementsystem.model.Transaction;
import hr.java.financemanagementsystem.validation.TransactionValidator;

public class TransactionService {
    private TransactionService() {}

    public static void createNewTransaction(Transaction transaction) {
        TransactionValidator.validateTransaction(transaction);

        TransactionDatabaseRepository.getInstance().save(transaction);

        DialogService.information("Transaction added", "You have successfully added the transaction.");
    }
}
