CREATE TABLE users (
    id BIGINT PRIMARY KEY  -- Telegram userId
);

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    parent_id BIGINT,  -- Reference to the parent category
    user_id BIGINT NOT NULL,  -- Reference to the user

    -- Foreign key for parent_id, references the same categories table
    CONSTRAINT fk_parent_category FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE CASCADE,

    -- Foreign key for user_id, references the users table
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);