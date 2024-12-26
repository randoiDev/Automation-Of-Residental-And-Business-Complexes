package com.arbc.sports_wellness_center.models.otds;

public record ReservationInternalOTD(
        String residentsEmail,
        String reservationNumber,
        String reservationReminderJobId,
        boolean arrived
) {
}
