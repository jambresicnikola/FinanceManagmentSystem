package hr.java.financemanagementsystem.database;

import hr.java.financemanagementsystem.dto.TransactionFilterForm;
import hr.java.financemanagementsystem.exception.RepositoryAccessException;
import hr.java.financemanagementsystem.model.Category;
import hr.java.financemanagementsystem.model.Transaction;
import hr.java.financemanagementsystem.model.TransactionType;
import hr.java.financemanagementsystem.model.User;
import hr.java.financemanagementsystem.service.UserService;
import hr.java.financemanagementsystem.util.DatabaseConnection;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionDatabaseRepository extends AbstractRepository<Transaction> {
    private static final String SAVE_TRANSACTION_QUERY =
            """
            INSERT INTO TRANSACTIONS
            (DESCRIPTION, AMOUNT, PRICE, CATEGORY_ID, TRANSACTION_DATE, TRANSACTION_TYPE, USER_ID) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
    private static final String FIND_ALL_TRANSACTIONS_QUERY =
            """
            SELECT ID, DESCRIPTION, AMOUNT, PRICE, CATEGORY_ID, TRANSACTION_DATE, TRANSACTION_TYPE, USER_ID 
            FROM TRANSACTIONS WHERE USER_ID = ?
            """;
    private static final String UPDATE_TRANSACTION_QUERY =
            """
            UPDATE TRANSACTIONS
            SET DESCRIPTION = ?, AMOUNT = ?, PRICE = ?, CATEGORY_ID = ?, TRANSACTION_DATE = ?, TRANSACTION_TYPE = ?
            WHERE ID = ?;
            """;
    private static final String DELETE_TRANSACTION_QUERY =
            """
            DELETE FROM TRANSACTIONS
            WHERE ID = ?;
            """;
    private static final String DELETE_TRANSACTIONS_BY_CATEGORY_QUERY =
            """
            DELETE FROM TRANSACTIONS
            WHERE CATEGORY_ID = ?;
            """;

    private TransactionDatabaseRepository() {}

    private static TransactionDatabaseRepository instance;

    public static TransactionDatabaseRepository getInstance() {
        if (Optional.ofNullable(instance).isEmpty()) {
            instance = new TransactionDatabaseRepository();
        }

        return instance;
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement stmt = connection.prepareStatement(FIND_ALL_TRANSACTIONS_QUERY)) {
                stmt.setLong(1, UserService.getLoggedInUser().getId());

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Transaction transaction = extractTransactionFromResultSet(rs);
                    transactions.add(transaction);
                }
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }

        return transactions;
    }

    private Transaction extractTransactionFromResultSet(ResultSet rs) throws SQLException {
        Long id = rs.getLong("ID");
        String description = rs.getString("DESCRIPTION");
        Integer amount = rs.getInt("AMOUNT");
        BigDecimal price = rs.getBigDecimal("PRICE");
        Category category = CategoryDatabaseRepository.getInstance().findById(rs.getLong("CATEGORY_ID")).get();
        LocalDate date = rs.getDate("TRANSACTION_DATE").toLocalDate();
        TransactionType transactionType = TransactionType.valueOf(rs.getString("TRANSACTION_TYPE"));
        User user = UserDatabaseRepository.getInstance().findById(rs.getLong("USER_ID")).get();

        return new Transaction.Builder()
                .withId(id)
                .withDescription(description)
                .withAmount(amount)
                .withPrice(price)
                .withCategory(category)
                .withDate(date)
                .withTransactionType(transactionType)
                .withUser(user)
                .build();
    }

    @Override
    public void save(Transaction entity) {
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement stmt = connection.prepareStatement(SAVE_TRANSACTION_QUERY)) {
                stmt.setString(1, entity.getDescription());
                stmt.setInt(2, entity.getAmount());
                stmt.setBigDecimal(3, entity.getPrice());
                stmt.setLong(4, entity.getCategory().getId());
                stmt.setDate(5, Date.valueOf(entity.getDate()));
                stmt.setString(6, entity.getTransactionType().toString());
                stmt.setLong(7, entity.getUser().getId());

                stmt.executeUpdate();
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }

    @Override
    public void delete(Transaction entity) {
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement stmt = connection.prepareStatement(DELETE_TRANSACTION_QUERY)) {
                stmt.setLong(1, entity.getId());

                stmt.executeUpdate();
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }

    @Override
    public void update(Transaction entity) {
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement stmt = connection.prepareStatement(UPDATE_TRANSACTION_QUERY)) {
                stmt.setString(1, entity.getDescription());
                stmt.setInt(2, entity.getAmount());
                stmt.setBigDecimal(3, entity.getPrice());
                stmt.setLong(4, entity.getCategory().getId());
                stmt.setDate(5, Date.valueOf(entity.getDate()));
                stmt.setString(6, entity.getTransactionType().toString());
                stmt.setLong(7, entity.getId());

                stmt.executeUpdate();
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }

    public List<Transaction> findByFilters(TransactionFilterForm transactionFilterForm) {
        StringBuilder filterQuery = new StringBuilder(FIND_ALL_TRANSACTIONS_QUERY);
        List<Object> params = new ArrayList<>();

        if (!transactionFilterForm.getDescription().isEmpty()) {
            filterQuery.append(" AND LOWER(DESCRIPTION) LIKE ?");
            params.add("%" + transactionFilterForm.getDescription().toLowerCase() + "%");
        }
        if (transactionFilterForm.getCategory() != null) {
            filterQuery.append(" AND CATEGORY_ID = ?");
            params.add(transactionFilterForm.getCategory().getId());
        }
        if (transactionFilterForm.getTransactionType() != null) {
            filterQuery.append(" AND TRANSACTION_TYPE = ?");
            params.add(transactionFilterForm.getTransactionType().toString());
        }
        if (transactionFilterForm.getPriceFrom() != null) {
            filterQuery.append(" AND PRICE >= ?");
            params.add(transactionFilterForm.getPriceFrom());
        }
        if (transactionFilterForm.getPriceTo() != null) {
            filterQuery.append(" AND PRICE <= ?");
            params.add(transactionFilterForm.getPriceTo());
        }
        if (transactionFilterForm.getFromDate() != null) {
            filterQuery.append(" AND TRANSACTION_DATE >= ?");
            params.add(transactionFilterForm.getFromDate());
        }
        if (transactionFilterForm.getToDate() != null) {
            filterQuery.append(" AND TRANSACTION_DATE <= ?");
            params.add(transactionFilterForm.getToDate());
        }

        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement stmt = connection.prepareStatement(filterQuery.toString())) {
                stmt.setLong(1, UserService.getLoggedInUser().getId());

                Integer parameterCounter = 2;
                for (Object param : params) {
                    stmt.setObject(parameterCounter, param);
                    parameterCounter++;
                }

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    transactions.add(extractTransactionFromResultSet(rs));
                }
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }

        return transactions;
    }

    public void deleteTransactionsByCategory(Category category) {
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement stmt = connection.prepareStatement(DELETE_TRANSACTIONS_BY_CATEGORY_QUERY)) {
                stmt.setLong(1, category.getId());

                stmt.executeUpdate();
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }
}