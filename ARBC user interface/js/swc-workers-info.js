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
        if (decodedToken && decodedToken.role && decodedToken.role.includes("SWC worker")) {
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
        url: `${API_BASE_URL}/workers/search/username`,
        type: "GET",
        headers: {
            "Authorization": `Bearer ${token}`
        },
        success: function (worker) {
            $("#username").text(worker.username);
            $("#name").text(worker.name);
            $("#surname").text(worker.surname);
            $("#mobile-number").text(worker.mobileNumber);
            $("#role").text(worker.role);
        },
        error: function (xhr) {
            console.log(xhr.response)
            $("#username").text(notFoundReplacement);
            $("#name").text(notFoundReplacement);
            $("#surname").text(notFoundReplacement);
            $("#mobile-number").text(notFoundReplacement);
            $("#role").text(notFoundReplacement);
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
    $("#submit-password-btn").click(function (e) {
        e.preventDefault();

        const payload = {
            oldPassword: $("#current-password").val(),
            newPassword: $("#new-password").val()
        };

        $.ajax({
            url: `${API_BASE_URL}/workers/update-password`,
            type: "PATCH",
            contentType: "application/json",
            data: JSON.stringify(payload),
            headers: {
                "Authorization": `Bearer ${token}`
            },
            success: function (response) {
                showMessage("#message-box-password", "success", response.message);
            },
            error: function (xhr) {
                showMessage("#message-box-password", "error", xhr.responseJSON.message);
            },
        });
    });

    // Handle Submit Button Click
    $("#submit-mobile-number-btn").click(function (e) {
        e.preventDefault();

        const payload = {
            mobileNumber: $("#new-mobile-number").val(),
        };

        $.ajax({
            url: `${API_BASE_URL}/workers/update-mobile-number`,
            type: "PATCH",
            contentType: "application/json",
            data: JSON.stringify(payload),
            headers: {
                "Authorization": `Bearer ${token}`
            },
            success: function (response) {
                showMessage("#message-box-mobile-number", "success", response.message);
                $("mobile-number").text(payload.mobileNumber)
            },
            error: function (xhr) {
                showMessage("#message-box-mobile-number", "error", xhr.responseJSON.message);
            },
        });
    });
});