package com.sneha.registration.model;

/**
 * User.java
 * 
 * Model class representing a registered user.
 * Maps directly to the 'users' table in MySQL database.
 * 
 * Current (Project 2):   Object is created but not saved to DB
 * Project 2.1 (AWS RDS): This object will be saved to RDS MySQL
 */
public class User {

    // Maps to: id INT AUTO_INCREMENT PRIMARY KEY
    private int id;

    // Maps to: name VARCHAR(100) NOT NULL
    private String name;

    // Maps to: surname VARCHAR(100) NOT NULL
    private String surname;

    // Maps to: email VARCHAR(150) NOT NULL UNIQUE
    private String email;

    // Maps to: password VARCHAR(255) NOT NULL  (will be hashed in v2.1)
    private String password;

    // Maps to: aadhar VARCHAR(12) NOT NULL UNIQUE
    private String aadhar;

    // Maps to: created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    private String createdAt;

    // ------------------------------------------------
    // Constructors
    // ------------------------------------------------

    public User() {}

    public User(String name, String surname, String email, String password, String aadhar) {
        this.name     = name;
        this.surname  = surname;
        this.email    = email;
        this.password = password;
        this.aadhar   = aadhar;
    }

    // ------------------------------------------------
    // Getters and Setters
    // ------------------------------------------------

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAadhar() { return aadhar; }
    public void setAadhar(String aadhar) { this.aadhar = aadhar; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", surname='" + surname + '\'' +
               ", email='" + email + '\'' +
               ", aadhar='" + aadhar + '\'' +
               '}';
    }
}
