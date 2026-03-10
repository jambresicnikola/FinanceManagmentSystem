package hr.java.financemanagementsystem.database;

import hr.java.financemanagementsystem.exception.RepositoryAccessException;
import hr.java.financemanagementsystem.model.Category;
import hr.java.financemanagementsystem.model.User;
import hr.java.financemanagementsystem.service.UserService;
import hr.java.financemanagementsystem.util.DatabaseConnection;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryDatabaseRepository extends AbstractRepository<Category> {
    private static final String FIND_CATEGORY_BY_ID_QUERY = "SELECT ID, NAME, USER_ID FROM CATEGORIES WHERE ID=?";
    private static final String FIND_CATEGORY_BY_NAME_QUERY = "SELECT ID, NAME, USER_ID FROM CATEGORIES WHERE NAME=? AND USER_ID=?";
    private static final String SAVE_CATEGORY_QUERY = "INSERT INTO CATEGORIES (NAME, USER_ID) VALUES (?, ?)";
    private static final String FIND_ALL_CATEGORIES_QUERY = "SELECT ID, NAME, USER_ID FROM CATEGORIES WHERE USER_ID=?";
    private static final String UPDATE_CATEGORY_QUERY = "UPDATE CATEGORIES SET NAME=? WHERE ID=?";
    private static final String DELETE_CATEGORY_QUERY = "DELETE FROM CATEGORIES WHERE ID=?";
    private static final String DELETE_CATEGORIES_BY_USER = "DELETE FROM CATEGORIES WHERE USER_ID=?";

    private static CategoryDatabaseRepository instance;

    private CategoryDatabaseRepository() {}

    public static CategoryDatabaseRepository getInstance() {
        if (Optional.ofNullable(instance).isEmpty()) {
            instance = new CategoryDatabaseRepository();
        }

        return instance;
    }

    @Override
    public Optional<Category> findById(Long id) {
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_CATEGORY_BY_ID_QUERY)) {
                preparedStatement.setLong(1, id);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return Optional.of(extractCategoryFromResultSet(resultSet));
                }
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }

        return Optional.empty();
    }

    @Override
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement statement = connection.prepareStatement(FIND_ALL_CATEGORIES_QUERY)) {
                statement.setLong(1, UserService.getLoggedInUser().getId());

                ResultSet rs = statement.executeQuery();

                while (rs.next()) {
                    Category category = extractCategoryFromResultSet(rs);
                    categories.add(category);
                }
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }

        return categories;
    }

    private Category extractCategoryFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("ID");
        String name = resultSet.getString("NAME");
        Long userId = resultSet.getLong("USER_ID");

        Optional<User> user = UserDatabaseRepository.getInstance().findById(userId);

        return new Category.Builder()
                .withId(id)
                .withName(name)
                .withUser(user.get())
                .build();
    }

    @Override
    public void save(Category entity) {
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement statement = connection.prepareStatement(SAVE_CATEGORY_QUERY)) {
                statement.setString(1, entity.getName());
                statement.setLong(2, entity.getUser().getId());

                statement.executeUpdate();
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }

    @Override
    public void delete(Category entity) {
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement statement = connection.prepareStatement(DELETE_CATEGORY_QUERY)) {
                statement.setLong(1, entity.getId());

                statement.executeUpdate();
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }

    @Override
    public void update(Category entity) {
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement statement = connection.prepareStatement(UPDATE_CATEGORY_QUERY)) {
                statement.setString(1, entity.getName());
                statement.setLong(2, entity.getId());

                statement.executeUpdate();
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }

    public Optional<Category> findCategoryByName(String categoryName, User user) {
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement stmt = connection.prepareStatement(FIND_CATEGORY_BY_NAME_QUERY)) {
                stmt.setString(1, categoryName);
                stmt.setLong(2, user.getId());

                ResultSet resultSet = stmt.executeQuery();

                if (resultSet.next()) {
                    return Optional.of(extractCategoryFromResultSet(resultSet));
                }
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }

        return Optional.empty();
    }

    public void deleteCategoriesByUser(User loggedInUser) {
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement statement = connection.prepareStatement(DELETE_CATEGORIES_BY_USER)) {
                statement.setLong(1, loggedInUser.getId());

                statement.executeUpdate();
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }
}
