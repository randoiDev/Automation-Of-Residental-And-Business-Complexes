package com.arbc.sports_wellness_center.mappers;

import org.bson.Document;

public class ReservationMapper {

    public static Document toDocument(String residentsEmail, String reservationNumber, String reminderJobId) {
        return new Document("residents_email", residentsEmail)
                .append("reservation_number", reservationNumber)
                .append("reservation_reminder_job_id", reminderJobId)
                .append("arrived", true);
    }
}
