package com.sneha.registration.dao;

import com.sneha.registration.model.User;
import com.sneha.registration.util.DBConnection;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UserDAO.java  (DAO = Data Access Object)
 *
 * Handles all database operations for the User table.
 * This is the ONLY class that talks to the database.
 * Servlets call this class — they never touch SQL directly.
 *
 * Design pattern: DAO Pattern
 *   Servlet → UserDAO → Database
 *
 * Current (Project 2.0):
 *   - registerUser() returns true (simulates success, no DB write)
 *   - emailExists() returns false (no DB to check)
 *
 * Project 2.1 (AWS RDS):
 *   - Set DB_ENABLED = true in DBConnection.java
 *   - Update RDS endpoint in DBConnection.java
 *   - Run db/create_tables.sql on your RDS instance
 *   - All methods below will work automatically
 *
 * SQL Table this maps to (see db/create_tables.sql):
 *   CREATE TABLE users (
 *     id          INT AUTO_INCREMENT PRIMARY KEY,
 *     name        VARCHAR(100) NOT NULL,
 *     surname     VARCHAR(100) NOT NULL,
 *     email       VARCHAR(150) NOT NULL UNIQUE,
 *     password    VARCHAR(255) NOT NULL,
 *     aadhar      VARCHAR(12)  NOT NULL UNIQUE,
 *     created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
 *   );
 */
public class UserDAO {

    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());

    // ------------------------------------------------
    // SQL Queries
    // ------------------------------------------------

    private static final String INSERT_USER_SQL =
        "INSERT INTO users (name, surname, email, password, aadhar) VALUES (?, ?, ?, ?, ?)";

    private static final String CHECK_EMAIL_SQL =
        "SELECT COUNT(*) FROM users WHERE email = ?";

    private static final String CHECK_AADHAR_SQL =
        "SELECT COUNT(*) FROM users WHERE aadhar = ?";

    private static final String GET_USER_BY_EMAIL =
        "SELECT * FROM users WHERE email = ?";

    // ------------------------------------------------
    // registerUser()
    // ------------------------------------------------

    /**
     * Saves a new user to the database.
     *
     * @param user  User object with all fields set
     * @return true if saved successfully, false if failed
     *
     * Project 2.0: Returns true always (no DB, simulates success)
     * Project 2.1: Actually inserts into AWS RDS
     */
    public boolean registerUser(User user) {

        Connection connection = DBConnection.getConnection();

        // Project 2.0: DB not connected — simulate success
        if (connection == null) {
            logger.info("DB not connected. Simulating successful registration for: " + user.getEmail());
            return true;
        }

        // Project 2.1+: Actually save to database
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_USER_SQL)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getSurname());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());  // TODO 2.2: hash password with BCrypt
            stmt.setString(5, user.getAadhar());

            int rowsInserted = stmt.executeUpdate();
            logger.info("User registered successfully: " + user.getEmail());
            return rowsInserted > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            // Email or Aadhar already exists (UNIQUE constraint)
            logger.warning("Duplicate entry — email or aadhar already registered: " + e.getMessage());
            return false;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error registering user: " + e.getMessage(), e);
            return false;

        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    // ------------------------------------------------
    // emailExists()
    // ------------------------------------------------

    /**
     * Checks if an email is already registered.
     *
     * @param email  Email to check
     * @return true if email already exists in DB
     *
     * Project 2.0: Returns false (no DB to check)
     * Project 2.1: Checks actual RDS database
     */
    public boolean emailExists(String email) {

        Connection connection = DBConnection.getConnection();

        if (connection == null) {
            logger.info("DB not connected. Skipping email duplicate check.");
            return false;
        }

        try (PreparedStatement stmt = connection.prepareStatement(CHECK_EMAIL_SQL)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking email existence: " + e.getMessage(), e);
        } finally {
            DBConnection.closeConnection(connection);
        }

        return false;
    }

    // ------------------------------------------------
    // aadharExists()
    // ------------------------------------------------

    /**
     * Checks if an Aadhar number is already registered.
     *
     * @param aadhar  12-digit Aadhar number to check
     * @return true if already exists
     */
    public boolean aadharExists(String aadhar) {

        Connection connection = DBConnection.getConnection();

        if (connection == null) {
            logger.info("DB not connected. Skipping aadhar duplicate check.");
            return false;
        }

        try (PreparedStatement stmt = connection.prepareStatement(CHECK_AADHAR_SQL)) {

            stmt.setString(1, aadhar);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking aadhar existence: " + e.getMessage(), e);
        } finally {
            DBConnection.closeConnection(connection);
        }

        return false;
    }
}
