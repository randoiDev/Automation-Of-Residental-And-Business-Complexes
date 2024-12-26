package com.arbc.notificationsservice.config;

import com.arbc.notificationsservice.services.spec.AMQPService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import static com.arbc.notificationsservice.Constants.*;

@Singleton
@Startup
public class RabbitMQClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQClient.class);
    private Connection connection;
    private Channel channel;
    @EJB
    private AMQPService rabbitMQ;

    @PostConstruct
    public void init() {

        // Set AMQPService connection details
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(RABBIT_HOST);
            factory.setPort(RABBIT_PORT);
            factory.setUsername(RABBIT_USERNAME);
            factory.setPassword(RABBIT_PASSWORD);
            connection = factory.newConnection();
            channel = connection.createChannel();

            // Declare Exchanges
            channel.exchangeDeclare(SPORTS_CENTER_EXCHANGE, "direct", true);
            channel.exchangeDeclare(USER_MANAGEMENT_EXCHANGE, "direct", true);
            channel.exchangeDeclare(RESIDENCE_MANAGEMENT_EXCHANGE, "direct", true);

            // Declare Queues and bind to Exchanges with Routing Keys
            channel.queueDeclare(QUEUE_RESERVATION_CREATION, true, false, false, null);
            channel.queueDeclare(QUEUE_RESERVATION_DELETION, true, false, false, null);
            channel.queueDeclare(QUEUE_RESERVATION_REMINDER, true, false, false, null);
            channel.queueDeclare(QUEUE_CREATE_USER_ACCOUNT, true, false, false, null);
            channel.queueDeclare(QUEUE_DELETE_USER_ACCOUNT, true, false, false, null);
            channel.queueDeclare(QUEUE_RENEW_USER_ACCOUNT_PASSWORD, true, false, false, null);
            channel.queueDeclare(QUEUE_ADD_RESIDENCE_TO_ACCOUNT, true, false, false, null);
            channel.queueDeclare(QUEUE_REMOVE_RESIDENCE_FROM_ACCOUNT, true, false, false, null);

            // Bind queues to exchanges with routing keys
            channel.queueBind(QUEUE_RESERVATION_CREATION, SPORTS_CENTER_EXCHANGE, ROUTING_KEY_RESERVATION_CREATION);
            channel.queueBind(QUEUE_RESERVATION_DELETION, SPORTS_CENTER_EXCHANGE, ROUTING_KEY_RESERVATION_DELETION);
            channel.queueBind(QUEUE_RESERVATION_REMINDER, SPORTS_CENTER_EXCHANGE, ROUTING_KEY_RESERVATION_REMINDER);

            channel.queueBind(QUEUE_CREATE_USER_ACCOUNT, USER_MANAGEMENT_EXCHANGE, ROUTING_KEY_CREATE_USER_ACCOUNT);
            channel.queueBind(QUEUE_DELETE_USER_ACCOUNT, USER_MANAGEMENT_EXCHANGE, ROUTING_KEY_DELETE_USER_ACCOUNT);
            channel.queueBind(QUEUE_RENEW_USER_ACCOUNT_PASSWORD, USER_MANAGEMENT_EXCHANGE, ROUTING_KEY_RENEW_USER_ACCOUNT_PASSWORD);

            channel.queueBind(QUEUE_ADD_RESIDENCE_TO_ACCOUNT, RESIDENCE_MANAGEMENT_EXCHANGE, ROUTING_KEY_ADD_RESIDENCE);
            channel.queueBind(QUEUE_REMOVE_RESIDENCE_FROM_ACCOUNT, RESIDENCE_MANAGEMENT_EXCHANGE, ROUTING_KEY_REMOVE_RESIDENCE);

            channel.basicConsume(QUEUE_RESERVATION_CREATION, true, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                LOGGER.info("Received from QUEUE_RESERVATION_CREATION: {}", message);

                rabbitMQ.onReservationCreation(message);
            }, consumerTag -> LOGGER.warn("Consumer {} canceled from QUEUE_RESERVATION_CREATION", consumerTag));

            channel.basicConsume(QUEUE_RESERVATION_DELETION, true, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                LOGGER.info("Received from QUEUE_RESERVATION_DELETION: {}", message);

                rabbitMQ.onReservationRemoval(message);
            }, consumerTag -> LOGGER.warn("Consumer {} canceled from QUEUE_RESERVATION_DELETION", consumerTag));

            channel.basicConsume(QUEUE_RESERVATION_REMINDER, true, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                LOGGER.info("Received from QUEUE_RESERVATION_REMINDER: {}", message);

                rabbitMQ.onReservationReminder(message);
            }, consumerTag -> LOGGER.warn("Consumer {} canceled from QUEUE_RESERVATION_REMINDER", consumerTag));

            channel.basicConsume(QUEUE_CREATE_USER_ACCOUNT, true, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                LOGGER.info("Received from QUEUE_CREATE_USER_ACCOUNT: {}", message);

                rabbitMQ.onUserAccountCreation(message);
            }, consumerTag -> LOGGER.warn("Consumer {} canceled from QUEUE_CREATE_USER_ACCOUNT", consumerTag));

            channel.basicConsume(QUEUE_DELETE_USER_ACCOUNT, true, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                LOGGER.info("Received from QUEUE_DELETE_USER_ACCOUNT: {}", message);

                rabbitMQ.onUserAccountDeletion(message);
            }, consumerTag -> LOGGER.warn("Consumer {} canceled from QUEUE_DELETE_USER_ACCOUNT", consumerTag));

            channel.basicConsume(QUEUE_RENEW_USER_ACCOUNT_PASSWORD, true, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                LOGGER.info("Received from QUEUE_RENEW_USER_ACCOUNT_PASSWORD: {}", message);

                rabbitMQ.onUserRenewedPassword(message);
            }, consumerTag -> LOGGER.warn("Consumer {} canceled from QUEUE_RENEW_USER_ACCOUNT_PASSWORD", consumerTag));

            channel.basicConsume(QUEUE_ADD_RESIDENCE_TO_ACCOUNT, true, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                LOGGER.info("Received from QUEUE_ADD_RESIDENCE_TO_ACCOUNT: {}", message);

                rabbitMQ.onResidenceAddedToAccount(message);
            }, consumerTag -> LOGGER.warn("Consumer {} canceled from QUEUE_ADD_RESIDENCE_TO_ACCOUNT", consumerTag));

            channel.basicConsume(QUEUE_REMOVE_RESIDENCE_FROM_ACCOUNT, true, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                LOGGER.info("Received from QUEUE_REMOVE_RESIDENCE_FROM_ACCOUNT: {}", message);

                rabbitMQ.onResidenceRemovedFromAccount(message);
            }, consumerTag -> LOGGER.warn("Consumer {} canceled from QUEUE_REMOVE_RESIDENCE_FROM_ACCOUNT", consumerTag));

        } catch (IOException|TimeoutException e) {
            LOGGER.error("Failed to initialize RabbitMQ configuration: {}", e.getMessage(), e);
        }
    }

    @PreDestroy
    public void close() {
        if (channel != null && channel.isOpen()) {
            try {
                channel.close();
            } catch (IOException|TimeoutException e) {
                LOGGER.error("Failed to close RabbitMQ channel: {}", e.getMessage(), e);
            }
        }
        if (connection != null && connection.isOpen()) {
            try {
                connection.close();
            } catch (IOException e) {
                LOGGER.error("Failed to close RabbitMQ connection: {}", e.getMessage(), e);
            }
        }
    }


}
