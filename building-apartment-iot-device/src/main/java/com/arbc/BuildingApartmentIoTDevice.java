package com.arbc;

import com.arbc.entities.AirConditioner;
import com.arbc.entities.InteriorHeater;
import com.arbc.entities.Windows;
import com.arbc.enums.*;
import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import org.eclipse.paho.client.mqttv3.*;

import java.time.Instant;

import static com.arbc.Constants.*;

public class BuildingApartmentIoTDevice {


    private final MqttClient client;
    private final String apartmentNumber;

    private BuildingApartmentIoTDevice(String apartmentNumber) throws MqttException {
        this.apartmentNumber = apartmentNumber;
        final String BROKER = "tcp://localhost:1883";
        final String CLIENT_ID = "building-apartment-" + apartmentNumber + "-iot-device";
        final String USERNAME = "admin";
        final String PASSWORD = "admin";

        this.client = new MqttClient(BROKER, CLIENT_ID);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());
        this.client.connect(options);
        this.client.setCallback(new MqttCallback() {
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

        this.client.subscribe(String.format(SUBSCRIBER_TOPIC_INTERIOR_HEATER, apartmentNumber));
        this.client.subscribe(String.format(SUBSCRIBER_TOPIC_AIR_CONDITIONER, apartmentNumber));
        this.client.subscribe(String.format(SUBSCRIBER_TOPIC_WINDOWS, apartmentNumber));
    }

    public static void main(String[] args) throws MqttException {
        new BuildingApartmentIoTDevice(args[0]);
    }

    private void publish(String topic, String message) {
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
        final String[] topicParts = topic.split("/");

        switch (topicParts[topicParts.length - 1]) {
            case "air_conditioner" -> {
                AirConditionerOperation operation = messageJson.get("action").as(AirConditionerOperation.class);
                String response = AirConditioner.getInstance().airConditionerOperation(operation);
                publish(String.format(PUBLISHER_TOPIC_AIR_CONDITIONER, apartmentNumber), response);
            }
            case "interior_heater" -> {
                InteriorHeaterOperation operation = messageJson.get("action").as(InteriorHeaterOperation.class);
                String response = InteriorHeater.getInstance().heaterOperation(operation);
                publish(String.format(PUBLISHER_TOPIC_INTERIOR_HEATER, apartmentNumber), response);
            }
            case "windows" -> {
                WindowsOperation operation = messageJson.get("action").as(WindowsOperation.class);
                WindowsLocation location = messageJson.get("location").as(WindowsLocation.class);
                String response = Windows.getInstance().windowsOperation(operation, location);
                publish(String.format(PUBLISHER_TOPIC_WINDOWS, apartmentNumber), response);
            }
        }
    }
}