package com.arbc.usermanagement.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.TimeoutException;

import static com.arbc.usermanagement.models.Constants.*;

@ApplicationScoped
public class RabbitMQClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQClient.class);
    private Connection connection;
    private Channel channel;

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
            channel.exchangeDeclare(USER_MANAGEMENT_EXCHANGE, "direct", true);

            // Declare Queues and bind to Exchanges with Routing Keys
            channel.queueDeclare(QUEUE_CREATE_USER_ACCOUNT, true, false, false, null);
            channel.queueDeclare(QUEUE_DELETE_USER_ACCOUNT, true, false, false, null);
            channel.queueDeclare(QUEUE_RENEW_USER_ACCOUNT_PASSWORD, true, false, false, null);

            // Bind queues to exchanges with routing keys
            channel.queueBind(QUEUE_CREATE_USER_ACCOUNT, USER_MANAGEMENT_EXCHANGE, ROUTING_KEY_CREATE_USER_ACCOUNT);
            channel.queueBind(QUEUE_DELETE_USER_ACCOUNT, USER_MANAGEMENT_EXCHANGE, ROUTING_KEY_DELETE_USER_ACCOUNT);
            channel.queueBind(QUEUE_RENEW_USER_ACCOUNT_PASSWORD, USER_MANAGEMENT_EXCHANGE,
                    ROUTING_KEY_RENEW_USER_ACCOUNT_PASSWORD);
        } catch (IOException|TimeoutException e) {
            LOGGER.error("Failed to initialize RabbitMQ configuration: {}", e.getMessage(), e);
        }
    }

    public void publishMessage(String payload, String exchange, String routingKey) {
        try {
            channel.basicPublish(exchange, routingKey, null, payload.getBytes(StandardCharsets.UTF_8));
            LOGGER.info("Message sent to a queue with routing key: {} with payload: {} at: {}", routingKey,
                    payload, Instant.now());
        } catch (IOException e) {
            LOGGER.error("Failed to publish message to a queue with routing key {} with payload: {} at: {}",
                    routingKey, payload, Instant.now(), e);
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
