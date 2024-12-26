package com.arbc.usermanagement.services.impl;

import com.arbc.usermanagement.config.RabbitMQClient;
import com.arbc.usermanagement.exceptions.UserCRUDException;
import com.arbc.usermanagement.mappers.ResidentsMapper;
import com.arbc.usermanagement.models.dtos.residents.LoginResidentDTO;
import com.arbc.usermanagement.models.dtos.UpdatePasswordDTO;
import com.arbc.usermanagement.models.enums.Role;
import com.arbc.usermanagement.models.otds.JwtToken;
import com.arbc.usermanagement.models.otds.Message;
import com.arbc.usermanagement.models.dtos.residents.CreateResidentDTO;
import com.arbc.usermanagement.models.otds.residents.ResidentOTD;
import com.arbc.usermanagement.repositories.ResidentsRepository;
import com.arbc.usermanagement.services.spec.ResidentsService;
import com.arbc.usermanagement.utils.JwtUtils;
import com.google.gson.JsonObject;
import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.apache.commons.lang3.RandomStringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static com.arbc.usermanagement.models.Constants.*;

@ApplicationScoped
public class ResidentsServiceImpl implements ResidentsService {

    private final Logger LOGGER = LoggerFactory.getLogger(ResidentsServiceImpl.class);
    @Inject
    private ResidentsRepository residentsRepository;
    @Inject
    private RabbitMQClient rabbitMQClient;

    @Override
    public Message createResident(CreateResidentDTO createResidentDTO, String jwt) {

        // Generate random password and try to insert resident into the collection
        final String password = generateRandomPassword();
        final boolean isResidentCreated = residentsRepository.insertResident(
                ResidentsMapper.toDocument(createResidentDTO, encryptPassword(password))
        );

        // For unlikely possible edge case scenario where exception is not thrown but
        // the resident is not inserted in to the collection.
        if (!isResidentCreated) {
            throw new InternalServerErrorException(USER_CREATION_EXCEPTION_MESSAGE);
        }

        // Payload for message that is going to be published to a queue for resident creation
        final JsonObject notificationsServicePayload = new JsonObject();
        notificationsServicePayload.addProperty("name", createResidentDTO.name());
        notificationsServicePayload.addProperty("recipient", createResidentDTO.email());
        notificationsServicePayload.addProperty("password", password);

        rabbitMQClient.publishMessage(notificationsServicePayload.toString(),
                USER_MANAGEMENT_EXCHANGE, ROUTING_KEY_CREATE_USER_ACCOUNT);

        return new Message("Resident is created successfully.");
    }

    @Override
    public Message deleteResident(String residentId, String jwt) {

        // First perform check whether the resident we want to delete exists
        Optional<ResidentOTD> residentToDelete = ResidentsMapper.toResidentOTDOptional(
                residentsRepository.retrieveResidentById(residentId)
        );

        if (residentToDelete.isEmpty())
            throw new UserCRUDException(String.format(USER_NOT_FOUND_EXCEPTION_MESSAGE, ID_TAG, residentId),
                    Response.Status.NOT_FOUND.getStatusCode());

        // Then check whether it has attached residences to him
        String responseBody = residencesManagementGetHttpApiCall(residentToDelete.get().email(), jwt);

        Any residentsResidences = JsonIterator.deserialize(responseBody);

        if(residentsResidences.size() > 0)
            throw new UserCRUDException(String.format(USER_HAS_ATTACHED_RESIDENCES_EXCEPTION_MESSAGE,
                    ID_TAG, residentId), Response.Status.FORBIDDEN.getStatusCode());

        boolean isResidentDeleted = residentsRepository.removeResident(residentId);

        // For unlikely possible edge case scenario where exception is not thrown but the resident is
        // not removed from the collection.
        if (!isResidentDeleted)
            throw new InternalServerErrorException(USER_DELETION_EXCEPTION_MESSAGE);

        // Payload for message that is going to be published to a queue for resident removal
        final JsonObject notificationsServicePayload = new JsonObject();
        notificationsServicePayload.addProperty("name", residentToDelete.get().name());
        notificationsServicePayload.addProperty("recipient", residentToDelete.get().email());

        rabbitMQClient.publishMessage(notificationsServicePayload.toString(), USER_MANAGEMENT_EXCHANGE,
                ROUTING_KEY_DELETE_USER_ACCOUNT);

        return new Message("Resident is deleted successfully.");
    }

    @Override
    public ResidentOTD readResidentByEmail(String email) {
        Optional<ResidentOTD> residentOTD = ResidentsMapper.toResidentOTDOptional(
                residentsRepository.retrieveResidentByEmail(email)
        );

        if (residentOTD.isEmpty())
            throw new UserCRUDException(String.format(USER_NOT_FOUND_EXCEPTION_MESSAGE, EMAIL_TAG, email),
                    Response.Status.NOT_FOUND.getStatusCode());

        return residentOTD.get();
    }

    @Override
    public ResidentOTD readResidentByEmail(SecurityContext securityContext) {

        return ResidentsMapper.toResidentOTD(
                residentsRepository.retrieveResidentByEmail(securityContext.getUserPrincipal().getName())
        );
    }

