package hr.java.financemanagementsystem.validation;

import hr.java.financemanagementsystem.exception.TransactionValidationException;
import hr.java.financemanagementsystem.model.Transaction;

import java.math.BigDecimal;

public class TransactionValidator {
    private TransactionValidator() {}

    public static void validateTransaction(Transaction transaction) throws TransactionValidationException {
        StringBuilder errorMessage = new StringBuilder();

        if (transaction.getDescription().isEmpty()) {
            errorMessage.append("\nTransaction description cannot be empty or contain more than 255 characters.");
        }

        if (transaction.getPrice() == null || transaction.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            errorMessage.append("\nTransaction price cannot be empty and must be a positive number (e.g. 100.00).");
        }

        if (transaction.getCategory() == null) {
            errorMessage.append("\nTransaction category not selected.");
        }

        if (transaction.getDate() == null) {
            errorMessage.append("\nTransaction date not selected.");
        }

        if (transaction.getTransactionType() == null) {
            errorMessage.append("\nTransaction type not selected.");
        }

        if (!errorMessage.isEmpty()) {
            throw new TransactionValidationException(errorMessage.toString());
        }
    }
}
