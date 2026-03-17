package hr.java.financemanagementsystem.exception;

/**
 * Thrown when a screen fails to load, usually due to a missing or invalid FXML file.
 */
public class ScreenLoadingException extends RuntimeException {
    public ScreenLoadingException(String message) {
        super(message);
    }

    public ScreenLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScreenLoadingException(Throwable cause) {
        super(cause);
    }

    public ScreenLoadingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ScreenLoadingException() {
    }
}
