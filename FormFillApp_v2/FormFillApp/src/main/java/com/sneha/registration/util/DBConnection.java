package com.sneha.registration.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DBConnection.java
 *
 * Manages MySQL database connection.
 *
 * HOW IT WORKS ACROSS PROJECT VERSIONS:
 * -------------------------------------------------------
 * Project 2.0 (Current):
 *   - DB_ENABLED = false
 *   - App runs fine with NO database
 *   - Registration form shows success page but data not saved
 *   - No errors thrown, pipeline works end to end
 *
 * Project 2.1 (AWS RDS):
 *   - Set DB_ENABLED = true
 *   - Update DB_URL to your RDS endpoint
 *   - Update DB_USER and DB_PASSWORD
 *   - Data will be saved to AWS RDS MySQL
 *
 * Project 2.2 (Production hardening):
 *   - Move credentials to Jenkins Secrets / AWS Secrets Manager
 *   - Read from environment variables, not hardcoded here
 * -------------------------------------------------------
 *
 * AWS RDS Endpoint format:
 *   jdbc:mysql://<rds-endpoint>:3306/registrationdb
 *   Example: jdbc:mysql://mydb.abc123.ap-south-1.rds.amazonaws.com:3306/registrationdb
 */
public class DBConnection {

    private static final Logger logger = Logger.getLogger(DBConnection.class.getName());

    // ------------------------------------------------
    // CONFIGURATION - Change these for Project 2.1
    // ------------------------------------------------

    /**
     * Set to true when AWS RDS is configured.
     * Keep false for Project 2.0 (no database needed).
     */
    private static final boolean DB_ENABLED = false;

    /**
     * Database URL.
     * Project 2.0: Not used (DB_ENABLED = false)
     * Project 2.1: Replace with your RDS endpoint
     *
     * Format: jdbc:mysql://<host>:<port>/<database>
     *
     * TODO Project 2.1: Replace placeholder with actual RDS endpoint
     */
    private static final String DB_URL =
        "jdbc:mysql://YOUR-RDS-ENDPOINT:3306/registrationdb?useSSL=true&serverTimezone=UTC";

    /**
     * TODO Project 2.1: Replace with your RDS master username
     * TODO Project 2.2: Read from environment variable:
     *   System.getenv("DB_USERNAME")
     */
    private static final String DB_USER = "admin";

    /**
     * TODO Project 2.1: Replace with your RDS password
     * TODO Project 2.2: Read from environment variable:
     *   System.getenv("DB_PASSWORD")
     *   Or use AWS Secrets Manager
     */
    private static final String DB_PASSWORD = "your-rds-password";

    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    // ------------------------------------------------
    // getConnection() - returns Connection or null
    // ------------------------------------------------

    /**
     * Returns a database connection.
     *
     * Returns null if:
     *   - DB_ENABLED is false (Project 2.0)
     *   - Connection fails (logs error, doesn't crash app)
     *
     * Callers must check for null before using connection.
     */
    public static Connection getConnection() {

        if (!DB_ENABLED) {
            logger.info("DB_ENABLED is false. Running without database (Project 2.0 mode).");
            return null;
        }

        try {
            Class.forName(DRIVER_CLASS);
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            logger.info("Database connection established successfully.");
            return connection;

        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "MySQL JDBC Driver not found: " + e.getMessage(), e);
            return null;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection failed: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Safely closes a database connection.
     * Always call this in a finally block after using a connection.
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Database connection closed.");
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error closing database connection: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Checks if database is reachable.
     * Useful for health check endpoints.
     */
    public static boolean isDatabaseAvailable() {
        if (!DB_ENABLED) return false;
        Connection conn = getConnection();
        if (conn != null) {
            closeConnection(conn);
            return true;
        }
        return false;
    }
}
