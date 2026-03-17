package hr.java.financemanagementsystem.validation;

import hr.java.financemanagementsystem.exception.TransactionValidationException;
import hr.java.financemanagementsystem.model.Transaction;

import java.math.BigDecimal;

/**
 * Validates {@link Transaction} data before saving or updating.
 */
public class TransactionValidator {
    private TransactionValidator() {}

    private static final BigDecimal MAX_PRICE = BigDecimal.valueOf(10_000_000);

    /**
     * Validates all fields of a transaction.
     * Collects all validation errors and throws them together in one exception.
     * @param transaction the transaction to validate
     * @throws TransactionValidationException if any field is invalid
     */
    public static void validateTransaction(Transaction transaction) throws TransactionValidationException {
        StringBuilder errorMessage = new StringBuilder();

        if (transaction.getDescription() == null || transaction.getDescription().isBlank()) {
            errorMessage.append("\nDescription cannot be empty.");
        } else if (transaction.getDescription().length() > 255) {
            errorMessage.append("\nDescription cannot be longer than 255 characters.");
        }

        if (transaction.getPrice() == null || transaction.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            errorMessage.append("\nPrice is required and must be a positive number (e.g. 100.00).");
        } else if (transaction.getPrice().compareTo(MAX_PRICE) > 0) {
            errorMessage.append("\nPrice cannot be greater than 10,000,000.");
        }

        if (transaction.getCategory() == null) {
            errorMessage.append("\nPlease select a category.");
        }

        if (transaction.getDate() == null) {
            errorMessage.append("\nPlease select a date.");
        }

        if (transaction.getTransactionType() == null) {
            errorMessage.append("\nPlease select a transaction type.");
        }

        if (!errorMessage.isEmpty()) {
            throw new TransactionValidationException(errorMessage.toString());
        }
    }
}
