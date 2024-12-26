$(document).ready(function () {
    const API_BASE_URL = "http://localhost:8080/sports-wellness-center-1.0-SNAPSHOT/api";
    const INTERIOR_HEATER = "interior-heater";
    const AIR_CONDITIONER = "air-conditioner";
    const ORDINARY_LIGHTS = "ordinary-lights";

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

    const socket = new WebSocket(`ws://localhost:8080/sports-wellness-center-1.0-SNAPSHOT/notifications?resource=GYM`);

    socket.onmessage = function (event) {

        // The event.data will contain the notification message sent from the backend
        const data = JSON.parse(event.data);

        switch (data.device) {
            case INTERIOR_HEATER: {
                processDeviceInfo(data, "interior-heater-temperature", "interior-heater-power-value");
                showMessage("#message-box-1", "success", data.message);
                break;
            }
            case AIR_CONDITIONER: {
                processDeviceInfo(data, "air-conditioner-temperature", "air-conditioner-power-value");
                showMessage("#message-box-1", "success", data.message);
                break;
            }
            case ORDINARY_LIGHTS: {
                processOrdinaryLightsInfo(data, "ordinary-lights-intensity");
                showMessage("#message-box-2", "success", data.message);
                break;
            }
        }
    };

    socket.onerror = function (event) {
        console.log(event);
    };

    socket.onclose = function () {
        console.log("WebSocket is closed now.");
    };

    socket.onopen = function () {
        console.log("WebSocket is open now.");

        $.ajax({
            url: `${API_BASE_URL}/devices/${INTERIOR_HEATER}`,
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`
            },
            contentType: "application/json",
            data: JSON.stringify({
                resource: "GYM",
                action: "INFO"
            }),
            success: function (response) {
                showMessage("#message-box-1", "success", response.message);
            },
            error: function () {
                showMessage("#message-box-1", "error", xhr.responseJSON.message);
            }
        });

        $.ajax({
            url: `${API_BASE_URL}/devices/${AIR_CONDITIONER}`,
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`
            },
            contentType: "application/json",
            data: JSON.stringify({
                resource: "GYM",
                action: "INFO"
            }),
            success: function (response) {
                showMessage("#message-box-2", "success", response.message);
            },
            error: function () {
                showMessage("#message-box-2", "error", xhr.responseJSON.message);
            }
        });
    
        $.ajax({
            url: `${API_BASE_URL}/devices/${ORDINARY_LIGHTS}`,
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`
            },
            contentType: "application/json",
            data: JSON.stringify({
                resource: "GYM",
                action: "INFO"
            }),
            success: function (response) {
                showMessage("#message-box-3", "success", response.message);
            },
            error: function () {
                showMessage("#message-box-3", "error", xhr.responseJSON.message);
            }
        });
    };

    $("#interior-heater-raise-temp").click(function () {
        $.ajax({
            url: `${API_BASE_URL}/devices/${INTERIOR_HEATER}`,
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`
            },
            contentType: "application/json",
            data: JSON.stringify({
                resource: "GYM",
                action: "RAISE"
            }),
            success: function (response) {
                showMessage("#message-box-1", "success", response.message);
            },
            error: function () {
                showMessage("#message-box-1", "error", xhr.responseJSON.message);
            }
        });
    });

    $("#interior-heater-lower-temp").click(function () {
        $.ajax({
            url: `${API_BASE_URL}/devices/${INTERIOR_HEATER}`,
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`
            },
            contentType: "application/json",
            data: JSON.stringify({
                resource: "GYM",
                action: "LOWER"
            }),
            success: function (response) {
                showMessage("#message-box-1", "success", response.message);
            },
            error: function () {
                showMessage("#message-box-1", "error", xhr.responseJSON.message);
            }
        });
    });

    $("#interior-heater-power").click(function () {
        $.ajax({
            url: `${API_BASE_URL}/devices/${INTERIOR_HEATER}`,
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`
            },
            contentType: "application/json",
            data: JSON.stringify({
                resource: "GYM",
                action: "POWER"
            }),
            success: function (response) {
                showMessage("#message-box-1", "success", response.message);
            },
            error: function () {
                showMessage("#message-box-1", "error", xhr.responseJSON.message);
            }
        });
    });

    $("#air-conditioner-raise-temp").click(function () {
        $.ajax({
            url: `${API_BASE_URL}/devices/${AIR_CONDITIONER}`,
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`
            },
            contentType: "application/json",
            data: JSON.stringify({
                resource: "GYM",
                action: "RAISE"
            }),
            success: function (response) {
                showMessage("#message-box-2", "success", response.message);
            },
            error: function () {
                showMessage("#message-box-2", "error", xhr.responseJSON.message);
            }
        });
    });

    $("#air-conditioner-lower-temp").click(function () {
        $.ajax({
            url: `${API_BASE_URL}/devices/${AIR_CONDITIONER}`,
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`
            },
            contentType: "application/json",
            data: JSON.stringify({
                resource: "GYM",
                action: "LOWER"
            }),
            success: function (response) {
                showMessage("#message-box-2", "success", response.message);
            },
            error: function () {
                showMessage("#message-box-2", "error", xhr.responseJSON.message);
            }
        });
    });

    $("#air-conditioner-power").click(function () {
        $.ajax({
            url: `${API_BASE_URL}/devices/${AIR_CONDITIONER}`,
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`
            },
            contentType: "application/json",
            data: JSON.stringify({
                resource: "GYM",
                action: "POWER"
            }),
            success: function (response) {
                showMessage("#message-box-2", "success", response.message);
            },
            error: function () {
                showMessage("#message-box-2", "error", xhr.responseJSON.message);
            }
        });
    });

    $("#ordinary-lights-raise-intensity").click(function () {
        $.ajax({
            url: `${API_BASE_URL}/devices/${ORDINARY_LIGHTS}`,
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`
            },
            contentType: "application/json",
            data: JSON.stringify({
                resource: "GYM",
                action: "RAISE"
            }),
            success: function (response) {
                showMessage("#message-box-3", "success", response.message);
            },
            error: function () {
                showMessage("#message-box-3", "error", xhr.responseJSON.message);
            }
        });
    });

    $("#ordinary-lights-lower-intensity").click(function () {
        $.ajax({
            url: `${API_BASE_URL}/devices/${ORDINARY_LIGHTS}`,
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`
            },
            contentType: "application/json",
            data: JSON.stringify({
                resource: "GYM",
                action: "LOWER"
            }),
            success: function (response) {
                showMessage("#message-box-3", "success", response.message);
            },
            error: function () {
                showMessage("#message-box-3", "error", xhr.responseJSON.message);
            }
        });
    });

    // Helper: Process Device Info
    function processDeviceInfo(response, tempId, powerId) {
        $(`#${tempId}`).text(`Temperature: ${response.temperature}°C`);
        $(`#${powerId}`).text(`Power: ${response.power}`);
    }

    // Helper: Process Ordinary Lights Info
    function processOrdinaryLightsInfo(response, intensity) {
        $(`#${intensity}`).text(`Intensity: ${response.volume}`);
    }
});