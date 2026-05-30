package com.sneha.registration.servlet;

import com.sneha.registration.dao.UserDAO;
import com.sneha.registration.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * RegistrationServlet.java
 *
 * Handles the user registration form submission.
 * URL: /register  (POST)
 *
 * Flow:
 *   1. User fills form → clicks Register → POST /register
 *   2. This servlet reads form fields
 *   3. Validates all inputs
 *   4. Calls UserDAO to save (or simulate save) to database
 *   5. Forwards to success.jsp or back to index.jsp with error
 *
 * @WebServlet annotation = no need to configure in web.xml
 */
@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(RegistrationServlet.class.getName());
    private static final long serialVersionUID = 1L;

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        logger.info("RegistrationServlet initialized.");
    }

    // ------------------------------------------------
    // GET /register — show the registration form
    // ------------------------------------------------

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Just forward to the registration form page
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    // ------------------------------------------------
    // POST /register — process form submission
    // ------------------------------------------------

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Step 1: Read form fields
        String name       = sanitize(request.getParameter("name"));
        String surname    = sanitize(request.getParameter("surname"));
        String email      = sanitize(request.getParameter("email"));
        String password   = request.getParameter("password");     // don't sanitize passwords
        String confirmPsw = request.getParameter("confirmPassword");
        String aadhar     = sanitize(request.getParameter("aadhar"));

        logger.info("Registration attempt for email: " + email);

        // Step 2: Validate inputs
        String validationError = validateInputs(name, surname, email, password, confirmPsw, aadhar);

        if (validationError != null) {
            // Validation failed — send back to form with error message
            request.setAttribute("errorMessage", validationError);
            // Keep form fields filled in (good UX)
            request.setAttribute("name", name);
            request.setAttribute("surname", surname);
            request.setAttribute("email", email);
            request.setAttribute("aadhar", aadhar);
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        // Step 3: Check for duplicate email (works when DB is connected)
        if (userDAO.emailExists(email)) {
            request.setAttribute("errorMessage", "This email is already registered. Please use a different email.");
            request.setAttribute("name", name);
            request.setAttribute("surname", surname);
            request.setAttribute("aadhar", aadhar);
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        // Step 4: Check for duplicate Aadhar (works when DB is connected)
        if (userDAO.aadharExists(aadhar)) {
            request.setAttribute("errorMessage", "This Aadhar number is already registered.");
            request.setAttribute("name", name);
            request.setAttribute("surname", surname);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        // Step 5: Create User object
        User user = new User(name, surname, email, password, aadhar);

        // Step 6: Save to database via DAO
        boolean registrationSuccess = userDAO.registerUser(user);

        if (registrationSuccess) {
            // Success — forward to success page
            request.setAttribute("userName", name + " " + surname);
            request.setAttribute("userEmail", email);
            logger.info("Registration successful for: " + email);
            request.getRequestDispatcher("/success.jsp").forward(request, response);
        } else {
            // DB save failed
            request.setAttribute("errorMessage", "Registration failed due to a server error. Please try again.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }

    // ------------------------------------------------
    // Input Validation
    // ------------------------------------------------

    /**
     * Validates all form fields.
     * Returns error message string if invalid, null if all valid.
     */
    private String validateInputs(String name, String surname, String email,
                                   String password, String confirmPsw, String aadhar) {

        // Check empty fields
        if (isBlank(name))      return "First name is required.";
        if (isBlank(surname))   return "Last name is required.";
        if (isBlank(email))     return "Email is required.";
        if (isBlank(password))  return "Password is required.";
        if (isBlank(aadhar))    return "Aadhar number is required.";

        // Name length
        if (name.length() < 2 || name.length() > 100)
            return "First name must be between 2 and 100 characters.";

        if (surname.length() < 2 || surname.length() > 100)
            return "Last name must be between 2 and 100 characters.";

        // Email format
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"))
            return "Please enter a valid email address.";

        // Password strength
        if (password.length() < 8)
            return "Password must be at least 8 characters long.";

        // Password match
        if (!password.equals(confirmPsw))
            return "Passwords do not match.";

        // Aadhar — must be exactly 12 digits
        if (!aadhar.matches("^[0-9]{12}$"))
            return "Aadhar number must be exactly 12 digits.";

        return null; // All valid
    }

    /**
     * Basic input sanitization — trims whitespace, removes HTML tags.
     * Prevents basic XSS injection through form fields.
     */
    private String sanitize(String input) {
        if (input == null) return "";
        return input.trim()
                    .replaceAll("<[^>]*>", "")      // remove HTML tags
                    .replaceAll("[<>\"']", "");      // remove dangerous chars
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
