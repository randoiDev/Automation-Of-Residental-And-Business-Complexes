$(document).ready(function () {
    const API_BASE_URL = "http://localhost:8080/user-management-1.0-SNAPSHOT/api";
    const notFoundReplacement = "uknown";

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

    // Check JWT token and access
    const token = localStorage.getItem("jwt");
    if (token) {
        const decodedToken = decodeJWT(token);
        if (decodedToken && decodedToken.role && decodedToken.role.includes("Resident")) {
            $("body").css("visibility", "visible");
            $("#welcome-tag").text($("#welcome-tag").text() + (decodedToken.name || "Guest"));
        } else {
            $("body").html("<h1>Forbidden Access</h1>");
            $("body").css("visibility", "visible");
        }
    } else {
        $("body").html("<h1>Forbidden Access</h1>");
        $("body").css("visibility", "visible");
    }

    $.ajax({
        url: `${API_BASE_URL}/residents/search/email`,
        type: "GET",
        headers: {
            "Authorization": `Bearer ${token}`
        },
        success: function (resident) {
            $("#email").text(resident.email);
            $("#name").text(resident.name);
            $("#surname").text(resident.surname);
            $("#banned-for-reservations").text(resident.bannedForReservations);
        },
        error: function (xhr) {
            console.log(xhr.response)
            $("#email").text(notFoundReplacement);
            $("#name").text(notFoundReplacement);
            $("#surname").text(notFoundReplacement);
            $("#banned-for-reservations").text(notFoundReplacement);
        }
    });

    function showMessage(messageBoxId, status, message) {
        const messageBox = $(messageBoxId);
        const color = (status == "success"
        ? "alert-success"
        : "alert-error");

        // Reset alert classes and update based on status code
        messageBox
            .removeClass("d-none")
            .addClass(color);

        // Set the message and display it
        messageBox.html(message.replace(/\n/g, "<br>"));

        // Fade out the message after 2 seconds
        setTimeout(function () {
            messageBox.fadeOut(500, function() {
                messageBox.removeClass(color).addClass("d-none").fadeIn(0); // Reset the fade for future messages
            });
        }, 2000);
    }

    // Handle Submit Button Click
    $("#submit-btn").click(function (e) {
        e.preventDefault();

        const payload = {
            oldPassword: $("#current-password").val(),
            newPassword: $("#new-password").val()
        };

        $.ajax({
            url: `${API_BASE_URL}/residents/update-password`,
            type: "PATCH",
            contentType: "application/json",
            data: JSON.stringify(payload),
            headers: {
                "Authorization": `Bearer ${token}`
            },
            success: function (response) {
                showMessage("success", response.message);
            },
            error: function (xhr) {
                showMessage("error", xhr.responseJSON.message);
            },
        });
    });
});