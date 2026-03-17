package hr.java.financemanagementsystem.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a financial transaction made by a user.
 */
public class Transaction extends Entity {
    private String description;
    private Integer amount;
    private BigDecimal price;
    private Category category;
    private LocalDate date;
    private TransactionType transactionType;
    private User user;

    private Transaction(Builder builder) {
        super(builder.id);
        this.description = builder.description;
        this.amount = builder.amount;
        this.price = builder.price;
        this.category = builder.category;
        this.date = builder.date;
        this.transactionType = builder.transactionType;
        this.user = builder.user;
    }

    /**
     * Builder for creating {@link Transaction} instances.
     */
    public static class Builder {
        private Long id;
        private String description;
        private Integer amount;
        private BigDecimal price;
        private Category category;
        private LocalDate date;
        private TransactionType transactionType;
        private User user;

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withAmount(Integer amount) {
            this.amount = amount;
            return this;
        }

        public Builder withPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder withCategory(Category category) {
            this.category = category;
            return this;
        }

        public Builder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder withTransactionType(TransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        public Builder withUser(User user) {
            this.user = user;
            return this;
        }

        public Transaction build() {
            return new Transaction(this);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
