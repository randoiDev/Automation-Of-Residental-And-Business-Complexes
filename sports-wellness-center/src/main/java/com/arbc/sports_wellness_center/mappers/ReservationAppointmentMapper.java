package com.arbc.sports_wellness_center.mappers;

import com.arbc.sports_wellness_center.models.enums.AppointmentResource;
import com.arbc.sports_wellness_center.models.otds.ReservationAppointmentInternalOTD;
import com.arbc.sports_wellness_center.models.otds.ReservationInternalOTD;
import org.bson.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReservationAppointmentMapper {

    public static Document toDocument(AppointmentResource resource, LocalDateTime startTime,
                                      LocalDateTime endTime, int maxUsers) {
        return new Document("resource", resource)
                .append("start_time", startTime)
                .append("end_time", endTime)
                .append("max_users", maxUsers)
                .append("version", 1)
                .append("reservations", new ArrayList<>());
    }

    public static Optional<ReservationAppointmentInternalOTD> toOptionalOtd(Document document) {

        return Optional.ofNullable(document)
                .map(doc -> new ReservationAppointmentInternalOTD(
                        doc.getObjectId("_id").toHexString(),
                        AppointmentResource.valueOf(doc.get("resource", String.class)).getResource(),
                        doc.get("start_time", Date.class).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                        doc.get("end_time", Date.class).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                        doc.getInteger("max_users"),
                        mapReservations(document.getList("reservations", Document.class)).orElseGet(ArrayList::new),
                        doc.getInteger("version")// map the reservations array
                ));
    }

    // Method to map the reservations field (a list of documents)
    private static Optional<List<ReservationInternalOTD>> mapReservations(List<Document> reservationsDocuments) {
        return Optional.ofNullable(reservationsDocuments)
                .filter(reservations -> !reservations.isEmpty()) // Ensure the list is not empty
                .map(reservations -> reservations.stream()
                        .map(reservationDoc -> new ReservationInternalOTD(
                                reservationDoc.getString("residents_email"),
                                reservationDoc.getString("reservation_number"),
                                reservationDoc.getString("reservation_reminder_job_id"),
                                reservationDoc.getBoolean("arrived")
                        ))
                        .collect(Collectors.toList())
                );
    }
}

