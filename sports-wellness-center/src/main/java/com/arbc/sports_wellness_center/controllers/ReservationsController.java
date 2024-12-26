package com.arbc.sports_wellness_center.controllers;

import com.arbc.sports_wellness_center.filters.security.annotations.RequiresRole;
import com.arbc.sports_wellness_center.models.dtos.reservations.CreateReservationAppointmentDTO;
import com.arbc.sports_wellness_center.services.spec.ReservationsService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/reservations")
@Produces("application/json; charset=UTF-8")
public class ReservationsController {

    @Inject
    private ReservationsService reservationsService;

    @POST
    @Consumes("application/json; charset=UTF-8")
    @RequiresRole("Admin")
    public Response createReservationAppointment(@Valid CreateReservationAppointmentDTO createReservationAppointmentDTO) {

        return Response
                .status(Response.Status.OK)
                .entity(reservationsService.createReservationAppointment(createReservationAppointmentDTO))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @RequiresRole("Admin")
    public Response deleteReservationAppointment(@PathParam("id") String reservationAppointmentId) {

        return Response
                .status(Response.Status.OK)
                .entity(reservationsService.deleteReservationAppointment(reservationAppointmentId))
                .build();
    }

    @POST
    @Path("/{id}")
    @Consumes("application/json; charset=UTF-8")
    @RequiresRole("Resident")
    public Response createReservation(@PathParam("id") String reservationAppointmentId,
                                      @Context SecurityContext securityContext,
                                      @HeaderParam("Authorization") String jwt) {

        return Response
                .status(Response.Status.OK)
                .entity(reservationsService.createReservation(reservationAppointmentId, securityContext, jwt))
                .build();
    }

    @DELETE
    @Path("/{id}/{reservation-number}")
    @RequiresRole("Resident")
    public Response deleteReservation(@PathParam("id") String reservationAppointmentId,
                                      @PathParam("reservation-number") String reservationNumber,
                                      @Context SecurityContext securityContext,
                                      @HeaderParam("Authorization") String jwt) {

        return Response
                .status(Response.Status.OK)
                .entity(reservationsService.deleteReservation(
                        reservationAppointmentId, reservationNumber, securityContext, jwt))
                .build();
    }

    @PATCH
    @Path("/{id}/{reservation-number}")
    @RequiresRole("SWC worker")
    public Response updateReservationArrivedField(@PathParam("id") String reservationAppointmentId,
                                      @PathParam("reservation-number") String reservationNumber) {

        return Response
                .status(Response.Status.OK)
                .entity(reservationsService.updateArrived(reservationAppointmentId, reservationNumber))
                .build();
    }

    @GET
    @Path("/search/residents-email-multiple-matches")
    @RequiresRole("SWC worker")
    public Response readReservationsByResidentsEmail(@QueryParam("email") @DefaultValue("") String residentsEmail,
                                                     @QueryParam("page") @DefaultValue("0") int page,
                                                     @QueryParam("size") @DefaultValue("5") int size) {

        return Response
                .status(Response.Status.OK)
                .entity(reservationsService.readReservations(residentsEmail, page, size))
                .build();
    }

    @GET
    @Path("/search/reservations-exact-match")
    @RequiresRole("Resident")
    public Response readReservationsByResidentsEmail(@Context SecurityContext securityContext,
                                                     @QueryParam("page") @DefaultValue("0") int page,
                                                     @QueryParam("size") @DefaultValue("5") int size) {

        return Response
                .status(Response.Status.OK)
                .entity(reservationsService.readReservations(securityContext, page, size))
                .build();
    }

    @GET
    @Path("/search/reservation-appointments")
    @RequiresRole({"Admin", "Resident"})
    public Response readReservationAppointments(@QueryParam("page") @DefaultValue("0") int page,
                                                @QueryParam("size") @DefaultValue("5") int size) {

        return Response
                .status(Response.Status.OK)
                .entity(reservationsService.readReservationAppointments(page, size))
                .build();
    }

}
