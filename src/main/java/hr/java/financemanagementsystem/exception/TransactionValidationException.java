package hr.java.financemanagementsystem.exception;

/**
 * Thrown when transaction data fails validation.
 */
public class TransactionValidationException extends RuntimeException {
    public TransactionValidationException(String message) {
        super(message);
    }

    public TransactionValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionValidationException(Throwable cause) {
        super(cause);
    }

    public TransactionValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public TransactionValidationException() {
    }
}
