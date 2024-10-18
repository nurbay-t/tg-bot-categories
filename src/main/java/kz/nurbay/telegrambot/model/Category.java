package kz.nurbay.telegrambot.model;

import jakarta.persistence.*;

/**
 * Represents a category in the system.
 * Categories are hierarchical, meaning each category can have a parent category.
 * Categories are linked to a specific user, and the parent-child relationship is represented via the 'parent' field.
 */
@Entity
@Table(name = "categories")
public class Category {
    /**
     * Automatically generated primary key and identifier for the category.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the category.
     * This is a required field and must not be null.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * The parent category of this category.
     * This establishes the parent-child relationship within the categories.
     * If this field is null, it means the category is a root category.
     */
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    /**
     * The user to whom this category belongs.
     * Every category is associated with a user, and all categories of a user form a hierarchy.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

