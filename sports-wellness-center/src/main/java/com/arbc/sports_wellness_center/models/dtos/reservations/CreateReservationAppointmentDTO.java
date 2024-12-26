package com.arbc.sports_wellness_center.models.dtos.reservations;

import com.arbc.sports_wellness_center.models.enums.AppointmentResource;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import static com.arbc.sports_wellness_center.models.Constants.*;

public record CreateReservationAppointmentDTO(
        @NotNull(message = RESERVATION_APPOINTMENT_RESOURCE_FIELD_REQUIRED_VIOLATION_MESSAGE)
        AppointmentResource resource,
        @NotNull(message = RESERVATION_APPOINTMENT_START_TIME_FIELD_REQUIRED_VIOLATION_MESSAGE)
        String startTime,
        @NotNull(message = RESERVATION_APPOINTMENT_END_TIME_REQUIRED_VIOLATION_MESSAGE)
        String endTime,
        @NotNull(message = RESERVATION_APPOINTMENT_MAX_USERS_REQUIRED_VIOLATION_MESSAGE)
        @Min(value = 3, message = RESERVATION_APPOINTMENT_MAX_USERS_MIN_VIOLATION_MESSAGE)
        int maxUsers
) {
}
