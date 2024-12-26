package com.arbc.usermanagement.services.impl;

import com.arbc.usermanagement.config.RabbitMQClient;
import com.arbc.usermanagement.exceptions.UserCRUDException;
import com.arbc.usermanagement.mappers.WorkersMapper;
import com.arbc.usermanagement.models.dtos.UpdatePasswordDTO;
import com.arbc.usermanagement.models.dtos.workers.*;
import com.arbc.usermanagement.models.enums.Role;
import com.arbc.usermanagement.models.otds.JwtToken;
import com.arbc.usermanagement.models.otds.Message;
import com.arbc.usermanagement.models.otds.workers.WorkerOTD;
import com.arbc.usermanagement.repositories.WorkersRepository;
import com.arbc.usermanagement.services.spec.WorkersService;
import com.arbc.usermanagement.utils.JwtUtils;
import com.google.gson.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.apache.commons.lang3.RandomStringUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;

import static com.arbc.usermanagement.models.Constants.*;

@ApplicationScoped
public class WorkersServiceImpl implements WorkersService {
    @Inject
    private WorkersRepository workersRepository;
    @Inject
    private RabbitMQClient rabbitMQClient;

    @Override
    public Message createWorker(CreateWorkerDTO createWorkerDTO) {

        // Generate both random password and username for worker that is going to be created.
        final String username = generateRandomUsername();
        final String password = generateRandomPassword();

        final boolean isWorkerCreated = workersRepository.insertWorker(
                WorkersMapper.toDocument(createWorkerDTO, username, encryptPassword(password))
        );

        // For unlikely possible edge case scenario where exception is not thrown but the entity is
        // not inserted in the collection.
        if (!isWorkerCreated)
            throw new InternalServerErrorException(USER_CREATION_EXCEPTION_MESSAGE);

        // Generate payload for message that is going to be published on certain queue
        final JsonObject notificationsServicePayload = new JsonObject();
        notificationsServicePayload.addProperty("name", createWorkerDTO.name());
        notificationsServicePayload.addProperty("recipient", createWorkerDTO.email());
        notificationsServicePayload.addProperty("username", username);
        notificationsServicePayload.addProperty("password", password);

        rabbitMQClient.publishMessage(notificationsServicePayload.toString(), USER_MANAGEMENT_EXCHANGE,
                ROUTING_KEY_CREATE_USER_ACCOUNT);

        return new Message("Worker is created successfully.");
    }

    @Override
    public Message deleteWorker(String workerId) {

        // Perform check that client with provided ID exists and therefore can be deleted
        Optional<WorkerOTD> workerToDelete = WorkersMapper.toWorkerOTDOptional(
                workersRepository.retrieveWorkerById(workerId)
        );

        if (workerToDelete.isEmpty() || workerToDelete.get().role().equals(Role.ADMIN.name()))
            throw new UserCRUDException(String.format(USER_NOT_FOUND_EXCEPTION_MESSAGE, ID_TAG, workerId),
                    Response.Status.NOT_FOUND.getStatusCode());

        boolean isWorkerDeleted = workersRepository.removeWorker(workerId);

        // For unlikely possible edge case scenario where exception is not thrown but the entity is
        // not removed from the collection.
        if (!isWorkerDeleted)
            throw new InternalServerErrorException(USER_DELETION_EXCEPTION_MESSAGE);

        return new Message("Worker is deleted successfully.");
    }

    @Override
    public WorkerOTD readWorkerByUsername(SecurityContext securityContext) {

        return WorkersMapper.toWorkerOTD(
                workersRepository.retrieveWorkerByUsername(securityContext.getUserPrincipal().getName())
        );
    }

