package hr.java.financemanagementsystem.dto;

import hr.java.financemanagementsystem.model.Category;
import hr.java.financemanagementsystem.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Holds the filter criteria for searching transactions.
 * Fields that are null or empty are ignored in the filter query.
 */
public class TransactionFilterForm {
    private String description;
    private Category category;
    private TransactionType transactionType;
    private BigDecimal priceFrom;
    private BigDecimal priceTo;
    private LocalDate fromDate;
    private LocalDate toDate;

    public TransactionFilterForm(String description, Category category, TransactionType transactionType, BigDecimal priceFrom, BigDecimal priceTo, LocalDate fromDate, LocalDate toDate) {
        this.description = description;
        this.category = category;
        this.transactionType = transactionType;
        this.priceFrom = priceFrom;
        this.priceTo = priceTo;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getPriceFrom() {
        return priceFrom;
    }

    public void setPriceFrom(BigDecimal priceFrom) {
        this.priceFrom = priceFrom;
    }

    public BigDecimal getPriceTo() {
        return priceTo;
    }

    public void setPriceTo(BigDecimal priceTo) {
        this.priceTo = priceTo;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }
}
