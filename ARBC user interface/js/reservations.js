$(document).ready(function () {
    const API_BASE_URL = "http://localhost:8080/sports-wellness-center-1.0-SNAPSHOT/api";
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
    $("#create-reservation-appointment-form").submit(function (e) {
        e.preventDefault();

        const payload = {
            resource: $("#resource").val(),
            startTime: $("#start-time").val(),
            endTime: $("#end-time").val(),
            maxUsers: $("#max-users").val(),
        };

        $.ajax({
            url: `${API_BASE_URL}/reservations`,
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(payload),
            headers: {
                "Authorization": `Bearer ${token}`
            },
            success: function (response) {
                showMessage("#message-box-left", "success", response.message);
                fetchAndPopulateTable(0);
            },
            error: function (xhr) {
                showMessage("#message-box-left", "error", xhr.responseJSON.message);
            },
        });
    });

    // Fetch and populate table
    function fetchAndPopulateTable(page) {
        $.ajax({
            url: `${API_BASE_URL}/reservations/search/reservation-appointments?page=${page}&size=${pageSize}`,
            type: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            },
            success: function (reservationAppointments) {
                const totalRecords = reservationAppointments.length;
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
                    fetchAndPopulateTable(selectedPage);
                });

                const tableBody = $("table tbody");
                tableBody.empty();

                reservationAppointments.forEach((reservationAppointment) => {
                    const now = new Date(); // Get the current time
                    const startTime = new Date(reservationAppointment.startTime);
                    const endTime = new Date(reservationAppointment.endTime); // Convert startTime to a Date object
                
                    const deleteButton = (startTime - now) < 0 || (startTime - now) > 4
                        ? `<button id="delete-reservation-appointment-${reservationAppointment.id}" class="btn btn-danger btn-sm" data-id="${reservationAppointment.id}">Delete</button>`
                        : ''; // No button if startTime is after now
                
                    const row = `
                        <tr>
                            <td>${reservationAppointment.resource}</td>
                            <td>${startTime.toISOString()}</td>
                            <td>${endTime.toISOString()}</td>
                            <td>${reservationAppointment.maxUsers}</td>
                            <td>${deleteButton}</td>
                        </tr>`;
                    tableBody.append(row);
                });
                

                // Add action handlers for delete and reset password buttons
                reservationAppointments.forEach((reservationAppointment) => {
                    $(`#delete-reservation-appointment-${reservationAppointment.id}`).click(function () {
                        const reservationAppointmentId = $(this).data("id");
                        deleteReservationAppointment(reservationAppointmentId);
                    });
                });
            },
            error: function (xhr) {
                console.error(xhr.response);
            }
        });
    }

    // Delete resident
    function deleteReservationAppointment(reservationAppointmentId) {
        $.ajax({
            url: `${API_BASE_URL}/reservations/${reservationAppointmentId}`,
            type: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            },
            success: function (response) {
                showMessage("#message-box-right", "success", response.message);
                fetchAndPopulateTable(0);
            },
            error: function (xhr) {
                showMessage("#message-box-right", "error", xhr.responseJSON.message);
            },
        });
    }

    fetchAndPopulateTable(0);
});

