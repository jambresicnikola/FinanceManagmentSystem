package hr.java.financemanagementsystem.exception;

public class CategoryValidationException extends RuntimeException {
    public CategoryValidationException(String message) {
        super(message);
    }

    public CategoryValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CategoryValidationException(Throwable cause) {
        super(cause);
    }

    public CategoryValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CategoryValidationException() {
    }
}
