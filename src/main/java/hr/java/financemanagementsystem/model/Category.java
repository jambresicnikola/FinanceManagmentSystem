package hr.java.financemanagementsystem.model;

public class Category extends Entity {
    private String name;
    private User user;

    public Category(Builder builder) {
        super(builder.id);
        this.name = builder.name;
        this.user = builder.user;
    }

    public static class Builder {
        private Long id;
        private String name;
        private User user;

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withUser(User user) {
            this.user = user;
            return this;
        }

        public Category build() {
            return new Category(this);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
