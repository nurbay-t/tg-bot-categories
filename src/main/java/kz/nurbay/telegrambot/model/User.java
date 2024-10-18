package kz.nurbay.telegrambot.model;

import jakarta.persistence.*;

import java.util.Set;

/**
 * Represents a Telegram user in the system.
 * The user's ID corresponds to their Telegram user ID.
 * Each user can have multiple categories associated with them.
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * Telegram user ID.
     * This ID is used as the primary key for the user in the system.
     */
    @Id
    private Long id;

    /**
     * The set of categories associated with the user.
     * Each user can manage multiple categories, which form a single tree structure.
     * Categories are deleted if the user is deleted.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Category> categories;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }
}