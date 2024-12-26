package com.arbc.residencemanagement.controllers;

import com.arbc.residencemanagement.filters.security.annotations.RequiresRole;
import com.arbc.residencemanagement.models.dto.ResidenceUpdateDTO;
import com.arbc.residencemanagement.services.spec.ResidenceService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/residences")
@Produces("application/json; charset=UTF-8")
public class ResidencesController {

    @Inject
    private ResidenceService residenceService;

    @Path("/add-resident")
    @PATCH
    @Consumes("application/json; charset=UTF-8")
    @RequiresRole("Admin")
    public Response attachResident(@Valid ResidenceUpdateDTO residenceUpdateDTO,
                                   @HeaderParam("Authorization") String jwt) {

        return Response
                .status(Response.Status.OK)
                .entity(residenceService.attachResident(residenceUpdateDTO, jwt))
                .build();
    }

    @Path("/remove-resident")
    @PATCH
    @Consumes("application/json; charset=UTF-8")
    @RequiresRole("Admin")
    public Response detachResident(@Valid ResidenceUpdateDTO residenceUpdateDTO,
                                   @HeaderParam("Authorization") String jwt) {

        return Response
                .status(Response.Status.OK)
                .entity(residenceService.detachResident(residenceUpdateDTO, jwt))
                .build();
    }

    @GET
    @Path("/search/available-residence-numbers")
    @RequiresRole("Admin")
    public Response readAvailableResidences() {

        return Response
                .status(Response.Status.OK)
                .entity(residenceService.readAvailableResidences())
                .build();
    }

    @GET
    @Path("/search/residence-numbers-exact-match")
    @RequiresRole("Admin")
    public Response readResidencesByResidentsEmail(@QueryParam("email")
                                                       @DefaultValue("") String residentsEmail) {

        return Response
                .status(Response.Status.OK)
                .entity(residenceService.readResidencesByResidentsEmail(residentsEmail))
                .build();
    }

    @GET
    @Path("/search/residence-numbers")
    @RequiresRole("Resident")
    public Response readResidencesByResidentsEmail(@Context SecurityContext securityContext) {

        return Response
                .status(Response.Status.OK)
                .entity(residenceService.readResidencesByResidentsEmail(securityContext.getUserPrincipal().getName()))
                .build();
    }
}
