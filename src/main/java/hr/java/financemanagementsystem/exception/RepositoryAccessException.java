package hr.java.financemanagementsystem.exception;

/**
 * Thrown when a database operation fails due to a connection or SQL error.
 * Wraps low level exceptions like {@link java.sql.SQLException} and {@link java.io.IOException}.
 */
public class RepositoryAccessException extends RuntimeException {
    public RepositoryAccessException(String message) {
        super(message);
    }

    public RepositoryAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositoryAccessException(Throwable cause) {
        super(cause);
    }

    public RepositoryAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RepositoryAccessException() {
    }
}
