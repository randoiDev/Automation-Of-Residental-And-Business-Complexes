package com.arbc.sports_wellness_center.services.spec;

import com.arbc.sports_wellness_center.models.dtos.reservations.CreateReservationAppointmentDTO;
import com.arbc.sports_wellness_center.models.messages.Message;
import com.arbc.sports_wellness_center.models.otds.ReservationAppointmentPublicOTD;
import com.arbc.sports_wellness_center.models.otds.ReservationPublicOTD;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;

public interface ReservationsService {

    Message createReservationAppointment(CreateReservationAppointmentDTO createReservationAppointmentDTO);

    Message deleteReservationAppointment(String reservationAppointmentId);

    Message createReservation(String reservationAppointmentId, SecurityContext securityContext, String jwt);

    Message deleteReservation(String reservationAppointmentId, String reservationNumber, SecurityContext securityContext, String jwt);

    Message updateArrived(String reservationAppointmentId, String reservationNumber);

    List<ReservationPublicOTD> readReservations(SecurityContext securityContext, int page, int size);

    List<ReservationPublicOTD> readReservations(String residentsEmail, int page, int size);

    List<ReservationAppointmentPublicOTD> readReservationAppointments(int page, int size);
}
