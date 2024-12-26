package com.arbc.usermanagement.models.otds.residents;

public record ResidentOTD(
        String id,
        String name,
        String surname,
        String email,
        boolean bannedForReservations
) {
}
