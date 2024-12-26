package com.arbc;

import com.arbc.entities.Gym;
import com.arbc.entities.Sauna;
import com.arbc.entities.SwimmingPool;
import com.arbc.enums.*;
import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import org.eclipse.paho.client.mqttv3.*;

import java.time.Instant;

import static com.arbc.Constants.*;

public class SWCIoTDevice {

    private static MqttClient client;

    private SWCIoTDevice() throws MqttException {
        final String BROKER = "tcp://localhost:1883";
        final String CLIENT_ID = "swc-iot-device";
        final String USERNAME = "admin";
        final String PASSWORD = "admin";
        client = new MqttClient(BROKER, CLIENT_ID);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());
        client.connect(options);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Connection lost! Cause: " + cause.getMessage());
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) {
                handleIncomingMessage(s, new String(mqttMessage.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                System.out.println("Delivery complete: " + iMqttDeliveryToken.getMessageId() + " time: " + Instant.now());
            }
        });

        client.subscribe(SUBSCRIBER_TOPIC_INTERIOR_HEATER);
        client.subscribe(SUBSCRIBER_TOPIC_AIR_CONDITIONER);
        client.subscribe(SUBSCRIBER_TOPIC_ORDINARY_LIGHTS);
        client.subscribe(SUBSCRIBER_TOPIC_WATER_PUMP);
        client.subscribe(SUBSCRIBER_TOPIC_ROOF);
        client.subscribe(SUBSCRIBER_TOPIC_CHANGING_COLOR_LIGHTS);
    }

    public static void main(String[] args) throws MqttException {
        new SWCIoTDevice();
    }

    public static void publish(String topic, String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setQos(2);
            client.publish(topic, mqttMessage);
            System.out.println("Published message: " + message + " topic: " + topic + " time: " + Instant.now());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void handleIncomingMessage(String topic, String message) {
        System.out.println("Received message on topic: " + topic + "message: " + message + " time: " + Instant.now());

        final Any messageJson = JsonIterator.deserialize(message);
        Resource resource = messageJson.get("resource").as(Resource.class);

        switch(topic) {
            case SUBSCRIBER_TOPIC_INTERIOR_HEATER -> {
                switch (resource) {
                    case SWIMMING_POOL -> {
                        SwimmingPoolOperations operation = messageJson.get("action").as(SwimmingPoolOperations.class);
                        String response = SwimmingPool.getInstance().interiorHeaterOperation(operation);
                        publish(PUBLISHER_TOPIC_INTERIOR_HEATER, response);
                    }
                    case GYM -> {
                        GymOperations operation = messageJson.get("action").as(GymOperations.class);
                        String response = Gym.getInstance().interiorHeaterOperation(operation);
                        publish(PUBLISHER_TOPIC_INTERIOR_HEATER, response);
                    }
                    case SAUNA -> {
                        SaunaOperations operation = messageJson.get("action").as(SaunaOperations.class);
                        String response = Sauna.getInstance().interiorHeaterOperation(operation);
                        publish(PUBLISHER_TOPIC_INTERIOR_HEATER, response);
                    }
                }
            }
            case SUBSCRIBER_TOPIC_WATER_PUMP -> {
                SwimmingPoolOperations operation = messageJson.get("action").as(SwimmingPoolOperations.class);
                String response = SwimmingPool.getInstance().waterPumpAction(operation);
                publish(PUBLISHER_TOPIC_WATER_PUMP, response);
            }
            case SUBSCRIBER_TOPIC_ROOF -> {
                SwimmingPoolOperations operation = messageJson.get("action").as(SwimmingPoolOperations.class);
                String response = SwimmingPool.getInstance().roofAction(operation);
                publish(PUBLISHER_TOPIC_ROOF, response);
            }
            case SUBSCRIBER_TOPIC_ORDINARY_LIGHTS -> {
                switch (resource) {
                    case SWIMMING_POOL -> {
                        SwimmingPoolOperations operation = messageJson.get("action").as(SwimmingPoolOperations.class);
                        String response = SwimmingPool.getInstance().ordinaryLightsOperation(operation);
                        publish(PUBLISHER_TOPIC_ORDINARY_LIGHTS, response);
                    }
                    case GYM -> {
                        GymOperations operation = messageJson.get("action").as(GymOperations.class);
                        String response = Gym.getInstance().ordinaryLightsOperation(operation);
                        publish(PUBLISHER_TOPIC_ORDINARY_LIGHTS, response);
                    }
                    case SAUNA -> {
                        SaunaOperations operation = messageJson.get("action").as(SaunaOperations.class);
                        String response = Sauna.getInstance().ordinaryLightsOperation(operation);
                        publish(PUBLISHER_TOPIC_ORDINARY_LIGHTS, response);
                    }
                }
            }
            case SUBSCRIBER_TOPIC_AIR_CONDITIONER -> {
                GymOperations operation = messageJson.get("action").as(GymOperations.class);
                String response = Gym.getInstance().airConditionerOperation(operation);
                publish(PUBLISHER_TOPIC_AIR_CONDITIONER, response);
            }
            case SUBSCRIBER_TOPIC_CHANGING_COLOR_LIGHTS -> {
                SwimmingPoolLights color = messageJson.get("color").as(SwimmingPoolLights.class);
                String response = SwimmingPool.getInstance().changingColorLightsAction(color);
                publish(PUBLISHER_TOPIC_CHANGING_COLOR_LIGHTS, response);
            }
        }
    }
}