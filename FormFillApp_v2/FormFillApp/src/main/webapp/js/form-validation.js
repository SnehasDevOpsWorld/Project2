// form-validation.js
// Client-side validation — runs BEFORE form is submitted to server
// Note: Server-side validation in RegistrationServlet.java is the REAL validation
// This is just for better user experience (instant feedback)

document.addEventListener('DOMContentLoaded', function () {

  const form = document.querySelector('form');
  if (!form) return;

  form.addEventListener('submit', function (e) {

    const name       = document.getElementById('name');
    const surname    = document.getElementById('surname');
    const email      = document.getElementById('email');
    const aadhar     = document.getElementById('aadhar');
    const password   = document.getElementById('password');
    const confirmPsw = document.getElementById('confirmPassword');
    const terms      = document.querySelector('input[name="terms"]');

    clearErrors();

    let valid = true;

    if (!name.value.trim() || name.value.trim().length < 2) {
      showError(name, 'First name must be at least 2 characters.');
      valid = false;
    }

    if (!surname.value.trim() || surname.value.trim().length < 2) {
      showError(surname, 'Last name must be at least 2 characters.');
      valid = false;
    }

    const emailRegex = /^[\w.-]+@[\w.-]+\.[a-zA-Z]{2,}$/;
    if (!emailRegex.test(email.value.trim())) {
      showError(email, 'Please enter a valid email address.');
      valid = false;
    }

    const aadharRegex = /^[0-9]{12}$/;
    if (!aadharRegex.test(aadhar.value.trim())) {
      showError(aadhar, 'Aadhar must be exactly 12 digits.');
      valid = false;
    }

    if (password.value.length < 8) {
      showError(password, 'Password must be at least 8 characters.');
      valid = false;
    }

    if (password.value !== confirmPsw.value) {
      showError(confirmPsw, 'Passwords do not match.');
      valid = false;
    }

    if (!terms.checked) {
      alert('Please accept the Terms & Privacy Policy to continue.');
      valid = false;
    }

    if (!valid) {
      e.preventDefault();
    }
  });

  function showError(inputElement, message) {
    inputElement.classList.add('error');
    const hint = document.createElement('small');
    hint.className = 'hint error-hint';
    hint.style.color = '#e94560';
    hint.textContent = message;
    inputElement.parentNode.appendChild(hint);
  }

  function clearErrors() {
    document.querySelectorAll('input.error').forEach(el => el.classList.remove('error'));
    document.querySelectorAll('.error-hint').forEach(el => el.remove());
  }

  // Live Aadhar digit-only filter
  const aadharInput = document.getElementById('aadhar');
  if (aadharInput) {
    aadharInput.addEventListener('input', function () {
      this.value = this.value.replace(/[^0-9]/g, '').slice(0, 12);
    });
  }
});
