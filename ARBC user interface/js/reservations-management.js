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

    // Fetch and populate left table
    function fetchAndPopulateLeftTable(page) {
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
                $("#left-pagination").empty();
                for (let i = 0; i < totalPages; i++) {
                    $("#left-pagination").append(`<li class="page-item"><a class="page-link" href="#">${i + 1}</a></li>`);
                }

                // Add event listeners for pagination
                $("#left-pagination .page-link").on("click", function (e) {
                    e.preventDefault();
                    const selectedPage = parseInt($(this).text()) - 1;
                    fetchAndPopulateLeftTable(selectedPage);
                });

                const tableBody = $("#left-table-body");
                tableBody.empty();

                reservationAppointments.forEach((reservationAppointment) => {
                    const startTime = new Date(reservationAppointment.startTime);
                    const endTime = new Date(reservationAppointment.endTime); // Convert startTime to a Date object
                
                    const createButton = reservationAppointment.currentOccupancy < reservationAppointment.maxUsers
                        ? `<button id="create-reservation-${reservationAppointment.id}" class="btn btn-danger btn-sm" data-id="${reservationAppointment.id}">Create Reservation</button>`
                        : ''; // No button if startTime is after now
                
                    const row = `
                        <tr>
                            <td>${reservationAppointment.resource}</td>
                            <td>${startTime.toISOString()}</td>
                            <td>${endTime.toISOString()}</td>
                            <td>${reservationAppointment.maxUsers}</td>
                            <td>${reservationAppointment.currentOccupancy}</td>
                            <td>${createButton}</td>
                        </tr>`;
                    tableBody.append(row);
                });
                

                // Add action handlers for delete and reset password buttons
                reservationAppointments.forEach((reservationAppointment) => {
                    $(`#create-reservation-${reservationAppointment.id}`).click(function () {
                        const reservationAppointmentId = $(this).data("id");
                        createReservation(reservationAppointmentId);
                    });
                });
            },
            error: function (xhr) {
                console.error(xhr.response);
            }
        });
    }

    // Create reservation 
    function createReservation(reservationAppointmentId) {
        $.ajax({
            url: `${API_BASE_URL}/reservations/${reservationAppointmentId}`,
            type: "POST",
            headers: {
                "Authorization": `Bearer ${token}`
            },
            success: function (response) {
                showMessage("#message-box-left", "success", response.message);
                fetchAndPopulateLeftTable(0);
                fetchAndPopulateRightTable(0);
            },
            error: function (xhr) {
                showMessage("#message-box-left", "error", xhr.responseJSON.message);
            },
        });
    }

    // Fetch and populate right table
    function fetchAndPopulateRightTable(page) {
        $.ajax({
            url: `${API_BASE_URL}/reservations/search/reservations-exact-match?page=${page}&size=${pageSize}`,
            type: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            },
            success: function (reservations) {
                const totalRecords = reservations.length;
                const totalPages = Math.ceil(totalRecords / pageSize);

                // Update pagination
                $("#right-pagination").empty();
                for (let i = 0; i < totalPages; i++) {
                    $("#right-pagination").append(`<li class="page-item"><a class="page-link" href="#">${i + 1}</a></li>`);
                }

                // Add event listeners for pagination
                $("#right-pagination .page-link").on("click", function (e) {
                    e.preventDefault();
                    const selectedPage = parseInt($(this).text()) - 1;
                    fetchAndPopulateRightTable(selectedPage);
                });

                const tableBody = $("#right-table-body");
                tableBody.empty();

                reservations.forEach((reservation) => {
                    const startTime = new Date(reservation.startTime);
                    const endTime = new Date(reservation.endTime);
                    const now = new Date();
                
                    // Calculate time difference in milliseconds
                    const timeDifference = startTime - now;
                
                    // Show delete button only if the time difference is 4 hours or more
                    const deleteButton = 
                        timeDifference >= 4 * 60 * 60 * 1000
                        ? `<button id="delete-reservation-${reservation.reservationNumber}" class="btn btn-danger btn-sm" data-id="${reservation.id}" data-reservation-number="${reservation.reservationNumber}">Delete Reservation</button>`
                        : ''; // No button if the difference is less than 4 hours or conditions aren't met
                
                    const row = `
                        <tr>
                            <td>${reservation.resource}</td>
                            <td>${startTime.toISOString()}</td>
                            <td>${endTime.toISOString()}</td>
                            <td>${reservation.reservationNumber}</td>
                            <td>${reservation.arrived}</td>
                            <td>${deleteButton}</td>
                        </tr>`;
                    tableBody.append(row);
                });
                
                // Add action handler for deleting reservations
                reservations.forEach((reservation) => {
                    $(`#delete-reservation-${reservation.reservationNumber}`).click(function () {
                        const reservationAppointmentId = $(this).data("id");
                        const reservationNumber = $(this).data("reservation-number");
                        deleteReservation(reservationAppointmentId, reservationNumber);
                    });
                });
            },
            error: function (xhr) {
                console.error(xhr.response);
            }
        });
    }

    // Delete reservation 
    function deleteReservation(reservationAppointmentId, reservationNumber) {
        $.ajax({
            url: `${API_BASE_URL}/reservations/${reservationAppointmentId}/${reservationNumber}`,
            type: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            },
            success: function (response) {
                showMessage("#message-box-right", "success", response.message);
                fetchAndPopulateLeftTable(0);
                fetchAndPopulateRightTable(0);
            },
            error: function (xhr) {
                showMessage("#message-box-right", "error", xhr.responseJSON.message);
            },
        });
    }

    fetchAndPopulateLeftTable(0);
    fetchAndPopulateRightTable(0);

});

