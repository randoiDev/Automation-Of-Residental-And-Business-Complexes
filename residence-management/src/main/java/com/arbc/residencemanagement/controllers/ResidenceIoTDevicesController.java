package com.arbc.residencemanagement.controllers;

import com.arbc.residencemanagement.filters.security.annotations.RequiresRole;
import com.arbc.residencemanagement.models.dto.AirConditionerOperationDTO;
import com.arbc.residencemanagement.models.dto.InteriorHeaterOperationDTO;
import com.arbc.residencemanagement.models.dto.WindowsOperationDTO;
import com.arbc.residencemanagement.services.spec.ResidenceIoTDeviceService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.io.OutputStream;
import java.io.PrintWriter;

import static com.mongodb.internal.authentication.AwsCredentialHelper.LOGGER;

@Path("/devices")
@Produces("application/json; charset=UTF-8")
public class ResidenceIoTDevicesController {

    @Inject
    private ResidenceIoTDeviceService residenceService;

    @Path("/interior-heater")
    @POST
    @Consumes("application/json; charset=UTF-8")
    @RequiresRole("Resident")
    public Response heaterOperation(@Valid InteriorHeaterOperationDTO interiorHeaterOperationDto,
                                    @Context SecurityContext securityContext) {

        return Response.ok(residenceService.heaterOperation(interiorHeaterOperationDto, securityContext)).build();
    }

    @Path("/air-conditioner")
    @POST
    @Consumes("application/json; charset=UTF-8")
    @RequiresRole("Resident")
    public Response airConditionerOperation(@Valid AirConditionerOperationDTO airConditionerOperationDto,
                                            @Context SecurityContext securityContext) {

        return Response.ok(residenceService.airConditionOperation(airConditionerOperationDto, securityContext)).build();
    }

    @Path("/windows")
    @POST
    @Consumes("application/json; charset=UTF-8")
    @RequiresRole("Resident")
    public Response windowsOperation(@Valid WindowsOperationDTO windowsOperationDto,
                                     @Context SecurityContext securityContext) {

        return Response.ok(residenceService.windowsOperation(windowsOperationDto, securityContext)).build();
    }
}
