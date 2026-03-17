package hr.java.financemanagementsystem.model;

/**
 * Base class for all model entities.
 * Every entity in the app has a unique database id.
 */
public abstract class Entity {
    private Long id;

    protected Entity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
