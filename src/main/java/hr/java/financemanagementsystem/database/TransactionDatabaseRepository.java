package hr.java.financemanagementsystem.database;

import hr.java.financemanagementsystem.dto.TransactionFilterForm;
import hr.java.financemanagementsystem.exception.DataIntegrityException;
import hr.java.financemanagementsystem.exception.RepositoryAccessException;
import hr.java.financemanagementsystem.model.Category;
import hr.java.financemanagementsystem.model.Transaction;
import hr.java.financemanagementsystem.model.TransactionType;
import hr.java.financemanagementsystem.model.User;
import hr.java.financemanagementsystem.service.UserService;
import hr.java.financemanagementsystem.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

/**
 * Repository for managing {@link Transaction} data in the database.
 * Follows the singleton pattern — use {@link #getInstance()} to get the instance.
 */
public class TransactionDatabaseRepository extends AbstractRepository<Transaction> {
    private static final Logger logger = LoggerFactory.getLogger(TransactionDatabaseRepository.class);

    private static final String FIND_TRANSACTION_BY_ID_QUERY =
            """
            SELECT ID, DESCRIPTION, AMOUNT, PRICE, CATEGORY_ID, TRANSACTION_DATE, TRANSACTION_TYPE, USER_ID FROM TRANSACTIONS WHERE ID = ?;
             """;
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
            WHERE ID = ?
            """;
    private static final String DELETE_TRANSACTION_QUERY =
            """
            DELETE FROM TRANSACTIONS
            WHERE ID = ?
            """;
    private static final String DELETE_TRANSACTIONS_BY_CATEGORY_QUERY =
            """
            DELETE FROM TRANSACTIONS
            WHERE CATEGORY_ID = ?
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
            WHERE USER_ID = ?
            """;
    private static final String EXPENSES_PER_CATEGORY_QUERY = """
            SELECT SUM(PRICE) AS EXPENSE, CATEGORIES.NAME AS CATEGORY_NAME
            FROM TRANSACTIONS
                     JOIN CATEGORIES ON CATEGORIES.ID = TRANSACTIONS.CATEGORY_ID
            WHERE TRANSACTIONS.USER_ID = ?
              AND TRANSACTION_TYPE = 'OUTCOME'
              AND YEAR(TRANSACTION_DATE) = YEAR(CURRENT_DATE)
            GROUP BY CATEGORY_ID
            """;
    private static final String INCOME_PER_MONTH_QUERY = """
            SELECT SUM(PRICE) AS INCOME, MONTHNAME(TRANSACTION_DATE) AS TRANSACTION_MONTH
            FROM TRANSACTIONS
            WHERE USER_ID = ?
              AND TRANSACTION_TYPE = 'INCOME'
              AND YEAR(TRANSACTION_DATE) = YEAR(CURRENT_DATE)
            GROUP BY MONTH(TRANSACTION_DATE), TRANSACTION_MONTH
            ORDER BY MONTH(TRANSACTION_DATE)
            """;

    private TransactionDatabaseRepository() {}

    private static TransactionDatabaseRepository instance;

    /**
     * Returns the singleton instance of this repository.
     * @return the single {@link TransactionDatabaseRepository} instance
     */
    public static TransactionDatabaseRepository getInstance() {
        if (Optional.ofNullable(instance).isEmpty()) {
            instance = new TransactionDatabaseRepository();
        }

        return instance;
    }

    /**
     * Finds a transaction by its id.
     * @param id the id of the transaction to find
     * @return an {@link Optional} containing the transaction if found, or empty if not
     * @throws RepositoryAccessException if a database error occurs
     */
    @Override
    public Optional<Transaction> findById(Long id) {
        logger.debug("Looking up transaction by id: {}", id);
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_TRANSACTION_BY_ID_QUERY)) {
                preparedStatement.setLong(1, id);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return Optional.of(extractTransactionFromResultSet(resultSet));
                }
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }

        return Optional.empty();
    }

    /**
     * Returns all transactions for the currently logged-in user, ordered by most recent first.
     * @return list of all transactions, empty list if none found
     * @throws RepositoryAccessException if a database error occurs
     */
    @Override
    public List<Transaction> findAll() {
        logger.debug("Fetching all transactions for user with id: {}", UserService.getLoggedInUser().getId());
        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement stmt = connection.prepareStatement(FIND_ALL_TRANSACTIONS_QUERY)) {
                stmt.setLong(1, UserService.getLoggedInUser().getId());

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Transaction transaction = extractTransactionFromResultSet(rs);
                    transactions.add(transaction);
                }
                logger.debug("Fetched {} transactions.", transactions.size());
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }

        return transactions;
    }

    /**
     * Extracts a {@link Transaction} object from the current row of a {@link ResultSet}.
     * @param rs the result set positioned at the row to extract
     * @return the extracted {@link Transaction}
     * @throws SQLException if a column cannot be read
     */
    private Transaction extractTransactionFromResultSet(ResultSet rs) throws SQLException {
        Long id = rs.getLong("ID");
        String description = rs.getString("DESCRIPTION");
        Integer amount = rs.getInt("AMOUNT");
        BigDecimal price = rs.getBigDecimal("PRICE");
        Category category = CategoryDatabaseRepository.getInstance().findById(rs.getLong("CATEGORY_ID"))
                .orElseThrow(() -> new DataIntegrityException("Category not found for transaction with id: " + id));
        LocalDate date = rs.getDate("TRANSACTION_DATE").toLocalDate();
        TransactionType transactionType = TransactionType.valueOf(rs.getString("TRANSACTION_TYPE"));
        User user = UserDatabaseRepository.getInstance().findById(rs.getLong("USER_ID"))
                .orElseThrow(() -> new DataIntegrityException("User not found for transaction with id: " + id));

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

    /**
     * Saves a new transaction to the database.
     * @param entity the transaction to save
     * @throws RepositoryAccessException if a database error occurs
     */
    @Override
    public void save(Transaction entity) {
        logger.debug("Saving new transaction for user with id: {}", entity.getUser().getId());
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
                logger.info("Transaction successfully saved for user with id: {}", entity.getUser().getId());
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }

    /**
     * Deletes a transaction from the database.
     * @param entity the transaction to delete
     * @throws RepositoryAccessException if a database error occurs
     */
    @Override
    public void delete(Transaction entity) {
        logger.debug("Deleting transaction with id: {}", entity.getId());
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement stmt = connection.prepareStatement(DELETE_TRANSACTION_QUERY)) {
                stmt.setLong(1, entity.getId());

                stmt.executeUpdate();
                logger.info("Transaction with id: {} successfully deleted.", entity.getId());
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }

    /**
     * Updates an existing transaction in the database.
     * @param entity the transaction with updated values
     * @throws RepositoryAccessException if a database error occurs
     */
    @Override
    public void update(Transaction entity) {
        logger.debug("Updating transaction with id: {}", entity.getId());
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
                logger.info("Transaction with id: {} successfully updated.", entity.getId());
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }

    /**
     * Returns transactions matching the given filter criteria.
     * Only filters on fields that are not null or empty.
     * @param transactionFilterForm the filter criteria to apply
     * @return list of matching transactions, empty list if none found
     * @throws RepositoryAccessException if a database error occurs
     */
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

        logger.debug("Executing filter query with {} filter(s).", params.size());
        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement stmt = connection.prepareStatement(filterQuery.toString())) {
                stmt.setLong(1, UserService.getLoggedInUser().getId());

                Integer parameterIndex = 2;
                for (Object param : params) {
                    stmt.setObject(parameterIndex++, param);
                }

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    transactions.add(extractTransactionFromResultSet(rs));
                }
                logger.debug("Filter returned {} transactions.", transactions.size());
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }

        return transactions;
    }

    /**
     * Deletes all transactions linked to a specific category.
     * @param category the category whose transactions should be deleted
     * @throws RepositoryAccessException if a database error occurs
     */
    public void deleteTransactionsByCategory(Category category) {
        logger.debug("Deleting all transactions for category with id: {}", category.getId());
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement stmt = connection.prepareStatement(DELETE_TRANSACTIONS_BY_CATEGORY_QUERY)) {
                stmt.setLong(1, category.getId());

                stmt.executeUpdate();
                logger.info("All transactions deleted for category with id: {}", category.getId());
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }

    /**
     * Returns the 5 most recent transactions for the logged-in user.
     * @return list of up to 5 latest transactions
     */
    public List<Transaction> findLatestTransactions() {
        return findAll().stream().limit(5).toList();
    }

    /**
     * Returns the total income for the logged-in user in the last 30 days.
     * @return total income as {@link BigDecimal}, or zero if none found
     * @throws RepositoryAccessException if a database error occurs
     */
    public BigDecimal findTotalIncome() {
        return findTotal(FIND_TOTAL_INCOME_LAST_30_DAYS_QUERY, "TOTAL_INCOME_LAST_30_DAYS");
    }

    /**
     * Returns the total expenses for the logged-in user in the last 30 days.
     * @return total expenses as {@link BigDecimal}, or zero if none found
     * @throws RepositoryAccessException if a database error occurs
     */
    public BigDecimal findTotalOutcome() {
        return findTotal(FIND_TOTAL_OUTCOME_LAST_30_DAYS_QUERY, "TOTAL_OUTCOME_LAST_30_DAYS");
    }

    /**
     * Runs a SUM query and returns the result.
     * Returns zero if there are no matching rows.
     * @param query the SQL query to run
     * @param columnName the column name of the SUM result
     * @return the total as {@link BigDecimal}, or zero if none found
     * @throws RepositoryAccessException if a database error occurs
     */
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

    /**
     * Deletes all transactions belonging to a specific user.
     * @param user the user whose transactions should be deleted
     * @throws RepositoryAccessException if a database error occurs
     */
    public void deleteTransactionsByUser(User user) {
        logger.debug("Deleting all transactions for user with id: {}", user.getId());
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement stmt = connection.prepareStatement(DELETE_TRANSACTIONS_BY_USER_QUERY)) {
                stmt.setLong(1, user.getId());

                stmt.executeUpdate();
                logger.info("All transactions deleted for user with id: {}", user.getId());
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }

    /**
     * Returns expense data grouped by category for the pie chart.
     * @return map of category name to total expense amount
     * @throws RepositoryAccessException if a database error occurs
     */
    public Map<String, BigDecimal> getPieChartData() {
        return getChartData(EXPENSES_PER_CATEGORY_QUERY, "CATEGORY_NAME", "EXPENSE");
    }

    /**
     * Returns income data grouped by month for the area chart.
     * @return map of month name to total income amount, ordered by month
     * @throws RepositoryAccessException if a database error occurs
     */
    public Map<String, BigDecimal> getAreaChartData() {
        return getChartData(INCOME_PER_MONTH_QUERY, "TRANSACTION_MONTH", "INCOME");
    }

    /**
     * Runs a query and returns the result as a map of label to value.
     * Used internally for chart data queries.
     * @param query the SQL query to run
     * @param keyColumn the column to use as the map key
     * @param valueColumn the column to use as the map value
     * @return linked map preserving insertion order
     * @throws RepositoryAccessException if a database error occurs
     */
    private Map<String, BigDecimal> getChartData(String query, String keyColumn, String valueColumn) {
        Map<String, BigDecimal> data = new LinkedHashMap<>();

        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setLong(1, UserService.getLoggedInUser().getId());

                ResultSet rs = stmt.executeQuery();

                while(rs.next()) {
                    data.put(rs.getString(keyColumn), rs.getBigDecimal(valueColumn));
                }
                logger.debug("Chart data loaded with {} entries.", data.size());
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }

        return data;
    }
}