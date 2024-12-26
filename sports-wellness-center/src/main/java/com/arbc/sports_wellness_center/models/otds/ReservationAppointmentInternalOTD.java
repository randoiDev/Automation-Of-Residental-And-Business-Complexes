package com.arbc.sports_wellness_center.models.otds;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationAppointmentInternalOTD(
        String id,
        String resource,
        LocalDateTime startTime,
        LocalDateTime endTime,
        int maxUsers,
        List<ReservationInternalOTD> reservations,
        int version
) {
}
