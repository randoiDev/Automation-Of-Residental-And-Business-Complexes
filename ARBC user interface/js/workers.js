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
    $("#create-worker-form").submit(function (e) {
        e.preventDefault();

        const payload = {
            email: $("#email").val(),
            name: $("#name").val(),
            surname: $("#surname").val(),
            mobileNumber: $("#mobile-number").val()
        };

        $.ajax({
            url: `${API_BASE_URL}/workers`,
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(payload),
            headers: {
                "Authorization": `Bearer ${token}`
            },
            success: function (response) {
                showMessage("#message-box-left", "success", response.message)
                $("#search-btn").click();
            },
            error: function (xhr) {
                showMessage("#message-box-left", "error", xhr.responseJSON.message)
            },
        });
    });

    // Handle Search Button Click
    $("#search-btn").click(function (e) {
        e.preventDefault()
        const usernameQuery = $("#search-username").val();

        // Fetch and populate the table with paginated results
        fetchAndPopulateTable(usernameQuery, 0);
    });

    // Fetch and populate table
    function fetchAndPopulateTable(usernameQuery, page) {
        $.ajax({
            url: `${API_BASE_URL}/workers/search/username-multiple-matches?username=${encodeURIComponent(usernameQuery)}&page=${page}&size=${pageSize}`,
            type: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            },
            success: function (workers) {
                const totalRecords = workers.length;
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
                    fetchAndPopulateTable(usernameQuery, selectedPage);
                });

                const tableBody = $("table tbody");
                tableBody.empty();

                workers.forEach((worker) => {
                    const deleteButtonClass = worker.role === "Admin" ? "btn-danger disabled" : "btn-danger";
                    const row = `
                    <tr>
                        <td>${worker.name}</td>
                        <td>${worker.surname}</td>
                        <td>${worker.username}</td>
                        <td>${worker.mobileNumber}</td>
                        <td>${worker.role}</td>
                        <td>
                            <button id="delete-worker-${worker.id}" class="btn ${deleteButtonClass} btn-sm" data-id="${worker.id}">Delete</button>
                        </td>
                    </tr>`;
                    tableBody.append(row);
                });

                // Add action handlers for delete buttons
                workers.forEach((worker) => {
                    $(`#delete-worker-${worker.id}`).click(function () {
                        const workerId = $(this).data("id");
                        deleteWorker(workerId);
                    });
                });
            },
            error: function (xhr) {
                console.error(xhr.response);
            }
        });
    }



    // Delete worker
    function deleteWorker(workerId) {
        $.ajax({
            url: `${API_BASE_URL}/workers/${encodeURIComponent(workerId)}`,
            type: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            },
            success: function (response) {
                showMessage("#message-box-right", "success", response.message)
                $("#search-btn").click();
            },
            error: function (xhr) {
                showMessage("#message-box-right", "error", xhr.responseJSON.message)
            },
        });
    }
});
