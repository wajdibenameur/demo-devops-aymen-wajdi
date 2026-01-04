-- Disable foreign key constraints during creation
SET REFERENTIAL_INTEGRITY FALSE;

-- Drop tables in correct order (if they exist)
DROP TABLE IF EXISTS commande_products;
DROP TABLE IF EXISTS commande;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS app_user;

-- Enable foreign key constraints
SET REFERENTIAL_INTEGRITY TRUE;

-- Create tables in correct order
CREATE TABLE app_user (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          first_name VARCHAR(255) NOT NULL,
                          last_name VARCHAR(255) NOT NULL,
                          email VARCHAR(255) UNIQUE NOT NULL,
                          phone_number VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE product (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name_product VARCHAR(255) NOT NULL,
                         price DOUBLE PRECISION NOT NULL,
                         quantity INTEGER NOT NULL
);

CREATE TABLE commande (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          date_commande TIMESTAMP,
                          status VARCHAR(50),
                          price_totale DOUBLE PRECISION,
                          user_id BIGINT NOT NULL,
                          FOREIGN KEY (user_id) REFERENCES app_user(id)
);

CREATE TABLE commande_products (
                                   commande_id BIGINT NOT NULL,
                                   product_id BIGINT NOT NULL,
                                   PRIMARY KEY (commande_id, product_id),
                                   FOREIGN KEY (commande_id) REFERENCES commande(id) ON DELETE CASCADE,
                                   FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);