package com.arbc.sports_wellness_center.services.impl;

import com.arbc.sports_wellness_center.config.RabbitMQClient;
import com.arbc.sports_wellness_center.exceptions.SWCCRUDException;
import com.arbc.sports_wellness_center.mappers.ReservationAppointmentMapper;
import com.arbc.sports_wellness_center.mappers.ReservationMapper;
import com.arbc.sports_wellness_center.models.dtos.reservations.CreateReservationAppointmentDTO;
import com.arbc.sports_wellness_center.models.messages.Message;
import com.arbc.sports_wellness_center.models.otds.ReservationAppointmentInternalOTD;
import com.arbc.sports_wellness_center.models.otds.ReservationAppointmentPublicOTD;
import com.arbc.sports_wellness_center.models.otds.ReservationInternalOTD;
import com.arbc.sports_wellness_center.models.otds.ReservationPublicOTD;
import com.arbc.sports_wellness_center.repositories.ReservationsRepository;
import com.arbc.sports_wellness_center.services.spec.ReservationsService;
import com.arbc.sports_wellness_center.utils.JwtUtils;
import com.google.gson.JsonObject;
import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import it.sauronsoftware.cron4j.Scheduler;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static com.arbc.sports_wellness_center.models.Constants.*;

@ApplicationScoped
public class ReservationsServiceImpl implements ReservationsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationsServiceImpl.class);
    @Inject
    private ReservationsRepository reservationsRepository;
    @Inject
    private RabbitMQClient rabbitMQClient;
    private Scheduler scheduler;

    @PostConstruct
    private void init() {
        scheduler = new Scheduler();
    }

    @Override
    public Message createReservationAppointment(CreateReservationAppointmentDTO createReservationAppointmentDTO) {

        //Perform check to see in which time format has data arrived from client and try to parse it in the right one
        final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime startTime = LocalDateTime.parse(createReservationAppointmentDTO.startTime(), FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(createReservationAppointmentDTO.endTime(), FORMATTER);

        // Check that start time is at least 24 hours upfront
        if (ChronoUnit.HOURS.between(LocalDateTime.now(), startTime) < 24)
            throw new SWCCRUDException(RESERVATION_APPOINTMENT_START_TIME_CONSTRAINT_VIOLATION_EXCEPTION_MESSAGE,
                    Response.Status.FORBIDDEN.getStatusCode());

        // Check that start time and end time of reservation appointment differ at least one hour
        if (ChronoUnit.HOURS.between(startTime, endTime) < 1)
            throw new SWCCRUDException(RESERVATION_APPOINTMENT_START_END_TIME_CONSTRAINT_VIOLATION_EXCEPTION_MESSAGE,
                    Response.Status.FORBIDDEN.getStatusCode());

        boolean isReservationAppointmentCreated = reservationsRepository.insertReservationAppointment(
                ReservationAppointmentMapper.toDocument(createReservationAppointmentDTO.resource(), startTime, endTime,
                        createReservationAppointmentDTO.maxUsers())
        );

        if(!isReservationAppointmentCreated)
            throw new InternalServerErrorException(RESERVATION_APPOINTMENT_CREATION_ERROR_EXCEPTION_MESSAGE);

        return new Message("Reservation appointment successfully created.");
    }

    @Override
    public Message deleteReservationAppointment(String reservationAppointmentId) {
        Optional<ReservationAppointmentInternalOTD> reservationAppointmentOTDOpt =
                ReservationAppointmentMapper.toOptionalOtd(
                reservationsRepository.retrieveReservationAppointmentById(reservationAppointmentId)
        );

        if (reservationAppointmentOTDOpt.isEmpty())
            throw new SWCCRUDException(String.format(RESERVATION_APPOINTMENT_NOT_FOUND_EXCEPTION_MESSAGE,
                    reservationAppointmentId), Response.Status.NOT_FOUND.getStatusCode());

        // Check that appointment started or was finished long time ago
        long hoursDifference = ChronoUnit.HOURS.between(LocalDateTime.now(), reservationAppointmentOTDOpt.get().startTime());
        if (hoursDifference > 0 && hoursDifference < 4)
            throw new SWCCRUDException(RESERVATION_APPOINTMENT_DELETION_TIME_CONSTRAINT_VIOLATION_EXCEPTION_MESSAGE,
                    Response.Status.FORBIDDEN.getStatusCode());

        boolean isReservationAppointmentDeleted = reservationsRepository.removeReservationAppointment(
                reservationAppointmentId
        );

        for(ReservationInternalOTD reservationInternalOTD : reservationAppointmentOTDOpt.get().reservations()) {
            scheduler.deschedule(reservationInternalOTD.reservationReminderJobId());
            String payload = createNotificationsServiceReservationPayload(
                    reservationAppointmentOTDOpt.get().startTime(),
                    reservationAppointmentOTDOpt.get().endTime(),
                    reservationAppointmentOTDOpt.get().resource(),
                    Optional.empty(),
                    "Guest",
                    reservationInternalOTD.residentsEmail()
            );
            rabbitMQClient.publishMessage(payload, SPORTS_CENTER_EXCHANGE, ROUTING_KEY_RESERVATION_DELETION);
        }

        // If something unexpectedly happens
        if(!isReservationAppointmentDeleted)
            throw new InternalServerErrorException(RESERVATION_APPOINTMENT_DELETION_ERROR_EXCEPTION_MESSAGE);

        return new Message("Reservation appointment successfully deleted.");
    }

    @Override
    public Message createReservation(String reservationAppointmentId, SecurityContext securityContext, String jwt) {
        boolean bannedForReservations = Boolean.parseBoolean(JwtUtils.getClaim(jwt.substring("Bearer".length()).trim(),
                "bannedForReservations"));

        // Check if user is banned to make reservations
        if(bannedForReservations)
            throw new SWCCRUDException(RESERVATION_CREATION_ERROR_EXCEPTION_MESSAGE, Response.Status.FORBIDDEN.getStatusCode());

        // Perform call to residence management service to gather info whether resident has attached residence to him
        String responseBody = residencesManagementGetHttpApiCall(securityContext.getUserPrincipal().getName(), jwt);
        Any residentsResidences = JsonIterator.deserialize(responseBody);

        // Only residents with residences attached to them can make reservations
        if (residentsResidences.size() < 0)
            throw new SWCCRUDException(RESERVATION_CREATION_FORBIDDEN_EXCEPTION_MESSAGE, Response.Status.FORBIDDEN.getStatusCode());

        Optional<ReservationAppointmentInternalOTD> reservationAppointmentOTDOpt = ReservationAppointmentMapper.toOptionalOtd(
                reservationsRepository.retrieveReservationAppointmentById(reservationAppointmentId)
        );

        if (reservationAppointmentOTDOpt.isEmpty())
            throw new SWCCRUDException(String.format(RESERVATION_APPOINTMENT_NOT_FOUND_EXCEPTION_MESSAGE, reservationAppointmentId), Response.Status.NOT_FOUND.getStatusCode());

        // You cannot make reservation 4 hours before appointment
        if (ChronoUnit.HOURS.between(LocalDateTime.now(), reservationAppointmentOTDOpt.get().startTime()) < 4)
            throw new SWCCRUDException(RESERVATION_CREATION_TIME_CONSTRAINT_VIOLATION_EXCEPTION_MESSAGE, Response.Status.FORBIDDEN.getStatusCode());

        // Check if max users limit has been reached
        if (reservationAppointmentOTDOpt.get().reservations().size() >= reservationAppointmentOTDOpt.get().maxUsers())
            throw new SWCCRUDException(RESERVATION_NUMBER_CONSTRAINT_VIOLATION_EXCEPTION_MESSAGE, Response.Status.CONFLICT.getStatusCode());

        // Create reminder job for sending reminder email
        LocalDateTime startTime = reservationAppointmentOTDOpt.get().startTime().minusHours(2);

        int minute = startTime.getMinute();
        int hour = startTime.getHour();
        int dayOfMonth = startTime.getDayOfMonth();
        int month = startTime.getMonthValue();
        String name = JwtUtils.getClaim(jwt.substring("Bearer".length()).trim(), "name");

        String reminderJobId = scheduler.schedule(String.format("%d %d %d %d *", minute, hour, dayOfMonth, month), () -> {
            String payload = createNotificationsServiceReservationPayload(
                    reservationAppointmentOTDOpt.get().startTime(),
                    reservationAppointmentOTDOpt.get().endTime(),
                    reservationAppointmentOTDOpt.get().resource(),
                    Optional.empty(),
                    name,
                    securityContext.getUserPrincipal().getName()
                    );
            rabbitMQClient.publishMessage(payload, SPORTS_CENTER_EXCHANGE, ROUTING_KEY_RESERVATION_REMINDER);
        });

        String reservationNumber = generateReservationNumber();

        // Try adding reservation to reservations collection
        boolean isReservationAcquired = reservationsRepository.incrementReservationNumberOnReservationAppointment(
                reservationAppointmentOTDOpt.get().id(), reservationAppointmentOTDOpt.get().version(), ReservationMapper.toDocument(
                        securityContext.getUserPrincipal().getName(), reservationNumber, reminderJobId
                ));

        if (!isReservationAcquired) {
            scheduler.deschedule(reminderJobId);
            throw new InternalServerErrorException(RESERVATION_CREATION_ERROR_EXCEPTION_MESSAGE);
        }

        String payload = createNotificationsServiceReservationPayload(
                reservationAppointmentOTDOpt.get().startTime(),
                reservationAppointmentOTDOpt.get().endTime(),
                reservationAppointmentOTDOpt.get().resource(),
                Optional.of(reservationNumber),
                name,
                securityContext.getUserPrincipal().getName()
        );
        rabbitMQClient.publishMessage(payload, SPORTS_CENTER_EXCHANGE, ROUTING_KEY_RESERVATION_CREATION);

        return new Message("Your reservation has been successfully created.");
    }

    @Override
    public Message deleteReservation(String reservationAppointmentId, String reservationNumber, SecurityContext securityContext, String jwt) {
        Optional<ReservationAppointmentInternalOTD> reservationAppointmentOTDOpt = ReservationAppointmentMapper.toOptionalOtd(
                reservationsRepository.retrieveReservationAppointmentById(reservationAppointmentId)
        );

        // First check if reservation appointment exists
        if (reservationAppointmentOTDOpt.isEmpty())
            throw new SWCCRUDException(String.format(RESERVATION_APPOINTMENT_NOT_FOUND_EXCEPTION_MESSAGE, reservationAppointmentId), Response.Status.NOT_FOUND.getStatusCode());

        // Then check whether reservation exists with specified reservation number
        Optional<ReservationInternalOTD> reservationInternalOTDOpt = reservationAppointmentOTDOpt.get().reservations()
                .stream()
                .filter(reservationInternalOTD1 -> reservationInternalOTD1.reservationNumber().equals(reservationNumber)).findFirst();

        if(reservationInternalOTDOpt.isEmpty())
            throw new SWCCRUDException(String.format(RESERVATION_NOT_FOUND_EXCEPTION_MESSAGE, reservationNumber), Response.Status.NOT_FOUND.getStatusCode());

        // Check whether the specified reservation is in residents ownership
        if (!reservationInternalOTDOpt.get().residentsEmail().equals(securityContext.getUserPrincipal().getName()))
            throw new SWCCRUDException(String.format(RESERVATION_OWNERSHIP_EXCEPTION_MESSAGE, reservationNumber), Response.Status.CONFLICT.getStatusCode());

        // You cannot remove reservation 4 hours before appointment
        if (ChronoUnit.HOURS.between(LocalDateTime.now(), reservationAppointmentOTDOpt.get().startTime()) < 4)
            throw new SWCCRUDException(RESERVATION_CREATION_TIME_CONSTRAINT_VIOLATION_EXCEPTION_MESSAGE, Response.Status.FORBIDDEN.getStatusCode());

        boolean isReservationDeleted = reservationsRepository.decrementReservationNumberOnReservationAppointment(reservationAppointmentOTDOpt.get().id(), reservationAppointmentOTDOpt.get().version(), reservationNumber);

        if (!isReservationDeleted)
            throw new InternalServerErrorException(RESERVATION_DELETION_ERROR_EXCEPTION_MESSAGE);

        scheduler.deschedule(reservationInternalOTDOpt.get().reservationReminderJobId());

        String payload = createNotificationsServiceReservationPayload(
                reservationAppointmentOTDOpt.get().startTime(),
                reservationAppointmentOTDOpt.get().endTime(),
                reservationAppointmentOTDOpt.get().resource(),
                Optional.empty(),
                JwtUtils.getClaim(jwt.substring("Bearer".length()).trim(), "name"),
                securityContext.getUserPrincipal().getName()
        );
        rabbitMQClient.publishMessage(payload, SPORTS_CENTER_EXCHANGE, ROUTING_KEY_RESERVATION_DELETION);

        return new Message("Your reservation has been successfully deleted.");
    }

    @Override
    public Message updateArrived(String reservationAppointmentId, String reservationNumber) {
        boolean isArrivedFieldUpdated = reservationsRepository.markReservationAsNotArrived(reservationAppointmentId, reservationNumber);

        if(!isArrivedFieldUpdated)
            throw new InternalServerErrorException(RESERVATION_ARRIVED_FIELD_UPDATE_ERROR_EXCEPTION_MESSAGE);

        return new Message("Reservation arrived field updated.");
    }

    @Override
    public List<ReservationPublicOTD> readReservations(SecurityContext securityContext, int page, int size) {

        return reservationsRepository.retrieveReservationAppointmentsByResidentsEmailExact(securityContext.getUserPrincipal().getName(), page, size)
                .stream()
                .map(ReservationAppointmentMapper::toOptionalOtd)
                .flatMap(Optional::stream)
                .flatMap(reservationAppointmentInternalOTD -> reservationAppointmentInternalOTD.reservations().stream()
                        .map(reservationInternalOTD -> new ReservationPublicOTD(
                                reservationAppointmentInternalOTD.id(),
                                reservationInternalOTD.residentsEmail(),
                                reservationInternalOTD.reservationNumber(),
                                reservationAppointmentInternalOTD.startTime().toString(),
                                reservationAppointmentInternalOTD.endTime().toString(),
                                reservationAppointmentInternalOTD.resource(),
                                reservationInternalOTD.arrived()
                        )))
                .filter(reservationPublicOTD -> reservationPublicOTD.residentsEmail().equals(securityContext.getUserPrincipal().getName()))
                .toList();
    }

    @Override
    public List<ReservationPublicOTD> readReservations(String residentsEmail, int page, int size) {

        return reservationsRepository.retrieveReservationAppointmentsByResidentsEmail(residentsEmail, page, size)
                .stream()
                .map(ReservationAppointmentMapper::toOptionalOtd)
                .flatMap(Optional::stream)
                .flatMap(reservationAppointmentInternalOTD -> reservationAppointmentInternalOTD.reservations().stream()
                        .map(reservationInternalOTD -> new ReservationPublicOTD(
                                reservationAppointmentInternalOTD.id(),
                                reservationInternalOTD.residentsEmail(),
                                reservationInternalOTD.reservationNumber(),
                                reservationAppointmentInternalOTD.startTime().toString(),
                                reservationAppointmentInternalOTD.endTime().toString(),
                                reservationAppointmentInternalOTD.resource(),
                                reservationInternalOTD.arrived()
                        )))
                .toList();
    }

    @Override
    public List<ReservationAppointmentPublicOTD> readReservationAppointments(int page, int size) {

        return reservationsRepository.retrieveReservationAppointments(page, size)
                .stream()
                .map(ReservationAppointmentMapper::toOptionalOtd)
                .flatMap(Optional::stream)
                .map(reservationAppointmentInternalOTD -> new ReservationAppointmentPublicOTD(
                        reservationAppointmentInternalOTD.id(),
                        reservationAppointmentInternalOTD.resource(),
                        reservationAppointmentInternalOTD.startTime().toString(),
                        reservationAppointmentInternalOTD.endTime().toString(),
                        reservationAppointmentInternalOTD.maxUsers(),
                        reservationAppointmentInternalOTD.reservations().size()
                ))
                .toList();
    }

    private String residencesManagementGetHttpApiCall(String email, String jwt) {

        try (Client client = ClientBuilder.newClient()) {
            try (Response response = client.target(RESIDENCE_BASE_URI + ":" + RESIDENCE_PORT + RESIDENCE_CONTEXT_PATH)
                    .path(RETRIEVE_RESIDENCES_BY_RESIDENTS_EMAIL)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", jwt)
                    .method("GET")) {

                // Based on different response code throw different exceptions
                switch (response.getStatus()) {
                    case 404 -> throw new SWCCRUDException(response.readEntity(
                            Message.class).message(), Response.Status.NOT_FOUND.getStatusCode());
                    case 500 -> throw new InternalServerErrorException(response.readEntity(Message.class).message());
                }

                return response.readEntity(String.class);
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred while performing GET {} operation: {}", RETRIEVE_RESIDENCES_BY_RESIDENTS_EMAIL, e.getMessage(), e);
            throw e;
        }
    }

    private String generateReservationNumber() {
        return RandomStringUtils.randomAlphanumeric(15,21);
    }

    public String createNotificationsServiceReservationPayload(LocalDateTime startTime, LocalDateTime endTime,
                                                               String resource, Optional<String> reservationNumber,
                                                               String name, String recipient) {
        final JsonObject notificationsServicePayload = new JsonObject();
        notificationsServicePayload.addProperty("startTime", startTime.toString());
        notificationsServicePayload.addProperty("endTime", endTime.toString());
        notificationsServicePayload.addProperty("resource", resource);
        notificationsServicePayload.addProperty("name", name);
        notificationsServicePayload.addProperty("recipient", recipient);

        reservationNumber.ifPresent(s -> notificationsServicePayload.addProperty("code", s));

        return notificationsServicePayload.toString();
    }
}
