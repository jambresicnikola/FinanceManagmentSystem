package hr.java.financemanagementsystem.database;

import hr.java.financemanagementsystem.exception.DataIntegrityException;
import hr.java.financemanagementsystem.exception.RepositoryAccessException;
import hr.java.financemanagementsystem.model.Category;
import hr.java.financemanagementsystem.model.User;
import hr.java.financemanagementsystem.service.UserService;
import hr.java.financemanagementsystem.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link Category} data in the database.
 * Follows the singleton pattern — use {@link #getInstance()} to get the instance.
 */
public class CategoryDatabaseRepository extends AbstractRepository<Category> {
    private static final Logger logger = LoggerFactory.getLogger(CategoryDatabaseRepository.class);

    private static final String FIND_CATEGORY_BY_ID_QUERY = "SELECT ID, NAME, USER_ID FROM CATEGORIES WHERE ID=?";
    private static final String FIND_CATEGORY_BY_NAME_QUERY = "SELECT ID, NAME, USER_ID FROM CATEGORIES WHERE NAME=? AND USER_ID=?";
    private static final String SAVE_CATEGORY_QUERY = "INSERT INTO CATEGORIES (NAME, USER_ID) VALUES (?, ?)";
    private static final String FIND_ALL_CATEGORIES_QUERY = "SELECT ID, NAME, USER_ID FROM CATEGORIES WHERE USER_ID=?";
    private static final String UPDATE_CATEGORY_QUERY = "UPDATE CATEGORIES SET NAME=? WHERE ID=?";
    private static final String DELETE_CATEGORY_QUERY = "DELETE FROM CATEGORIES WHERE ID=?";
    private static final String DELETE_CATEGORIES_BY_USER = "DELETE FROM CATEGORIES WHERE USER_ID=?";

    private static CategoryDatabaseRepository instance;

    private CategoryDatabaseRepository() {}

    /**
     * Returns the singleton instance of this repository.
     * @return the single {@link CategoryDatabaseRepository} instance
     */
    public static CategoryDatabaseRepository getInstance() {
        if (Optional.ofNullable(instance).isEmpty()) {
            instance = new CategoryDatabaseRepository();
        }

        return instance;
    }

    /**
     * Finds a category by its id.
     * @param id the id of the category to find
     * @return an {@link Optional} containing the category if found, or empty if not
     * @throws RepositoryAccessException if a database error occurs
     */
    @Override
    public Optional<Category> findById(Long id) {
        logger.debug("Looking up category by id: {}", id);
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

    /**
     * Returns all categories for the currently logged-in user.
     * @return list of all categories, empty list if none found
     * @throws RepositoryAccessException if a database error occurs
     */
    @Override
    public List<Category> findAll() {
        logger.debug("Fetching all categories for user with id: {}", UserService.getLoggedInUser().getId());

        List<Category> categories = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement statement = connection.prepareStatement(FIND_ALL_CATEGORIES_QUERY)) {
                statement.setLong(1, UserService.getLoggedInUser().getId());

                ResultSet rs = statement.executeQuery();

                while (rs.next()) {
                    Category category = extractCategoryFromResultSet(rs);
                    categories.add(category);
                }
                logger.debug("Fetched {} categories.", categories.size());
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }

        return categories;
    }

    /**
     * Extracts a {@link Category} object from the current row of a {@link ResultSet}.
     * @param resultSet the result set positioned at the row to extract
     * @return the extracted {@link Category}
     * @throws SQLException if a column cannot be read
     */
    private Category extractCategoryFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("ID");
        String name = resultSet.getString("NAME");
        Long userId = resultSet.getLong("USER_ID");

        User user = UserDatabaseRepository.getInstance().findById(userId)
                .orElseThrow(() -> new DataIntegrityException(
                "User with id: " + userId + " not found for category: " + name));

        return new Category.Builder()
                .withId(id)
                .withName(name)
                .withUser(user)
                .build();
    }

    /**
     * Saves a new category to the database.
     * @param entity the category to save
     * @throws RepositoryAccessException if a database error occurs
     */
    @Override
    public void save(Category entity) {
        logger.debug("Saving new category with name: '{}'", entity.getName());
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement statement = connection.prepareStatement(SAVE_CATEGORY_QUERY)) {
                statement.setString(1, entity.getName());
                statement.setLong(2, entity.getUser().getId());

                statement.executeUpdate();
                logger.info("Category '{}' successfully saved.", entity.getName());
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }

    /**
     * Deletes a category from the database.
     * @param entity the category to delete
     * @throws RepositoryAccessException if a database error occurs
     */
    @Override
    public void delete(Category entity) {
        logger.debug("Deleting category with id: {}", entity.getId());
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement statement = connection.prepareStatement(DELETE_CATEGORY_QUERY)) {
                statement.setLong(1, entity.getId());

                statement.executeUpdate();
                logger.info("Category '{}' successfully deleted.", entity.getName());
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }

    /**
     * Updates an existing category in the database.
     * @param entity the category with updated values
     * @throws RepositoryAccessException if a database error occurs
     */
    @Override
    public void update(Category entity) {
        logger.debug("Updating category with id: {}", entity.getId());
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement statement = connection.prepareStatement(UPDATE_CATEGORY_QUERY)) {
                statement.setString(1, entity.getName());
                statement.setLong(2, entity.getId());

                statement.executeUpdate();
                logger.info("Category with id: {} successfully updated to '{}'.", entity.getId(), entity.getName());
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }

    /**
     * Finds a category by name for a specific user.
     * @param categoryName the name of the category to find
     * @param user the user who owns the category
     * @return an {@link Optional} containing the category if found, or empty if not
     * @throws RepositoryAccessException if a database error occurs
     */
    public Optional<Category> findCategoryByName(String categoryName, User user) {
        logger.debug("Looking up category by name: '{}' for user with id: {}", categoryName, user.getId());
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

    /**
     * Deletes all categories belonging to a specific user.
     * @param user the user whose categories should be deleted
     * @throws RepositoryAccessException if a database error occurs
     */
    public void deleteCategoriesByUser(User user) {
        logger.debug("Deleting all categories for user with id: {}", user.getId());
        try (Connection connection = DatabaseConnection.connectToDatabase()) {
            try (PreparedStatement statement = connection.prepareStatement(DELETE_CATEGORIES_BY_USER)) {
                statement.setLong(1, user.getId());

                statement.executeUpdate();
                logger.info("All categories deleted for user with id: {}", user.getId());
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }
}
