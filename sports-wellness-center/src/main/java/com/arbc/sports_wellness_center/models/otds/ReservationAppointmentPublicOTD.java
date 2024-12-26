package com.arbc.sports_wellness_center.models.otds;

public record ReservationAppointmentPublicOTD(
        String id,
        String resource,
        String startTime,
        String endTime,
        int maxUsers,
        int currentOccupancy
) {
}
