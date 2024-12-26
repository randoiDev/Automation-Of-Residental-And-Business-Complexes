package com.arbc.usermanagement.controllers;

import com.arbc.usermanagement.filters.security.annotations.Login;
import com.arbc.usermanagement.filters.security.annotations.RequiresRole;
import com.arbc.usermanagement.models.dtos.residents.LoginResidentDTO;
import com.arbc.usermanagement.models.dtos.UpdatePasswordDTO;
import com.arbc.usermanagement.models.dtos.residents.CreateResidentDTO;
import com.arbc.usermanagement.services.spec.ResidentsService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/residents")
@Produces("application/json; charset=UTF-8")
public class ResidentsController {

    @Inject
    private ResidentsService residentsService;

    @POST
    @Consumes("application/json; charset=UTF-8")
    @RequiresRole("Admin")
    public Response createResident(@Valid CreateResidentDTO createResidentDTO, @HeaderParam("Authorization") String jwt) {

        return Response
                .status(Response.Status.CREATED)
                .entity(residentsService.createResident(createResidentDTO, jwt))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Consumes("application/json; charset=UTF-8")
    @RequiresRole("Admin")
    public Response deleteResident(@PathParam("id") String residentId, @HeaderParam("Authorization") String jwt) {

        return Response
                .status(Response.Status.OK)
                .entity(residentsService.deleteResident(residentId, jwt))
                .build();
    }

    @PATCH
    @Path("/banned-for-reservations/{id}")
    @RequiresRole("Admin")
    public Response updateBannedForReservationsResidentField(@PathParam("id") String residentId,
                                                             @Context SecurityContext securityContext) {

        return Response
                .status(Response.Status.OK)
                .entity(residentsService.updateBannedForReservations(securityContext,residentId))
                .build();
    }

    @GET
    @Path("/search/email-exact-match")
    @RequiresRole("Admin")
    public Response readResidentByEmail(@QueryParam("email") @DefaultValue("") String email) {

        return Response
                .status(Response.Status.OK)
                .entity(residentsService.readResidentByEmail(email))
                .build();
    }

    @GET
    @Path("/search/email")
    @RequiresRole("Resident")
    public Response readResidentByEmail(@Context SecurityContext securityContext) {

        return Response
                .status(Response.Status.OK)
                .entity(residentsService.readResidentByEmail(securityContext))
                .build();
    }

    @GET
    @Path("/search/email-multiple-matches")
    @RequiresRole("Admin")
    public Response readResidentsByEmail(@QueryParam("email") @DefaultValue("") String email,
                                         @QueryParam("page") @DefaultValue("0") int page,
                                         @QueryParam("size") @DefaultValue("5") int size) {

        return Response
                .status(Response.Status.OK)
                .entity(residentsService.readResidentsByEmail(email, page, size))
                .build();
    }

    @PATCH
    @Path("/reset-password/{id}")
    @Consumes("application/json; charset=UTF-8")
    @RequiresRole("Admin")
    public Response resetResidentPassword(@PathParam("id") String residentsId) {

        return Response
                .status(Response.Status.OK)
                .entity(residentsService.resetResidentPassword(residentsId))
                .build();
    }

    @PATCH
    @Path("/update-password")
    @Consumes("application/json; charset=UTF-8")
    @RequiresRole("Resident")
    public Response updateResidentPassword(@Valid UpdatePasswordDTO updatePasswordDTO,
                                           @Context SecurityContext securityContext) {

        return Response
                .status(Response.Status.OK)
                .entity(residentsService.updatePassword(updatePasswordDTO, securityContext))
                .build();
    }

    @POST
    @Path("/login")
    @Consumes("application/json; charset=UTF-8")
    @Login
    public Response login(@Valid LoginResidentDTO loginResidentDto) {

        return Response
                .status(Response.Status.OK)
                .entity(residentsService.login(loginResidentDto))
                .build();
    }
}

