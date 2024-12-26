package com.arbc.sports_wellness_center.services.impl;

import com.arbc.sports_wellness_center.controllers.DataStreamEndpoint;
import com.arbc.sports_wellness_center.models.dtos.devices.*;
import com.arbc.sports_wellness_center.models.enums.Resource;
import com.arbc.sports_wellness_center.models.messages.Message;
import com.arbc.sports_wellness_center.services.spec.SWCIoTDevicesService;
import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

import static com.arbc.sports_wellness_center.models.Constants.*;

@ApplicationScoped
public class SWCIoTDevicesServiceImpl implements SWCIoTDevicesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SWCIoTDevicesServiceImpl.class);
    private MqttClient client;

    @PostConstruct
    public void init() {

        try {
            this.client = new MqttClient(MQTT_BROKER + ":" + MQTT_PORT, MQTT_CLIENT_ID);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(MQTT_USERNAME);
            options.setPassword(MQTT_PASSWORD.toCharArray());
            this.client.connect(options);
            this.client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    LOGGER.error("Connection lost! Cause: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) {
                    handleIncomingMessage(s, new String(mqttMessage.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    LOGGER.info("Delivery complete: " + iMqttDeliveryToken.getMessageId() + " time: " + Instant.now());
                }
            });

            client.subscribe(SUBSCRIBER_TOPIC_INTERIOR_HEATER);
            client.subscribe(SUBSCRIBER_TOPIC_ORDINARY_LIGHTS);
            client.subscribe(SUBSCRIBER_TOPIC_AIR_CONDITIONER);
            client.subscribe(SUBSCRIBER_TOPIC_ROOF);
            client.subscribe(SUBSCRIBER_TOPIC_CHANGING_COLOR_LIGHTS);
            client.subscribe(SUBSCRIBER_TOPIC_WATER_PUMP);
        } catch (MqttException e) {
            LOGGER.error("Error while configuring MQTT client: {}", e.getMessage(), e);
        }
    }

    private void handleIncomingMessage(String topic, String message) {
        LOGGER.info("Received message on topic: " + topic + "message: " + message + " time: " + Instant.now());

        System.out.println(message);

        Any response = JsonIterator.deserialize(message);

        switch (response.get("resource").toString()) {
            case "Sauna" -> DataStreamEndpoint.sendDataToClients(Resource.SAUNA, message);
            case "Gym" -> DataStreamEndpoint.sendDataToClients(Resource.GYM, message);
            case "Swimming-pool" -> DataStreamEndpoint.sendDataToClients(Resource.SWIMMING_POOL, message);
        }
    }

    private void publish(String topic, String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setQos(2);
            client.publish(topic, mqttMessage);
            LOGGER.info("Published message: " + message + " topic: " + topic + " time: " + Instant.now());
        } catch (MqttException e) {
            LOGGER.error("Error when trying to publish mqtt message: {}", e.getMessage(), e);
        }
    }

    @Override
    public Message performActionOnInteriorHeater(InteriorHeaterOperationDTO interiorHeaterOperationDTO) {
        publish(PUBLISHER_TOPIC_INTERIOR_HEATER, interiorHeaterOperationDTO.toString());

        return new Message(String.format("%s interior heater operation is in progress...",
                interiorHeaterOperationDTO.resource().getResource()));
    }

    @Override
    public Message performActionOnOrdinaryLights(OrdinaryLightsOperationDTO ordinaryLightsOperationDTO) {
        publish(PUBLISHER_TOPIC_ORDINARY_LIGHTS, ordinaryLightsOperationDTO.toString());

        return new Message(String.format("%s ordinary lights operation is in progress...",
                ordinaryLightsOperationDTO.resource().getResource()));
    }

    @Override
    public Message performActionOnAirConditioner(AirConditionerOperationDTO airConditionerOperationDTO) {
        publish(PUBLISHER_TOPIC_AIR_CONDITIONER, airConditionerOperationDTO.toString());

        return new Message(String.format("%s air conditioner operation is in progress...",
                airConditionerOperationDTO.resource().getResource()));
    }

    @Override
    public Message performActionOnRoof(RoofOperationDTO roofOperationDTO) {
        publish(PUBLISHER_TOPIC_ROOF, roofOperationDTO.toString());

        return new Message(String.format("%s roof operation is in progress...",
                roofOperationDTO.resource().getResource()));
    }

    @Override
    public Message performActionOnChangingColorLights(ChangingColorLightsOperationDTO changingColorLightsOperationDTO) {
        publish(PUBLISHER_TOPIC_CHANGING_COLOR_LIGHTS, changingColorLightsOperationDTO.toString());

        return new Message(String.format("%s changing color lights operation is in progress...",
                changingColorLightsOperationDTO.resource().getResource()));
    }

    @Override
    public Message performActionOnWaterPump(WaterPumpOperationDTO waterPumpOperationDTO) {
        publish(PUBLISHER_TOPIC_WATER_PUMP, waterPumpOperationDTO.toString());

        return new Message(String.format("%s water pump operation is in progress...",
                waterPumpOperationDTO.resource().getResource()));
    }
}
