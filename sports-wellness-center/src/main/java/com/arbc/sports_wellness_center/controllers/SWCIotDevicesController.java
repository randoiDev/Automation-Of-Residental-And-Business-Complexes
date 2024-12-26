package com.arbc.sports_wellness_center.controllers;

import com.arbc.sports_wellness_center.filters.security.annotations.RequiresRole;
import com.arbc.sports_wellness_center.models.dtos.devices.*;
import com.arbc.sports_wellness_center.services.spec.SWCIoTDevicesService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/devices")
@Produces("application/json; charset=UTF-8")
public class SWCIotDevicesController {

    @Inject
    SWCIoTDevicesService swcIoTDevicesService;

    @Path("/interior-heater")
    @POST
    @Consumes("application/json; charset=UTF-8")
    @RequiresRole("SWC worker")
    public Response performInteriorHeaterOperation(@Valid InteriorHeaterOperationDTO heaterOperationDTO) {

        return Response.ok(swcIoTDevicesService.performActionOnInteriorHeater(heaterOperationDTO)).build();
    }

    @Path("/ordinary-lights")
    @POST
    @Consumes("application/json; charset=UTF-8")
    @RequiresRole("SWC worker")
    public Response performOrdinaryLightsOperation(@Valid OrdinaryLightsOperationDTO ordinaryLightsOperationDTO) {

        return Response.ok(swcIoTDevicesService.performActionOnOrdinaryLights(ordinaryLightsOperationDTO)).build();
    }

    @Path("/air-conditioner")
    @POST
    @Consumes("application/json; charset=UTF-8")
    @RequiresRole("SWC worker")
    public Response performAirConditionerOperation(@Valid AirConditionerOperationDTO airConditionerOperationDTO) {

        return Response.ok(swcIoTDevicesService.performActionOnAirConditioner(airConditionerOperationDTO)).build();
    }

    @Path("/roof")
    @POST
    @Consumes("application/json; charset=UTF-8")
    @RequiresRole("SWC worker")
    public Response performRoofOperation(@Valid RoofOperationDTO roofOperationDTO) {

        return Response.ok(swcIoTDevicesService.performActionOnRoof(roofOperationDTO)).build();
    }

    @Path("/changing-color-lights")
    @POST
    @Consumes("application/json; charset=UTF-8")
    @RequiresRole("SWC worker")
    public Response performColorChangingLightsOperation(@Valid ChangingColorLightsOperationDTO changingColorLightsOperationDTO) {

        return Response.ok(swcIoTDevicesService.performActionOnChangingColorLights(changingColorLightsOperationDTO)).build();
    }

    @Path("/water-pump")
    @POST
    @Consumes("application/json; charset=UTF-8")
    @RequiresRole("SWC worker")
    public Response performWaterPumpOperation(@Valid WaterPumpOperationDTO waterPumpOperationDTO) {

        return Response.ok(swcIoTDevicesService.performActionOnWaterPump(waterPumpOperationDTO)).build();
    }
}
