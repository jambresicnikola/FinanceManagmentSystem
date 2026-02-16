package hr.java.financemanagementsystem.database;

import java.util.List;
import java.util.Optional;

public abstract class AbstractRepository <T> {
    public abstract Optional<T> findById(Long id);
    public abstract List<T> findAll();
    public abstract void save(T entity);
    public abstract void delete(T entity);
    public abstract void update(T entity);
}