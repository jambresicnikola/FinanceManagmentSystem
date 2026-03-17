package hr.java.financemanagementsystem.exception;

/**
 * Thrown when a password change request fails validation.
 */
public class PasswordChangeException extends RuntimeException {
    public PasswordChangeException(String message) {
        super(message);
    }

    public PasswordChangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public PasswordChangeException(Throwable cause) {
        super(cause);
    }

    public PasswordChangeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public PasswordChangeException() {
    }
}
