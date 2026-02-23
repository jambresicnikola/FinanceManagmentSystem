package hr.java.financemanagementsystem.database;

import hr.java.financemanagementsystem.exception.RepositoryAccessException;
import hr.java.financemanagementsystem.model.Transaction;
import hr.java.financemanagementsystem.util.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TransactionDatabaseRepository extends AbstractRepository<Transaction> {
    private static final String SAVE_TRANSACTION_QUERY =
            """
            INSERT INTO TRANSACTIONS
            (DESCRIPTION, AMOUNT, PRICE, CATEGORY_ID, TRANSACTION_DATE, TRANSACTION_TYPE, USER_ID) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
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
        return List.of();
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

    }

    @Override
    public void update(Transaction entity) {

    }
}
