package hr.java.financemanagementsystem.database;

import hr.java.financemanagementsystem.exception.RepositoryAccessException;
import hr.java.financemanagementsystem.model.User;
import hr.java.financemanagementsystem.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link User} data in the database.
 * Follows the singleton pattern — use {@link #getInstance()} to get the instance.
 */
public class UserDatabaseRepository extends AbstractRepository<User> {
    private static final Logger logger = LoggerFactory.getLogger(UserDatabaseRepository.class);

    private static final String FIND_BY_ID_QUERY = "SELECT ID, FIRST_NAME, LAST_NAME, USERNAME, PASSWORD FROM USERS WHERE ID=?";
    private static final String FIND_ALL_QUERY = "SELECT ID, FIRST_NAME, LAST_NAME, USERNAME, PASSWORD FROM USERS";
    private static final String SAVE_QUERY = "INSERT INTO USERS (FIRST_NAME, LAST_NAME, USERNAME, PASSWORD) VALUES (?,?,?,?)";
    private static final String FIND_BY_USERNAME_QUERY = "SELECT ID, FIRST_NAME, LAST_NAME, USERNAME, PASSWORD FROM USERS WHERE USERNAME=?";
    private static final String UPDATE_USER_QUERY = "UPDATE USERS SET FIRST_NAME=?, LAST_NAME=?, USERNAME=?, PASSWORD=? WHERE ID=?";
    private static final String DELETE_USER_QUERY = "DELETE FROM USERS WHERE ID=?";

    private static UserDatabaseRepository instance;

    private UserDatabaseRepository() {}

    /**
     * Returns the singleton instance of this repository.
     * @return the single {@link UserDatabaseRepository} instance
     */
    public static UserDatabaseRepository getInstance() {
        if (Optional.ofNullable(instance).isEmpty()) {
            instance = new UserDatabaseRepository();
        }

        return instance;
    }

    /**
     * Finds a user by their id.
     * @param id the id of the user to find
     * @return an {@link Optional} containing the user if found, or empty if not
     * @throws RepositoryAccessException if a database error occurs
     */
    @Override
    public Optional<User> findById(Long id) {
        logger.debug("Looking up user by id: {}", id);
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_QUERY)) {
                statement.setLong(1, id);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return Optional.of(extractUserFromResultSet(resultSet));
                }
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }

        return Optional.empty();
    }

    /**
     * Returns all users from the database.
     * @return list of all users, empty list if none found
     * @throws RepositoryAccessException if a database error occurs
     */
    @Override
    public List<User> findAll() {
        logger.debug("Fetching all users.");
        List<User> users = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(FIND_ALL_QUERY);

                while (resultSet.next()) {
                    User user = extractUserFromResultSet(resultSet);
                    users.add(user);
                }

                logger.debug("Fetched {} users.", users.size());
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }

        return users;
    }

    /**
     * Extracts a {@link User} object from the current row of a {@link ResultSet}.
     * @param resultSet the result set positioned at the row to extract
     * @return the extracted {@link User}
     * @throws SQLException if a column cannot be read
     */
    private static User extractUserFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("ID");
        String firstName = resultSet.getString("FIRST_NAME");
        String lastName = resultSet.getString("LAST_NAME");
        String username = resultSet.getString("USERNAME");
        String password = resultSet.getString("PASSWORD");

        return new User.Builder()
                .withId(id)
                .withFirstName(firstName)
                .withLastName(lastName)
                .withUsername(username)
                .withPassword(password)
                .build();
    }

    /**
     * Saves a new user to the database.
     * @param entity the user to save
     * @throws RepositoryAccessException if a database error occurs
     */
    @Override
    public void save(User entity) {
        logger.debug("Saving new user with username: '{}'", entity.getUsername());
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement statement = connection.prepareStatement(SAVE_QUERY)) {
                statement.setString(1, entity.getFirstName());
                statement.setString(2, entity.getLastName());
                statement.setString(3, entity.getUsername());
                statement.setString(4, entity.getPassword());

                statement.executeUpdate();

                logger.info("User '{}' successfully saved.", entity.getUsername());
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }

    /**
     * Deletes a user from the database.
     * @param entity the user to delete
     * @throws RepositoryAccessException if a database error occurs
     */
    @Override
    public void delete(User entity) {
        logger.debug("Deleting user with id: {}", entity.getId());
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement stmt = connection.prepareStatement(DELETE_USER_QUERY)) {
                stmt.setLong(1, entity.getId());

                stmt.executeUpdate();

                logger.info("User with id: {} successfully deleted.", entity.getId());
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }

    /**
     * Updates an existing user in the database.
     * @param entity the user with updated values
     * @throws RepositoryAccessException if a database error occurs
     */
    @Override
    public void update(User entity) {
        logger.debug("Updating user with id: {}", entity.getId());
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement stmt = connection.prepareStatement(UPDATE_USER_QUERY)) {
                stmt.setString(1, entity.getFirstName());
                stmt.setString(2, entity.getLastName());
                stmt.setString(3, entity.getUsername());
                stmt.setString(4, entity.getPassword());

                stmt.setLong(5, entity.getId());

                stmt.executeUpdate();

                logger.info("User with id: {} successfully updated.", entity.getId());
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }

    /**
     * Finds a user by their username.
     * @param username the username to search for
     * @return an {@link Optional} containing the user if found, or empty if not
     * @throws RepositoryAccessException if a database error occurs
     */
    public Optional<User> findUserByUsername(String username) {
        logger.debug("Looking up user by username: '{}'", username);
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement statement = connection.prepareStatement(FIND_BY_USERNAME_QUERY)) {
                statement.setString(1, username);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return Optional.of(extractUserFromResultSet(resultSet));
                }
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }

        return Optional.empty();
    }
}
