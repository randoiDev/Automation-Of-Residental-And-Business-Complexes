package com.arbc.residencemanagement.services.impl;

import com.arbc.residencemanagement.controllers.DataStreamEndpoint;
import com.arbc.residencemanagement.exceptions.ResidenceCRUDException;
import com.arbc.residencemanagement.mappers.ResidenceMapper;
import com.arbc.residencemanagement.models.Message;
import com.arbc.residencemanagement.models.dto.AirConditionerOperationDTO;
import com.arbc.residencemanagement.models.dto.InteriorHeaterOperationDTO;
import com.arbc.residencemanagement.models.dto.WindowsOperationDTO;
import com.arbc.residencemanagement.models.otd.ResidenceOTD;
import com.arbc.residencemanagement.repositories.ResidencesRepository;
import com.arbc.residencemanagement.services.spec.ResidenceIoTDeviceService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.arbc.residencemanagement.models.Constants.*;

@ApplicationScoped
public class ResidenceIoTDeviceServiceImpl implements ResidenceIoTDeviceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResidenceIoTDeviceServiceImpl.class);
    @Inject
    private ResidencesRepository residencesRepository;
    private MqttClient client;
    private Map<String, OutputStream> clients;

    @PostConstruct
    public void init() {
        clients = new ConcurrentHashMap<>();

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

            List<String> residenceNumbers = residencesRepository.retrieveAllResidences()
                    .stream()
                    .map(ResidenceMapper::toOtd)
                    .flatMap(Optional::stream)
                    .map(ResidenceOTD::residenceNumber)
                    .toList();

            for(String residenceNumber: residenceNumbers) {
                this.client.subscribe(String.format(SUBSCRIBER_TOPIC_HEATER_TEMPLATE, residenceNumber));
                this.client.subscribe(String.format(SUBSCRIBER_TOPIC_AIR_CONDITION_TEMPLATE, residenceNumber));
                this.client.subscribe(String.format(SUBSCRIBER_TOPIC_WINDOWS_TEMPLATE, residenceNumber));
            }
        } catch (MqttException e) {
            LOGGER.error("Error while configuring MQTT client: {}", e.getMessage(), e);
        }
    }

    private void handleIncomingMessage(String topic, String message) {
        LOGGER.info("Received message on topic: " + topic + " message: " + message + " time: " + Instant.now());

        Optional<ResidenceOTD> residenceOTD = ResidenceMapper.toOtd(
                residencesRepository.retrieveResidenceByResidenceNumber(topic.split("/")[1])
        );

        // Check if residence information was successfully retrieved
        if (residenceOTD.isPresent()) {
            DataStreamEndpoint.sendDataToClient(residenceOTD.get().residenceNumber(), message);
        } else {
            LOGGER.warn("No residence found for topic: " + topic);
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
            throw new InternalServerErrorException(SERVER_ERROR_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public Message heaterOperation(InteriorHeaterOperationDTO interiorHeaterOperationDto,
                                   SecurityContext securityContext) {
        checkResidenceOperationConstraints(interiorHeaterOperationDto.residenceNumber(), securityContext);
        publish(String.format(PUBLISHER_TOPIC_HEATER_TEMPLATE, interiorHeaterOperationDto.residenceNumber()),
                interiorHeaterOperationDto.toString());

        return new Message("Heater operation is in progress...");
    }

    @Override
    public Message airConditionOperation(AirConditionerOperationDTO airConditionerOperationDto,
                                         SecurityContext securityContext) {
        checkResidenceOperationConstraints(airConditionerOperationDto.residenceNumber(), securityContext);
        publish(String.format(PUBLISHER_TOPIC_AIR_CONDITION_TEMPLATE, airConditionerOperationDto.residenceNumber()),
                airConditionerOperationDto.toString());

        return new Message("Air conditioner operation is in progress...");
    }

    @Override
    public Message windowsOperation(WindowsOperationDTO windowsOperationDto, SecurityContext securityContext) {
        checkResidenceOperationConstraints(windowsOperationDto.residenceNumber(), securityContext);
        publish(String.format(PUBLISHER_TOPIC_WINDOWS_TEMPLATE, windowsOperationDto.residenceNumber()),
                windowsOperationDto.toString());

        return new Message("Windows operation is in progress...");
    }

    // This is here to be sure that MQTT connection is closed
    @PreDestroy
    public void close() {
        if(this.client.isConnected()) {
            try {
                this.client.close();
            } catch (MqttException e) {
                LOGGER.error("Failed to close MQTT channel: {}", e.getMessage(), e);
            }
        }
    }

    // Check some constraints and throw exceptions if they are not meet
    private void checkResidenceOperationConstraints(String residenceNumber, SecurityContext securityContext) {
        Optional<ResidenceOTD> residenceOpt = ResidenceMapper.toOtd(
                residencesRepository.retrieveResidenceByResidenceNumber(residenceNumber));

        if(residenceOpt.isEmpty()) {
            throw new ResidenceCRUDException(String.format(RESIDENCE_NOT_FOUND_EXCEPTION_MESSAGE, residenceNumber),
                    Response.Status.NOT_FOUND.getStatusCode());
        } else if(!residenceOpt.get().residentsEmail().equals(securityContext.getUserPrincipal().getName())){
            throw new ResidenceCRUDException(String.format(RESIDENCE_OWNERSHIP_EXCEPTION_MESSAGE, residenceNumber,
                    securityContext.getUserPrincipal().getName()),
                    Response.Status.BAD_REQUEST.getStatusCode());
        }
    }

}
