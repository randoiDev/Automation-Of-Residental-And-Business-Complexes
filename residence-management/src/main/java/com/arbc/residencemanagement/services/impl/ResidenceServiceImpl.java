package com.arbc.residencemanagement.services.impl;

import com.arbc.residencemanagement.config.RabbitMQClient;
import com.arbc.residencemanagement.exceptions.ResidenceCRUDException;
import com.arbc.residencemanagement.mappers.ResidenceMapper;
import com.arbc.residencemanagement.models.Message;
import com.arbc.residencemanagement.models.dto.ResidenceUpdateDTO;
import com.arbc.residencemanagement.models.otd.ResidenceOTD;
import com.arbc.residencemanagement.repositories.ResidencesRepository;
import com.arbc.residencemanagement.services.spec.ResidenceService;
import com.google.gson.JsonObject;
import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.arbc.residencemanagement.models.Constants.*;

@RequestScoped
public class ResidenceServiceImpl implements ResidenceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResidenceServiceImpl.class);
    @Inject
    private ResidencesRepository residencesRepository;
    @Inject
    private RabbitMQClient rabbitMQClient;

    @Override
    public Message attachResident(ResidenceUpdateDTO residenceUpdateDTO, String jwt) {
        final String responseBody = usersManagementGetHttpApiCall(residenceUpdateDTO.residentsEmail(), jwt);

        if(residenceUpdateDTO.residenceNumbers().isEmpty())
            throw new ResidenceCRUDException(RESIDENCES_MUST_BE_SPECIFIED_EXCEPTION_MESSAGE,
                    Response.Status.BAD_REQUEST.getStatusCode());

        for(String residenceNumber: residenceUpdateDTO.residenceNumbers()) {
            Optional<ResidenceOTD> residenceOpt = ResidenceMapper.toOtd(
                    residencesRepository.retrieveResidenceByResidenceNumber(residenceNumber));

            if(residenceOpt.isEmpty())
                throw new ResidenceCRUDException(String.format(RESIDENCE_NOT_FOUND_EXCEPTION_MESSAGE,
                        residenceNumber), Response.Status.NOT_FOUND.getStatusCode());

            if(Optional.ofNullable(residenceOpt.get().residentsEmail()).isPresent())
                throw new ResidenceCRUDException(String.format(RESIDENCE_ALREADY_OWNED_EXCEPTION_MESSAGE,
                        residenceNumber), Response.Status.CONFLICT.getStatusCode());
        }

        boolean areResidencesUpdated = residencesRepository.bulkModifyResidencesByResidentsEmail
                (residenceUpdateDTO.residenceNumbers(), residenceUpdateDTO.residentsEmail());

        if(!areResidencesUpdated)
            throw new InternalServerErrorException(RESIDENCES_ATTACHMENT_ERROR_EXCEPTION_MESSAGE);

        Any residentInfo = JsonIterator.deserialize(responseBody);

        rabbitMQClient.publishMessage(createNotificationsServiceResidenceAttachmentPayload(residentInfo,
                residenceUpdateDTO.residenceNumbers()), RESIDENCE_MANAGEMENT_EXCHANGE, ROUTING_KEY_ADD_RESIDENT);

        return new Message("Residences added to residents inventory.");
    }

    @Override
    public Message detachResident(ResidenceUpdateDTO residenceUpdateDTO, String jwt) {
        final String responseBody = usersManagementGetHttpApiCall(residenceUpdateDTO.residentsEmail(), jwt);

        if(residenceUpdateDTO.residenceNumbers().isEmpty())
            throw new ResidenceCRUDException(RESIDENCES_MUST_BE_SPECIFIED_EXCEPTION_MESSAGE,
                    Response.Status.BAD_REQUEST.getStatusCode());

        Set<String> residenceNumbers = residencesRepository.retrieveResidencesByResidentsEmail
                        (residenceUpdateDTO.residentsEmail())
                .stream()
                .map(ResidenceMapper::toOtd)
                .flatMap(Optional::stream)
                .map(ResidenceOTD::residenceNumber)
                .collect(Collectors.toSet());

        if(!residenceNumbers.containsAll(residenceUpdateDTO.residenceNumbers()))
            throw new ResidenceCRUDException(String.format(
                    RESIDENCES_OWNERSHIP_EXCEPTION_MESSAGE,
                    residenceUpdateDTO.residenceNumbers(),
                    residenceUpdateDTO.residentsEmail()), Response.Status.CONFLICT.getStatusCode());

        boolean areResidencesUpdated = residencesRepository.bulkModifyResidencesByResidentsEmail(
                residenceUpdateDTO.residenceNumbers(), null);

        if(!areResidencesUpdated)
            throw new InternalServerErrorException(RESIDENCES_DETACHMENT_ERROR_EXCEPTION_MESSAGE);

        Any residentInfo = JsonIterator.deserialize(responseBody);

        rabbitMQClient.publishMessage(createNotificationsServiceResidenceAttachmentPayload(residentInfo,
                residenceUpdateDTO.residenceNumbers()), RESIDENCE_MANAGEMENT_EXCHANGE, ROUTING_KEY_REMOVE_RESIDENT);

        return new Message("Residences removed from residents inventory.");
    }

    @Override
    public List<String> readAvailableResidences() {

        return residencesRepository.retrieveAvailableResidences()
                .stream()
                .map(ResidenceMapper::toOtd)
                .flatMap(Optional::stream)
                .map(ResidenceOTD::residenceNumber)
                .toList();
    }

    @Override
    public List<String> readResidencesByResidentsEmail(String residentsEmail) {

        return residencesRepository.retrieveResidencesByResidentsEmail(residentsEmail)
                .stream()
                .map(ResidenceMapper::toOtd)
                .flatMap(Optional::stream)
                .map(ResidenceOTD::residenceNumber)
                .toList();
    }

    private String usersManagementGetHttpApiCall(String email, String jwt) {

        try (Client client = ClientBuilder.newClient()) {
            try (Response response = client.target(USER_BASE_URI + ":" + USER_PORT + USER_CONTEXT_PATH)
                    .path(RETRIEVE_RESIDENT_BY_EMAIL)
                    .queryParam("email", email)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", jwt)
                    .method("GET")) {

                // Based on different response code throw different exceptions
                switch (response.getStatus()) {
                    case 404 -> throw new ResidenceCRUDException(response.readEntity(
                            Message.class).message(), Response.Status.NOT_FOUND.getStatusCode());
                    case 500 -> throw new InternalServerErrorException(response.readEntity(Message.class).message());
                }

                return response.readEntity(String.class);
            }
        } catch (ProcessingException e) {
            throw new InternalServerErrorException(SERVER_ERROR_EXCEPTION_MESSAGE);
        } catch (Exception e) {
            LOGGER.error("Error occurred while performing GET {} operation: {}", RETRIEVE_RESIDENT_BY_EMAIL,
                    e.getMessage(), e);
            throw e;
        }
    }

    public String createNotificationsServiceResidenceAttachmentPayload(Any residentsInfo, Set<String> residenceNumbers) {
        final JsonObject notificationsServicePayload = new JsonObject();
        notificationsServicePayload.addProperty("name", residentsInfo.get("name").toString());
        notificationsServicePayload.addProperty("recipient", residentsInfo.get("email").toString());
        notificationsServicePayload.addProperty("residentNumbers", residenceNumbers.toString());

        return notificationsServicePayload.toString();
    }
}