    @Override
    public Message updatePassword(UpdatePasswordDTO updatePasswordDTO, SecurityContext securityContext) {

        final Optional<String> hashedPasswordOpt = workersRepository.retrieveWorkerPasswordByUsername(
                securityContext.getUserPrincipal().getName());

        // Validate password or throw exception
        String hashedPassword = hashedPasswordOpt.orElseThrow(() ->
                new UserCRUDException(USER_NOT_FOUND_EXCEPTION_MESSAGE, Response.Status.NOT_FOUND.getStatusCode())
        );

        if (!BCrypt.checkpw(updatePasswordDTO.oldPassword(), hashedPassword)) {
            throw new UserCRUDException(USER_PASSWORD_UPDATE_UNAUTHORIZED_EXCEPTION_MESSAGE,
                    Response.Status.UNAUTHORIZED.getStatusCode());
        }

        // Modify current password if current password was guessed right
        final boolean isPasswordUpdated = workersRepository.modifyWorkerPassword(
                securityContext.getUserPrincipal().getName(), encryptPassword(updatePasswordDTO.newPassword()));

        // For unlikely possible edge case scenario where exception is not thrown but the entity
        // password field is not updated
        if (!isPasswordUpdated)
            throw new InternalServerErrorException(USER_PASSWORD_UPDATE_EXCEPTION_MESSAGE);

        return new Message("Your password is updated successfully.");
    }

    @Override
    public Message updateMobileNumber(UpdateMobileNumberDTO updateMobileNumberDTO, SecurityContext securityContext) {

        Optional<WorkerOTD> workerOpt = WorkersMapper.toWorkerOTDOptional(
                workersRepository.retrieveWorkerByUsername(securityContext.getUserPrincipal().getName()));

        if(workerOpt.isEmpty())
            throw new InternalServerErrorException(SERVER_ERROR_EXCEPTION_MESSAGE);

        if(workerOpt.get().mobileNumber().equals(updateMobileNumberDTO.mobileNumber()))
            throw new UserCRUDException(USER_MOBILE_NUMBER_ALREADY_PRESENT_UPDATE_EXCEPTION_MESSAGE,
                    Response.Status.CONFLICT.getStatusCode());

        final boolean isMobileNumberUpdated = workersRepository.modifyWorkerMobileNumber(
                securityContext.getUserPrincipal().getName(), updateMobileNumberDTO.mobileNumber());

        // For unlikely possible edge case scenario where exception is not thrown but the entity
        // mobile number update is not acknowledged
        if (!isMobileNumberUpdated)
            throw new InternalServerErrorException(USER_MOBILE_NUMBER_UPDATE_EXCEPTION_MESSAGE);

        return new Message("Your mobile number is updated successfully.");
    }

    @Override
    public List<WorkerOTD> readWorkersByUsername(String username, int page, int size) {

        return workersRepository.retrieveWorkersByUsername(username, page, size)
                .stream()
                .map(WorkersMapper::toWorkerOTDOptional)
                .flatMap(Optional::stream)
                .toList();
    }


    @Override
    public JwtToken login(LoginWorkerDTO loginWorkerDto) {

        // Retrieve the hashed password wrapped in an Optional
        final Optional<String> hashedPasswordOpt = workersRepository.retrieveWorkerPasswordByUsername(
                loginWorkerDto.username());

        // Validate password or throw exception
        String hashedPassword = hashedPasswordOpt.orElseThrow(() ->
                new UserCRUDException(String.format(USER_NOT_FOUND_EXCEPTION_MESSAGE, USERNAME_TAG,
                        loginWorkerDto.username()), Response.Status.NOT_FOUND.getStatusCode())
        );

        if (!BCrypt.checkpw(loginWorkerDto.password(), hashedPassword)) {
            throw new UserCRUDException(USER_LOGIN_EXCEPTION_MESSAGE, Response.Status.UNAUTHORIZED.getStatusCode());
        }

        // Retrieve worker info and map to WorkerOTD
        Optional<WorkerOTD> worker = WorkersMapper.toWorkerOTDOptional(
                workersRepository.retrieveWorkerByUsername(loginWorkerDto.username())
        );

        // If info for worker could not be retrieved for some reason
        if(worker.isEmpty())
            throw new InternalServerErrorException(SERVER_ERROR_EXCEPTION_MESSAGE);

        // Generate and return JWT token
        return new JwtToken(JwtUtils.generateWorkerToken(worker.get().username(), worker.get().role(),
                worker.get().name()));
    }

    private String generateRandomUsername() {
        return RandomStringUtils.randomAlphanumeric(8, 17);
    }

    private String generateRandomPassword() {
        return RandomStringUtils.randomAlphanumeric(8, 17);
    }

    private String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
