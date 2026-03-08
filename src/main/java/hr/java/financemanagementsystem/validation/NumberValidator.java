package hr.java.financemanagementsystem.validation;

import java.math.BigDecimal;

public class NumberValidator {
    private NumberValidator() {
    }

    public static BigDecimal validatePositiveBigDecimal(String stringValue) {
        BigDecimal value;

        try {
            value = new BigDecimal(stringValue);
        } catch (NumberFormatException _) {
            value = null;
        }

        return value;
    }
}
