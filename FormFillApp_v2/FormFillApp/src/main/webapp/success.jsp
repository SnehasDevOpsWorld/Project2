<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%-- Security: if someone lands here directly without registering, redirect them --%>
<%
  if (request.getAttribute("userName") == null) {
      response.sendRedirect(request.getContextPath() + "/index.jsp");
      return;
  }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Registration Successful | Sneha DevOps Project</title>
  <link rel="stylesheet" href="css/style.css">
</head>
<body>

  <div class="page-wrapper">

    <header class="header">
      <div class="header-inner">
        <span class="logo">⚙️ DevOps Project</span>
        <span class="version-badge">v2.1-SNAPSHOT</span>
      </div>
    </header>

    <main class="main">
      <div class="form-card success-card">

        <div class="success-icon">✅</div>

        <h1>Registration Successful!</h1>

        <p class="success-message">
          Welcome, <strong>${userName}</strong>!<br>
          Your account has been created successfully.
        </p>

        <div class="success-details">
          <p>📧 Registered email: <strong>${userEmail}</strong></p>
          <%--
            Project 2.1: When AWS RDS is connected, show:
            "Your data has been saved to our secure database."
            For now show the current status.
          --%>
          <p class="db-status">
            🗄️ Database: <span class="status-pending">Coming in v2.1 — AWS RDS integration</span>
          </p>
        </div>

        <a href="index.jsp" class="btn-register" style="display:inline-block; text-decoration:none; text-align:center;">
          Register Another User
        </a>

      </div>
    </main>

    <footer class="footer">
      <p>
        DevOps CI/CD Project by <strong>Sneha Soni</strong> |
        <a href="https://github.com/SnehasDevOpsWorld" target="_blank">GitHub</a> |
        Pipeline: Jenkins → Maven → Tomcat → AWS EC2
      </p>
    </footer>

  </div>

</body>
</html>
