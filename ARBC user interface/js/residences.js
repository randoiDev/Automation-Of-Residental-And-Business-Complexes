$(document).ready(function () {

    // Base API URLs
    const USER_MANAGEMENT_API_BASE_URL = "http://localhost:8080/user-management-1.0-SNAPSHOT/api";
    const RESIDENCE_MANAGEMENT_API_BASE_URL = "http://localhost:8080/residence-management-1.0-SNAPSHOT/api";

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
        if (decodedToken && decodedToken.role && decodedToken.role.includes("Admin")) {
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


    function fetchAndPopulateAvailableResidences() {
        $.ajax({
            url: `${RESIDENCE_MANAGEMENT_API_BASE_URL}/residences/search/available-residence-numbers`,
            type: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            },
            success: function (residences) {
                const residenceDropdown = $("#attach-residences");
                residenceDropdown.empty();
                residences.forEach(residence => {
                    residenceDropdown.append(new Option(residence, residence));
                });

            },
            error: function (xhr) {
                console.error(xhr.response)
            }
        });
    }

    $.ajax({
        url: `${USER_MANAGEMENT_API_BASE_URL}/residents/search/email-multiple-matches`,
        type: "GET",
        headers: {
            "Authorization": `Bearer ${token}`
        },
        success: function (residents) {
            const residentsAttachDropdown = $("#attach-residents");
            residentsAttachDropdown.empty();
            residentsAttachDropdown.append(new Option("Select a resident", "", true, false))
            residents.forEach(resident => {
                residentsAttachDropdown.append(new Option(resident.email, resident.email));
            });

            const residentsDettachDropdown = $("#detach-residents");
            residentsDettachDropdown.empty();
            residentsDettachDropdown.append(new Option("Select a resident", "", true, false))
            residents.forEach(resident => {
                residentsDettachDropdown.append(new Option(resident.email, resident.email));
            });

        },
        error: function (xhr) {
            console.error(xhr.response)
        }
    });

    fetchAndPopulateAvailableResidences();

    function fetchAndPopulateAttachedResidences(email) {
        $.ajax({
            url: `${RESIDENCE_MANAGEMENT_API_BASE_URL}/residences/search/residence-numbers-exact-match?email=${encodeURIComponent(email)}`,
            type: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            },
            success: function (residences) {
                const residenceDropdown = $("#detach-residences");
                residenceDropdown.empty();
                residences.forEach(residence => {
                    residenceDropdown.append(new Option(residence, residence));
                });

            },
            error: function (xhr) {
                console.error(xhr.response)
            }
        });
    }

    // Event: On resident selection change in Detach Section
    $("#detach-residents").change(function () {
        const selectedEmail = $(this).val();
        fetchAndPopulateAttachedResidences(selectedEmail);
    });

    // Submit action for Attach Section
    $("#attach-residences-form").submit(function (e) {
        e.preventDefault();
        const residenceNumbers = $("#attach-residences").val();
        const residentsEmail = $("#attach-residents").val();

        $.ajax({
            url: `${RESIDENCE_MANAGEMENT_API_BASE_URL}/residences/add-resident`,
            method: "PATCH",
            headers: {
                "Authorization": `Bearer ${token}`
            },
            contentType: "application/json",
            data: JSON.stringify({ residenceNumbers: residenceNumbers, residentsEmail: residentsEmail }),
            success: function (response) {
                showMessage("#message-box-left", "success", response.message)
                fetchAndPopulateAvailableResidences();
            },
            error: function (xhr) {
                showMessage("#message-box-left", "error", xhr.responseJSON.message)
            }
        });
    });

    // Submit action for Detach Section
    $("#detach-residences-form").submit(function (e) {
        e.preventDefault();
        const residenceNumbers = $("#detach-residences").val();
        const residentsEmail = $("#detach-residents").val();

        console.log(residenceNumbers);

        $.ajax({
            url: `${RESIDENCE_MANAGEMENT_API_BASE_URL}/residences/remove-resident`,
            method: "PATCH",
            headers: {
                "Authorization": `Bearer ${token}`
            },
            contentType: "application/json",
            data: JSON.stringify({ residenceNumbers: residenceNumbers, residentsEmail: residentsEmail }),
            success: function (response) {
                showMessage("#message-box-right", "success", response.message)
                fetchAndPopulateAvailableResidences();
                fetchAndPopulateAttachedResidences($("#detach-residents").val())
            },
            error: function (xhr) {
                showMessage("#message-box-right", "error", xhr.responseJSON.message)
            }
        });
    });
});
