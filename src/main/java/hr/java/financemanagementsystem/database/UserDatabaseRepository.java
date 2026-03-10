package hr.java.financemanagementsystem.database;

import hr.java.financemanagementsystem.exception.RepositoryAccessException;
import hr.java.financemanagementsystem.model.User;
import hr.java.financemanagementsystem.util.DatabaseConnection;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDatabaseRepository extends AbstractRepository<User> {
    private static final String FIND_BY_ID_QUERY = "SELECT ID, FIRST_NAME, LAST_NAME, USERNAME, PASSWORD FROM USERS WHERE ID=?";
    private static final String FIND_ALL_QUERY = "SELECT ID, FIRST_NAME, LAST_NAME, USERNAME, PASSWORD FROM USERS";
    private static final String SAVE_QUERY = "INSERT INTO USERS (FIRST_NAME, LAST_NAME, USERNAME, PASSWORD) VALUES (?,?,?,?)";
    private static final String FIND_BY_USERNAME_QUERY = "SELECT ID, FIRST_NAME, LAST_NAME, USERNAME, PASSWORD FROM USERS WHERE USERNAME=?";
    private static final String UPDATE_USER_QUERY = "UPDATE USERS SET FIRST_NAME=?, LAST_NAME=?, USERNAME=?, PASSWORD=? WHERE ID=?";
    private static final String DELETE_USER_QUERY = "DELETE FROM USERS WHERE ID=?";

    private static UserDatabaseRepository instance;

    private UserDatabaseRepository() {}

    public static UserDatabaseRepository getInstance() {
        if (Optional.ofNullable(instance).isEmpty()) {
            instance = new UserDatabaseRepository();
        }

        return instance;
    }

    @Override
    public Optional<User> findById(Long id) {
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

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(FIND_ALL_QUERY);

                while (resultSet.next()) {
                    User user = extractUserFromResultSet(resultSet);
                    users.add(user);
                }
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }

        return users;
    }

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

    @Override
    public void save(User entity) {
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement statement = connection.prepareStatement(SAVE_QUERY)) {
                statement.setString(1, entity.getFirstName());
                statement.setString(2, entity.getLastName());
                statement.setString(3, entity.getUsername());
                statement.setString(4, entity.getPassword());

                statement.executeUpdate();
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }

    @Override
    public void delete(User entity) {
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement stmt = connection.prepareStatement(DELETE_USER_QUERY)) {
                stmt.setLong(1, entity.getId());

                stmt.executeUpdate();
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }

    @Override
    public void update(User entity) {
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement stmt = connection.prepareStatement(UPDATE_USER_QUERY)) {
                stmt.setString(1, entity.getFirstName());
                stmt.setString(2, entity.getLastName());
                stmt.setString(3, entity.getUsername());
                stmt.setString(4, entity.getPassword());

                stmt.setLong(5, entity.getId());

                stmt.executeUpdate();
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }

    public Optional<User> findUserByUsername(String username) {
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
