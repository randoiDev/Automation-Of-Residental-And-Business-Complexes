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
            url: `${API_BASE_URL}/reservations/search/residents-email-multiple-matches?email=${encodeURIComponent(emailQuery)}&page=${page}&size=${pageSize}`,
            type: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            },
            success: function (reservations) {
                const totalRecords = reservations.length;
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

                reservations.forEach((reservation) => {
                    const startTime = new Date(reservation.startTime);
                    const endTime = new Date(reservation.endTime);

                    const markNotArrivedButton = 
                        reservation.arrived
                        ? `<button id="mark-reservation-not-arrived-${reservation.id}" class="btn btn-danger btn-sm" data-id="${reservation.id}" data-reservation-number="${reservation.reservationNumber}">Mark Not Arrived</button>`
                        : ''; 
                
                    const row = `
                        <tr>
                            <td>${reservation.resource}</td>
                            <td>${startTime.toISOString()}</td>
                            <td>${endTime.toISOString()}</td>
                            <td>${reservation.reservationNumber}</td>
                            <td>${reservation.arrived}</td>
                            <td>${markNotArrivedButton}</td>
                        </tr>`;
                    tableBody.append(row);
                });

                // Add action handler for deleting reservations
                reservations.forEach((reservation) => {
                    $(`#mark-reservation-not-arrived-${reservation.id}`).click(function () {
                        const reservationAppointmentId = $(this).data("id");
                        const reservationNumber = $(this).data("reservation-number");
                        markResidentDidNotArrive(reservationAppointmentId, reservationNumber);
                    });
                });
            },
            error: function (xhr) {
                console.error(xhr.response);
            }
        });
    }

    // Mark resident did not arrive
    function markResidentDidNotArrive(reservationAppointmentId, reservationNumber) {
        $.ajax({
            url: `${API_BASE_URL}/reservations/${reservationAppointmentId}/${reservationNumber}`,
            type: "PATCH",
            headers: {
                "Authorization": `Bearer ${token}`
            },
            success: function (response) {
                showMessage("#message-box", "success", response.message);
                fetchAndPopulateTable($("#search-email").val(), 0);
            },
            error: function (xhr) {
                showMessage("#message-box", "error", xhr.responseJSON.message);
            },
        });
    }
});

