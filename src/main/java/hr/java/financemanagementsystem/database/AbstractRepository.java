package hr.java.financemanagementsystem.database;

import java.util.List;
import java.util.Optional;

/**
 * Abstract base class for all repository classes.
 * Defines the standard set of database operations every repository must implement.
 * @param <T> the type of entity this repository manages
 */
public abstract class AbstractRepository <T> {
    /**
     * Finds a single entity by its id.
     * @param id the id of the entity to find
     * @return an {@link Optional} containing the entity if found, or empty if not
     */
    public abstract Optional<T> findById(Long id);

    /**
     * Returns all entities of this type from the database.
     * @return list of all entities, empty list if none found
     */
    public abstract List<T> findAll();

    /**
     * Saves a new entity to the database.
     * @param entity the entity to save
     */
    public abstract void save(T entity);

    /**
     * Deletes an existing entity from the database.
     * @param entity the entity to delete
     */
    public abstract void delete(T entity);

    /**
     * Updates an existing entity in the database.
     * @param entity the entity to update
     */
    public abstract void update(T entity);
}