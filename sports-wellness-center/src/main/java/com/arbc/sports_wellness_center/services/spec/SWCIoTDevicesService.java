package com.arbc.sports_wellness_center.services.spec;

import com.arbc.sports_wellness_center.models.dtos.devices.*;
import com.arbc.sports_wellness_center.models.messages.Message;

public interface SWCIoTDevicesService {

    Message performActionOnInteriorHeater(InteriorHeaterOperationDTO interiorHeaterOperationDTO);

    Message performActionOnOrdinaryLights(OrdinaryLightsOperationDTO ordinaryLightsOperationDTO);

    Message performActionOnAirConditioner(AirConditionerOperationDTO airConditionerOperationDTO);

    Message performActionOnRoof(RoofOperationDTO roofOperationDTO);

    Message performActionOnChangingColorLights(ChangingColorLightsOperationDTO changingColorLightsOperationDTO);

    Message performActionOnWaterPump(WaterPumpOperationDTO waterPumpOperationDTO);


}
