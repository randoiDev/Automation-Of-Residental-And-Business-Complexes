$(document).ready(function () {
    const API_BASE_URL = "http://localhost:8080/user-management-1.0-SNAPSHOT/api";
    const pageSize = 5;

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

    // Handle Submit Button Click
    $("#create-resident-form").submit(function (e) {
        e.preventDefault();

        const payload = {
            email: $("#email").val(),
            name: $("#name").val(),
            surname: $("#surname").val(),
        };

        $.ajax({
            url: `${API_BASE_URL}/residents`,
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(payload),
            headers: {
                "Authorization": `Bearer ${token}`
            },
            success: function (response) {
                showMessage("#message-box-left", "success", response.message);
                $("#search-btn").click();
            },
            error: function (xhr) {
                showMessage("#message-box-left", "error", xhr.responseJSON.message);
            },
        });
    });

    // Handle Search Button Click
    $("#search-btn").click(function (e) {
        e.preventDefault();

        const emailQuery = $("#search-email").val();

        // Fetch and populate the table with paginated results
        fetchAndPopulateTable(emailQuery, 0);
    });

    // Fetch and populate table
    function fetchAndPopulateTable(emailQuery, page) {
        $.ajax({
            url: `${API_BASE_URL}/residents/search/email-multiple-matches?email=${encodeURIComponent(emailQuery)}&page=${page}&size=${pageSize}`,
            type: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            },
            success: function (residents) {
                const totalRecords = residents.length;
                const totalPages = Math.ceil(totalRecords / pageSize);

                // Update pagination
                $(".pagination").empty();
                for (let i = 0; i < totalPages; i++) {
                    $(".pagination").append(`<li class="page-item"><a class="page-link" href="#">${i + 1}</a></li>`);
                }

                // Add event listeners for pagination
                $(".pagination .page-link").on("click", function (e) {
                    e.preventDefault();
                    const selectedPage = parseInt($(this).text()) - 1;
                    fetchAndPopulateTable(emailQuery, selectedPage);
                });

                const tableBody = $("table tbody");
                tableBody.empty();

                residents.forEach((resident) => {
                    const row = `
                    <tr>
                        <td>${resident.name}</td>
                        <td>${resident.surname}</td>
                        <td>${resident.email}</td>
                        <td>${resident.bannedForReservations}</td>
                        <td class="actions-column">
                            <button id="delete-resident-${resident.id}" class="btn btn-danger btn-sm" data-id="${resident.id}">Delete</button>
                            <button id="reset-password-${resident.id}" class="btn btn-warning btn-sm" data-id="${resident.id}">Reset Password</button>
                            <button id="ban-unban-${resident.id}" class="btn btn-secondary btn-sm" data-id="${resident.id}">(Un)Ban</button>
                        </td>
                    </tr>`;
                    tableBody.append(row);
                });

                // Add action handlers for delete and reset password buttons
                residents.forEach((resident) => {
                    $(`#delete-resident-${resident.id}`).click(function () {
                        const residentId = $(this).data("id");
                        deleteResident(residentId);
                    });
                });

                residents.forEach((resident) => {
                    $(`#reset-password-${resident.id}`).click(function () {
                        const residentId = $(this).data("id");
                        resetPassword(residentId);
                    });
                });

                residents.forEach((resident) => {
                    $(`#ban-unban-${resident.id}`).click(function () {
                        const residentId = $(this).data("id");
                        banUnbanResidentForReservations(residentId);
                    });
                });
            },
            error: function (xhr) {
                console.error(xhr.response);
            }
        });
    }

    // Delete resident
    function deleteResident(residentId) {
        $.ajax({
            url: `${API_BASE_URL}/residents/${encodeURIComponent(residentId)}`,
            type: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            },
            success: function (response) {
                showMessage("#message-box-right", "success", response.message);
                $("#search-btn").click();
            },
            error: function (xhr) {
                showMessage("#message-box-right", "error", xhr.responseJSON.message);
            },
        });
    }

    // Reset resident's password
    function resetPassword(residentId) {
        $.ajax({
            url: `${API_BASE_URL}/residents/reset-password/${encodeURIComponent(residentId)}`,
            type: "PATCH",
            headers: {
                "Authorization": `Bearer ${token}`
            },
            success: function (response) {
                showMessage("#message-box-right", "success", response.message);
            },
            error: function (xhr) {
                showMessage("#message-box-right", "error", xhr.responseJSON.message);
            },
        });
    }

    // (Un)Ban resident for reservations
    function banUnbanResidentForReservations(residentId) {
        $.ajax({
            url: `${API_BASE_URL}/residents/banned-for-reservations/${encodeURIComponent(residentId)}`,
            type: "PATCH",
            headers: {
                "Authorization": `Bearer ${token}`
            },
            success: function (response) {
                showMessage("#message-box-right", "success", response.message);
                $("#search-btn").click();
            },
            error: function (xhr) {
                showMessage("#message-box-right", "error", xhr.responseJSON.message);
            },
        });
    }
});