    @Override
    public Message resetResidentPassword(String residentId) {

        // Retrieve info for a resident which password is going to be renewed since we need it for publishing message
        // on relevant queue
        Optional<ResidentOTD> residentToRenewPassword = ResidentsMapper.toResidentOTDOptional(
                residentsRepository.retrieveResidentById(residentId)
        );

        if (residentToRenewPassword.isEmpty())
            throw new UserCRUDException(String.format(USER_NOT_FOUND_EXCEPTION_MESSAGE, ID_TAG, residentId),
                    Response.Status.NOT_FOUND.getStatusCode());

        // Generate new random password and update the existing one
        final String newPassword = generateRandomPassword();
        boolean passwordUpdated = residentsRepository.modifyResidentPassword(residentToRenewPassword.get().email(),
                encryptPassword(newPassword));

        // For unlikely possible edge case scenario where exception is not thrown but the residents password is not updated.
        if (!passwordUpdated)
            throw new InternalServerErrorException(USER_PASSWORD_RENEWAL_EXCEPTION_MESSAGE);

        // Payload for message that is going to be published to a queue for resident password renewal
        final JsonObject notificationsServicePayload = new JsonObject();
        notificationsServicePayload.addProperty("name", residentToRenewPassword.get().name());
        notificationsServicePayload.addProperty("recipient", residentToRenewPassword.get().email());
        notificationsServicePayload.addProperty("password", newPassword);

        rabbitMQClient.publishMessage(notificationsServicePayload.toString(), USER_MANAGEMENT_EXCHANGE,
                ROUTING_KEY_RENEW_USER_ACCOUNT_PASSWORD);

        return new Message("New password has been sent to " + residentToRenewPassword.get().email());
    }

    @Override
    public Message updatePassword(UpdatePasswordDTO updatePasswordDTO, SecurityContext securityContext) {
        final Optional<String> hashedPasswordOpt = residentsRepository.retrieveResidentPasswordByEmail(
                securityContext.getUserPrincipal().getName());

        // Validate password or throw exception
        String hashedPassword = hashedPasswordOpt.orElseThrow(() ->
                new UserCRUDException(String.format(USER_NOT_FOUND_EXCEPTION_MESSAGE, EMAIL_TAG,
                        securityContext.getUserPrincipal().getName()),
                        Response.Status.NOT_FOUND.getStatusCode())
        );

        // Perform check whether the current password matches residents expectation for current password.
        if (!BCrypt.checkpw(updatePasswordDTO.oldPassword(), hashedPassword))
            throw new UserCRUDException(USER_PASSWORD_UPDATE_UNAUTHORIZED_EXCEPTION_MESSAGE,
                    Response.Status.UNAUTHORIZED.getStatusCode());

        final boolean isPasswordUpdated = residentsRepository.modifyResidentPassword(
                securityContext.getUserPrincipal().getName(), encryptPassword(updatePasswordDTO.newPassword()));

        // For unlikely possible edge case scenario where exception is not thrown but the residents password is not updated.
        if (!isPasswordUpdated)
            throw new InternalServerErrorException(USER_PASSWORD_UPDATE_EXCEPTION_MESSAGE);

        return new Message("Your password is updated successfully.");
    }

    @Override
    public Message updateBannedForReservations(SecurityContext securityContext, String residentId) {
        boolean isBannedForReservationsUpdated = residentsRepository.modifyResidentsBannedForReservationField(residentId);

        if (!isBannedForReservationsUpdated)
            throw new InternalServerErrorException(USER_BANNED_FOR_RESERVATIONS_UPDATE_EXCEPTION_MESSAGE);

        return new Message("Banned for reservations field updated successfully.");
    }

    @Override
    public List<ResidentOTD> readResidentsByEmail(String email, int page, int size) {

        return residentsRepository.retrieveResidentsByEmail(email, page, size)
                .stream()
                .map(ResidentsMapper::toResidentOTDOptional)
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    public JwtToken login(LoginResidentDTO loginResidentDto) {

        // Retrieve the hashed password wrapped in an Optional
        final Optional<String> hashedPasswordOpt = residentsRepository.retrieveResidentPasswordByEmail(
                loginResidentDto.email());

        // Validate password or throw exception
        String hashedPassword = hashedPasswordOpt.orElseThrow(() ->
                new UserCRUDException(String.format(USER_NOT_FOUND_EXCEPTION_MESSAGE, EMAIL_TAG,
                        loginResidentDto.email()), Response.Status.NOT_FOUND.getStatusCode())
        );

        if (!BCrypt.checkpw(loginResidentDto.password(), hashedPassword)) {
            throw new UserCRUDException(USER_LOGIN_EXCEPTION_MESSAGE, Response.Status.UNAUTHORIZED.getStatusCode());
        }

        // Retrieve info that is going to fill jwt that is returned to the client as response on login
        Optional<ResidentOTD> residentOtd = ResidentsMapper.toResidentOTDOptional(
                residentsRepository.retrieveResidentByEmail(loginResidentDto.email()));

        if (residentOtd.isEmpty())
            throw new InternalServerErrorException(SERVER_ERROR_EXCEPTION_MESSAGE);

        return new JwtToken(JwtUtils.generateResidentToken(loginResidentDto.email(), Role.RESIDENT.getRole(),
                residentOtd.get().name(),
                String.valueOf(residentOtd.get().bannedForReservations())));
    }

    private String generateRandomPassword() {
        return RandomStringUtils.randomAlphanumeric(8, 17);
    }

    private String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private String residencesManagementGetHttpApiCall(String email, String jwt) {

        try (Client client = ClientBuilder.newClient()) {
            try (Response response = client.target(RESIDENCE_BASE_URI + ":" +
                            RESIDENCE_PORT + RESIDENCE_CONTEXT_PATH)
                    .path(RETRIEVE_RESIDENCES_BY_RESIDENTS_EMAIL)
                    .queryParam("email", email)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", jwt)
                    .method("GET")) {

                // Based on different response code throw different exceptions
                if (response.getStatus() != 200) {
                    throw new InternalServerErrorException(SERVER_ERROR_EXCEPTION_MESSAGE);
                }

                return response.readEntity(String.class);
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred while performing GET {} operation: {}",
                    RETRIEVE_RESIDENCES_BY_RESIDENTS_EMAIL, e.getMessage(), e);
            throw e;
        }
    }
}
