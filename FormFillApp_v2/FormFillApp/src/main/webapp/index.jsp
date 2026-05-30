<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>User Registration | Sneha DevOps Project</title>
  <link rel="stylesheet" href="css/style.css">
</head>
<body>

  <div class="page-wrapper">

    <!-- Header -->
    <header class="header">
      <div class="header-inner">
        <span class="logo">⚙️Sneha's DevOps Project</span>
        <span class="version-badge">v2.1-SNAPSHOT</span>
      </div>
    </header>

    <!-- Main Form Card -->
    <main class="main">
      <div class="form-card">

        <div class="form-header">
          <h1>Create Account</h1>
          <p>Fill in the details below to register</p>
        </div>

        <%-- Show error message if validation failed --%>
        <c:if test="${not empty errorMessage}">
          <div class="alert alert-error">
            ⚠️ ${errorMessage}
          </div>
        </c:if>

        <%--
          Form submits to /register (RegistrationServlet.java doPost)
          Fields are pre-filled from request attributes on validation error
        --%>
        <form action="register" method="POST" novalidate>

          <!-- Row 1: First Name + Last Name -->
          <div class="form-row">
            <div class="form-group">
              <label for="name">First Name <span class="required">*</span></label>
              <input
                type="text"
                id="name"
                name="name"
                placeholder="Enter first name"
                value="${not empty name ? name : ''}"
                maxlength="100"
                required
              >
            </div>

            <div class="form-group">
              <label for="surname">Last Name <span class="required">*</span></label>
              <input
                type="text"
                id="surname"
                name="surname"
                placeholder="Enter last name"
                value="${not empty surname ? surname : ''}"
                maxlength="100"
                required
              >
            </div>
          </div>

          <!-- Email -->
          <div class="form-group">
            <label for="email">Email Address <span class="required">*</span></label>
            <input
              type="email"
              id="email"
              name="email"
              placeholder="Enter email address"
              value="${not empty email ? email : ''}"
              maxlength="150"
              required
            >
          </div>

          <!-- Aadhar -->
          <div class="form-group">
            <label for="aadhar">Aadhar Number <span class="required">*</span></label>
            <input
              type="text"
              id="aadhar"
              name="aadhar"
              placeholder="Enter 12-digit Aadhar number"
              value="${not empty aadhar ? aadhar : ''}"
              maxlength="12"
              pattern="[0-9]{12}"
              required
            >
            <small class="hint">Must be exactly 12 digits</small>
          </div>

          <!-- Password -->
          <div class="form-group">
            <label for="password">Password <span class="required">*</span></label>
            <input
              type="password"
              id="password"
              name="password"
              placeholder="Minimum 8 characters"
              minlength="8"
              required
            >
          </div>

          <!-- Confirm Password -->
          <div class="form-group">
            <label for="confirmPassword">Confirm Password <span class="required">*</span></label>
            <input
              type="password"
              id="confirmPassword"
              name="confirmPassword"
              placeholder="Repeat your password"
              required
            >
          </div>

          <!-- Terms -->
          <div class="form-group checkbox-group">
            <label class="checkbox-label">
              <input type="checkbox" name="terms" required>
              I agree to the <a href="#" class="link">Terms & Privacy Policy</a>
            </label>
          </div>

          <!-- Submit -->
          <button type="submit" class="btn-register">Register Now</button>

        </form>

      </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
      <p>
        DevOps CI/CD Project by <strong>Sneha Soni</strong> |
        <a href="https://github.com/SnehasDevOpsWorld" target="_blank">GitHub</a> |
        Pipeline: Jenkins → Maven → Tomcat → AWS EC2
      </p>
    </footer>

  </div>

  <script src="js/form-validation.js"></script>
</body>
</html>
