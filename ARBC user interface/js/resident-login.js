$(document).ready(function () {
    const API_URL = "http://localhost:8080/user-management-1.0-SNAPSHOT/api/residents/login";

    localStorage.removeItem('jwt');

    function showMessage(message) {
        const messageBox = $("#message-box");

        // Reset d-none class and update to alert-error
        messageBox
            .removeClass("d-none")
            .addClass("alert-error");

        // Set the message and display it
        messageBox.text(message.replace(/\n/g, "<br>"));

        // Fade out the message after 2 seconds
        setTimeout(function () {
            messageBox.fadeOut(500, function() {
                messageBox.addClass("d-none").fadeIn(0); // Reset the fade for future messages
            });
        }, 2000);
    }

    /**
     * Handle the login button click event.
     */
    $("#login-btn").on("click", function () {
        const email = $("#email").val();
        const password = $("#password").val();

        // Prepare payload
        const payload = JSON.stringify({ email: email, password: password });

        // Make the POST request
        $.ajax({
            url: API_URL,
            type: "POST",
            contentType: "application/json",
            data: payload,
            success: function (response) {

                // Save the JWT to localStorage or cookies
                localStorage.setItem("jwt", response.token);

                // Redirect to the appropriate page
                window.location.href = "./residents-dashboard.html";
            },
            error: function (xhr) {
                
                // Handle errors based on status code
                showMessage(xhr.responseJSON.message);
            },
        });
    });
});
