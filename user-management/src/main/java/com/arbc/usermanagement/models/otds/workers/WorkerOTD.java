package com.arbc.usermanagement.models.otds.workers;

public record WorkerOTD(
        String id,
        String name,
        String surname,
        String username,
        String mobileNumber,
        String role
) {
}
