-- ============================================================
--   create_tables.sql
--   FormFillApp — Database Setup Script
--
--   Run this on your AWS RDS MySQL instance for Project 2.1
--
--   How to run:
--   mysql -h <your-rds-endpoint> -u admin -p < db/create_tables.sql
-- ============================================================

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS registrationdb
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE registrationdb;

-- ============================================================
--   users table
--   Stores all registered users from the FormFillApp
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
  id          INT           NOT NULL AUTO_INCREMENT,
  name        VARCHAR(100)  NOT NULL,
  surname     VARCHAR(100)  NOT NULL,
  email       VARCHAR(150)  NOT NULL,
  password    VARCHAR(255)  NOT NULL,   -- TODO v2.2: store BCrypt hash, not plain text
  aadhar      VARCHAR(12)   NOT NULL,
  created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,

  -- Primary Key
  PRIMARY KEY (id),

  -- Unique constraints (no duplicate email or aadhar)
  UNIQUE KEY uk_email  (email),
  UNIQUE KEY uk_aadhar (aadhar)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Registered users from FormFillApp CI/CD project';


-- ============================================================
--   Verify table was created correctly
-- ============================================================
DESCRIBE users;

-- ============================================================
--   Optional: Insert a test record to verify DB is working
--   (Delete this row after testing!)
-- ============================================================
-- INSERT INTO users (name, surname, email, password, aadhar)
-- VALUES ('Test', 'User', 'test@example.com', 'testpass123', '123456789012');
-- SELECT * FROM users;
-- DELETE FROM users WHERE email = 'test@example.com';
