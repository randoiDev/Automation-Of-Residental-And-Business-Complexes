package com.arbc.sports_wellness_center.config;

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

import static com.arbc.sports_wellness_center.models.Constants.*;

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
            channel.exchangeDeclare(SPORTS_CENTER_EXCHANGE, "direct", true);

            // Declare Queues and bind to Exchanges with Routing Keys
            channel.queueDeclare(QUEUE_RESERVATION_CREATION, true, false, false, null);
            channel.queueDeclare(QUEUE_RESERVATION_DELETION, true, false, false, null);
            channel.queueDeclare(QUEUE_RESERVATION_REMINDER, true, false, false, null);

            // Bind queues to exchanges with routing keys
            channel.queueBind(QUEUE_RESERVATION_CREATION, SPORTS_CENTER_EXCHANGE, ROUTING_KEY_RESERVATION_CREATION);
            channel.queueBind(QUEUE_RESERVATION_DELETION, SPORTS_CENTER_EXCHANGE, ROUTING_KEY_RESERVATION_DELETION);
            channel.queueBind(QUEUE_RESERVATION_REMINDER, SPORTS_CENTER_EXCHANGE, ROUTING_KEY_RESERVATION_REMINDER);
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
