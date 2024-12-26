package com.arbc.notificationsservice.services.spec;

import java.util.List;

public interface EmailService {

    void sendUserCreationEmail(String name, String username, String password, String recipientEmail);

    void sendUserRemovalEmail(String name, String recipientEmail);

    void sendUserRenewedPassword(String name, String password, String recipientEmail);

    void sendResidenceAdditionEmail(String name, String residenceNumbers, String recipientEmail);

    void sendResidenceRemovalEmail(String name, String residenceNumbers, String recipientEmail);

    void sendReservationCreationEmail(String name, String resource, String startTime, String endTime, String recipientEmail, String code);

    void sendReservationRemovalEmail(String name, String resource, String startTime, String endTime, String recipientEmail);

    void sendReservationReminderEmail(String name, String resource, String startTime, String endTime, String recipientEmail);

}

