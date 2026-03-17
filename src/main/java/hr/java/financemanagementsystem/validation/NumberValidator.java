package hr.java.financemanagementsystem.validation;

import java.math.BigDecimal;

/**
 * Utility class for parsing and validating numeric input.
 */
public class NumberValidator {
    private NumberValidator() {
    }

    /**
     * Tries to parse a string as a positive {@link BigDecimal}.
     * Returns null if the string is empty, blank, or not a valid number.
     * @param stringValue the string to parse
     * @return the parsed {@link BigDecimal}, or null if parsing fails
     */
    public static BigDecimal validatePositiveBigDecimal(String stringValue) {
        if (stringValue == null || stringValue.isBlank()) {
            return null;
        }

        try {
            return new BigDecimal(stringValue.trim());
        } catch (NumberFormatException _) {
            return null;
        }
    }
}
