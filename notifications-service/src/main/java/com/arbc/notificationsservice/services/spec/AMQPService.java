package com.arbc.notificationsservice.services.spec;

public interface AMQPService {

    void onReservationCreation(String message);

    void onReservationRemoval(String message);

    void onReservationReminder(String message);

    void onUserAccountCreation(String message);

    void onUserAccountDeletion(String message);

    void onUserRenewedPassword(String message);

    void onResidenceAddedToAccount(String message);

    void onResidenceRemovedFromAccount(String message);

}
