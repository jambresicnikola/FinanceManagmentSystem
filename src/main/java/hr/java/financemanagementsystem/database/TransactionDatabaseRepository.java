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
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

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
            ORDER BY ID DESC
            """;
    private static final String FILTER_TRANSACTIONS_QUERY =
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
    private static final String FIND_TOTAL_INCOME_LAST_30_DAYS_QUERY = """
            SELECT SUM(PRICE) AS TOTAL_INCOME_LAST_30_DAYS
            FROM TRANSACTIONS
            WHERE USER_ID = ?
              AND TRANSACTION_TYPE = 'INCOME'
              AND TRANSACTION_DATE > CURDATE() - 30
            """;
    private static final String FIND_TOTAL_OUTCOME_LAST_30_DAYS_QUERY = """
            SELECT SUM(PRICE) AS TOTAL_OUTCOME_LAST_30_DAYS
            FROM TRANSACTIONS
            WHERE USER_ID = ?
              AND TRANSACTION_TYPE = 'OUTCOME'
              AND TRANSACTION_DATE > CURDATE() - 30
            """;
    private static final String DELETE_TRANSACTIONS_BY_USER_QUERY =
            """
            DELETE FROM TRANSACTIONS
            WHERE USER_ID = ?;
            """;
    private static final String EXPENSES_PER_CATEGORY_QUERY = """
            SELECT SUM(PRICE) AS EXPENSE, CATEGORIES.NAME AS CATEGORY_NAME
            FROM TRANSACTIONS
                     JOIN CATEGORIES ON CATEGORIES.ID = TRANSACTIONS.CATEGORY_ID
            WHERE TRANSACTIONS.USER_ID = ?
              AND TRANSACTION_TYPE = 'OUTCOME'
              AND YEAR(TRANSACTION_DATE) = YEAR(CURRENT_DATE)
            GROUP BY CATEGORY_ID;
            
            """;
    private static final String INCOME_PER_MONTH_QUERY = """
            SELECT SUM(PRICE) AS INCOME, MONTHNAME(TRANSACTION_DATE) AS TRANSACTION_MONTH
            FROM TRANSACTIONS
            WHERE USER_ID = ?
              AND TRANSACTION_TYPE = 'INCOME'
              AND YEAR(TRANSACTION_DATE) = YEAR(CURRENT_DATE)
            GROUP BY MONTH(TRANSACTION_DATE), TRANSACTION_MONTH
            ORDER BY MONTH(TRANSACTION_DATE);
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
        StringBuilder filterQuery = new StringBuilder(FILTER_TRANSACTIONS_QUERY);
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

        filterQuery.append(" ORDER BY ID DESC");

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

    public List<Transaction> findLatestTransactions() {
        return findAll().stream().limit(5).toList();
    }

    public BigDecimal findTotalIncome() {
        return findTotal(FIND_TOTAL_INCOME_LAST_30_DAYS_QUERY, "TOTAL_INCOME_LAST_30_DAYS");
    }

    public BigDecimal findTotalOutcome() {
        return findTotal(FIND_TOTAL_OUTCOME_LAST_30_DAYS_QUERY, "TOTAL_OUTCOME_LAST_30_DAYS");
    }

    private BigDecimal findTotal(String query, String columnName) {
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setLong(1, UserService.getLoggedInUser().getId());

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    BigDecimal result = rs.getBigDecimal(columnName);
                    return result != null ? result : BigDecimal.ZERO;
                }
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }

        return BigDecimal.ZERO;
    }

    public void deleteTransactionsByUser(User loggedInUser) {
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement stmt = connection.prepareStatement(DELETE_TRANSACTIONS_BY_USER_QUERY)) {
                stmt.setLong(1, loggedInUser.getId());

                stmt.executeUpdate();
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }

    public Map<String, BigDecimal> getPieChartData() {
        return getChartData(EXPENSES_PER_CATEGORY_QUERY, "CATEGORY_NAME", "EXPENSE");
    }

    public Map<String, BigDecimal> getAreaChartData() {
        return getChartData(INCOME_PER_MONTH_QUERY, "TRANSACTION_MONTH", "INCOME");
    }

    public Map<String, BigDecimal> getChartData(String query, String keyColumn, String valueColumn) {
        Map<String, BigDecimal> data = new LinkedHashMap<>();

        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setLong(1, UserService.getLoggedInUser().getId());

                ResultSet rs = stmt.executeQuery();

                while(rs.next()) {
                    data.put(rs.getString(keyColumn), rs.getBigDecimal(valueColumn));
                }
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }

        return data;
    }
}