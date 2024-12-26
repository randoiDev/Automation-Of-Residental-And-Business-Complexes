package com.arbc.usermanagement.controllers;

import com.arbc.usermanagement.filters.security.annotations.Login;
import com.arbc.usermanagement.filters.security.annotations.RequiresRole;
import com.arbc.usermanagement.models.dtos.UpdatePasswordDTO;
import com.arbc.usermanagement.models.dtos.workers.*;
import com.arbc.usermanagement.services.spec.WorkersService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/workers")
@Produces("application/json; charset=UTF-8")
public class WorkersController {

    @Inject
    WorkersService workersService;

    @POST
    @Consumes("application/json; charset=UTF-8")
    @RequiresRole("Admin")
    public Response createWorker(@Valid CreateWorkerDTO createWorkerDTO) {

        return Response
                .status(Response.Status.CREATED)
                .entity(workersService.createWorker(createWorkerDTO))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Consumes("application/json; charset=UTF-8")
    @RequiresRole("Admin")
    public Response deleteWorker(@PathParam("id") String workerId) {

        return Response
                .status(Response.Status.OK)
                .entity(workersService.deleteWorker(workerId))
                .build();
    }

    @GET
    @Path("/search/username")
    @RequiresRole({"Admin", "SWC worker"})
    public Response readWorkerByUsername(@Context SecurityContext securityContext) {

        return Response
                .status(Response.Status.OK)
                .entity(workersService.readWorkerByUsername(securityContext))
                .build();
    }

    @GET
    @Path("/search/username-multiple-matches")
    @RequiresRole("Admin")
    public Response readWorkersByUsername(@QueryParam("username") @DefaultValue("") String username,
                                          @QueryParam("page") @DefaultValue("0") int page,
                                          @QueryParam("size") @DefaultValue("5") int size) {

        return Response
                .status(Response.Status.OK)
                .entity(workersService.readWorkersByUsername(username, page, size))
                .build();
    }

    @PATCH
    @Path("/update-password")
    @Consumes("application/json; charset=UTF-8")
    @RequiresRole({"Admin", "SWC worker"})
    public Response updateWorkerPassword(@Valid UpdatePasswordDTO updatePasswordDTO,
                                         @Context SecurityContext securityContext) {

        return Response
                .status(Response.Status.OK)
                .entity(workersService.updatePassword(updatePasswordDTO, securityContext))
                .build();
    }

    @PATCH
    @Path("/update-mobile-number")
    @Consumes("application/json; charset=UTF-8")
    @RequiresRole({"Admin", "SWC worker"})
    public Response updateWorkerMobileNumber(@Valid UpdateMobileNumberDTO updateMobileNumberDTO,
                                             @Context SecurityContext securityContext) {

        return Response
                .status(Response.Status.OK)
                .entity(workersService.updateMobileNumber(updateMobileNumberDTO, securityContext))
                .build();
    }

    @POST
    @Path("/login")
    @Consumes("application/json; charset=UTF-8")
    @Login
    public Response login(@Valid LoginWorkerDTO loginWorkerDTO) {

        return Response
                .status(Response.Status.OK)
                .entity(workersService.login(loginWorkerDTO))
                .build();
    }
}
