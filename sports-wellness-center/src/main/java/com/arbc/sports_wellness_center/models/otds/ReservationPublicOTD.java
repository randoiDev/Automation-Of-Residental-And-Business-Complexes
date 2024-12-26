package com.arbc.sports_wellness_center.models.otds;

public record ReservationPublicOTD(
        String id,
        String residentsEmail,
        String reservationNumber,
        String startTime,
        String endTime,
        String resource,
        boolean arrived
) {
}
