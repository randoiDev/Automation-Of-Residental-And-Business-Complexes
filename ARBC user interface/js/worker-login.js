$(document).ready(function () {
    const API_URL = "http://localhost:8080/user-management-1.0-SNAPSHOT/api/workers/login";

    localStorage.removeItem("jwt");

    // Function to decode JWT
    function decodeJWT(token) {
        try {
            const base64Url = token.split(".")[1];
            const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
            const jsonPayload = decodeURIComponent(atob(base64).split("").map(function (c) {
                return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
            }).join(""));
            return JSON.parse(jsonPayload);
        } catch (e) {
            return null;
        }
    }

    function showMessage(message) {
        const messageBox = $("#message-box");

        // Reset d-none class and update to alert-error
        messageBox
            .removeClass("d-none")
            .addClass("alert-error");

        // Set the message and display it
        messageBox.html(message.replace(/\n/g, "<br>"));

        // Fade out the message after 2 seconds
        setTimeout(function () {
            messageBox.fadeOut(500, function () {
                messageBox.addClass("d-none").fadeIn(0); // Reset the fade for future messages
            });
        }, 2000);
    }

    /**
     * Handle the login button click event.
     */
    $("#login-btn").click( function () {
        const username = $("#username").val();
        const password = $("#password").val();

        // Prepare payload
        const payload = JSON.stringify({ username: username, password: password });

        // Make the POST request
        $.ajax({
            url: API_URL,
            type: "POST",
            contentType: "application/json",
            data: payload,
            success: function (response) {

                // Assuming the JWT is in the response
                const jwtToken = response.token;
                const decodedToken = decodeJWT(jwtToken);

                if(!decodedToken || !decodedToken.role) {
                    showMessage("Payload of jwt token does not have proper structure.")
                    return;
                }

                // Save the JWT to localStorage or cookies
                localStorage.setItem("jwt", jwtToken);

                // Redirect to the appropriate page
                if (decodedToken.role.includes("Admin"))
                    window.location.href = "./residents.html";
                else
                    window.location.href = "./sauna-dashboard.html";
            },
            error: function (xhr) {

                // Handle errors based on status code
                showMessage(xhr.responseJSON.message);
            },
        });
    });
});
