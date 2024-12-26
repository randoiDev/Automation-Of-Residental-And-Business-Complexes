package com.arbc.notificationsservice.services.impl;

import com.arbc.notificationsservice.services.spec.AMQPService;
import com.arbc.notificationsservice.services.spec.EmailService;
import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class RabbitMQ implements AMQPService {
    @EJB
    EmailService emailService;
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQ.class);

    @Override
    public void onReservationCreation(String message) {
        LOGGER.info("Received message from Sports and Wellness center on adding a reservation for resident account: {}", message);

        // Deserialize the JSON message to `Any`
        Any messageJson = JsonIterator.deserialize(message);

        // Extract fields dynamically using `Any`
        String name = messageJson.get("name").toString();
        String resource = messageJson.get("resource").toString();
        String startTime = messageJson.get("startTime").toString();
        String endTime = messageJson.get("endTime").toString();
        String recipient = messageJson.get("recipient").toString();
        String code = messageJson.get("code").toString();

        emailService.sendReservationCreationEmail(name, resource, startTime, endTime, recipient, code);
    }

    @Override
    public void onReservationRemoval(String message) {
        LOGGER.info("Received message from Sports and Wellness center on removing a reservation from resident account: {}", message);

        // Deserialize the JSON message to `Any`
        Any messageJson = JsonIterator.deserialize(message);

        // Extract fields dynamically using `Any`
        String name = messageJson.get("name").toString();
        String resource = messageJson.get("resource").toString();
        String startTime = messageJson.get("startTime").toString();
        String endTime = messageJson.get("endTime").toString();
        String recipient = messageJson.get("recipient").toString();

        emailService.sendReservationRemovalEmail(name, resource, startTime, endTime, recipient);
    }

    @Override
    public void onReservationReminder(String message) {
        LOGGER.info("Received message from Sports and Wellness center on reminding a resident account for a reservation: {}", message);

        // Deserialize the JSON message to `Any`
        Any messageJson = JsonIterator.deserialize(message);

        // Extract fields dynamically using `Any`
        String name = messageJson.get("name").toString();
        String resource = messageJson.get("resource").toString();
        String startTime = messageJson.get("startTime").toString();
        String endTime = messageJson.get("endTime").toString();
        String recipient = messageJson.get("recipient").toString();

        emailService.sendReservationReminderEmail(name, resource, startTime, endTime, recipient);
    }

    @Override
    public void onUserAccountCreation(String message) {
        LOGGER.info("Received message from User Management on creating a resident account: {}", message);

        // Deserialize the JSON message to `Any`
        Any messageJson = JsonIterator.deserialize(message);

        // Extract fields dynamically using `Any`
        String name = messageJson.get("name").toString();
        String recipient = messageJson.get("recipient").toString();
        String password = messageJson.get("password").toString();
        String username = messageJson.get("username").toString();

        if (username.isEmpty()) {
            emailService.sendUserCreationEmail(name, null, password, recipient);
        } else {
            emailService.sendUserCreationEmail(name, username, password, recipient);
        }
    }

    @Override
    public void onUserAccountDeletion(String message) {
        LOGGER.info("Received message from User Management on deleting a resident account: {}", message);

        // Deserialize the JSON message to `Any`
        Any messageJson = JsonIterator.deserialize(message);

        // Extract fields dynamically using `Any`
        String name = messageJson.get("name").toString();
        String recipient = messageJson.get("recipient").toString();

        emailService.sendUserRemovalEmail(name, recipient);
    }

    @Override
    public void onUserRenewedPassword(String message) {
        LOGGER.info("Received message from User Management on renewing password for resident account: {}", message);

        // Deserialize the JSON message to `Any`
        Any messageJson = JsonIterator.deserialize(message);

        // Extract fields dynamically using `Any`
        String name = messageJson.get("name").toString();
        String recipient = messageJson.get("recipient").toString();
        String password = messageJson.get("password").toString();

        emailService.sendUserRenewedPassword(name, password, recipient);
    }

    @Override
    public void onResidenceAddedToAccount(String message) {
        LOGGER.info("Received message from User Management on adding a residence to resident account: {}", message);

        // Deserialize the JSON message to `Any`
        Any messageJson = JsonIterator.deserialize(message);

        // Extract fields dynamically using `Any`
        String name = messageJson.get("name").toString();
        String recipient = messageJson.get("recipient").toString();
        String residentNumbers = messageJson.get("residentNumbers").toString();

        emailService.sendResidenceAdditionEmail(name, residentNumbers, recipient);
    }

    @Override
    public void onResidenceRemovedFromAccount(String message) {
        LOGGER.info("Received message from User Management on removing a residence from resident account: {}", message);

        // Deserialize the JSON message to `Any`
        Any messageJson = JsonIterator.deserialize(message);

        // Extract fields dynamically using `Any`
        String name = messageJson.get("name").toString();
        String recipient = messageJson.get("recipient").toString();
        String residentNumbers = messageJson.get("residentNumbers").toString();

        emailService.sendResidenceRemovalEmail(name, residentNumbers, recipient);
    }
}
