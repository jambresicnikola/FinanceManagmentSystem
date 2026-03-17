package hr.java.financemanagementsystem.exception;

/**
 * Thrown when a database operation returns data in an inconsistent or unexpected state.
 */
public class DataIntegrityException extends RuntimeException {
    public DataIntegrityException(String message) {
        super(message);
    }

    public DataIntegrityException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataIntegrityException(Throwable cause) {
        super(cause);
    }

    public DataIntegrityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DataIntegrityException() {
    }
}
