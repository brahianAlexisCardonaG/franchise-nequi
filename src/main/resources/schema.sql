-- Table: franchise
CREATE TABLE IF NOT EXISTS franchise (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Table: branch
CREATE TABLE IF NOT EXISTS branch (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    franchise_id INT NOT NULL,
    CONSTRAINT fk_franchise
        FOREIGN KEY (franchise_id)
        REFERENCES franchise(id)
        ON DELETE CASCADE
);

-- Table: product
CREATE TABLE IF NOT EXISTS product (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    branch_id INT NOT NULL,
    CONSTRAINT fk_branch
        FOREIGN KEY (branch_id)
        REFERENCES branch(id)
        ON DELETE CASCADE
);