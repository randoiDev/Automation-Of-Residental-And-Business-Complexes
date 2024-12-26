package com.arbc.sports_wellness_center.models.dtos.devices;

import com.arbc.sports_wellness_center.models.enums.Resource;
import com.arbc.sports_wellness_center.models.enums.devices.AirConditionerAction;
import jakarta.validation.constraints.NotNull;

import static com.arbc.sports_wellness_center.models.Constants.DEVICE_ACTION_FIELD_REQUIRED_VIOLATION_MESSAGE;
import static com.arbc.sports_wellness_center.models.Constants.RESOURCE_FIELD_REQUIRED_VIOLATION_MESSAGE;

public record AirConditionerOperationDTO(
        @NotNull(message = RESOURCE_FIELD_REQUIRED_VIOLATION_MESSAGE)
        Resource resource,
        @NotNull(message = DEVICE_ACTION_FIELD_REQUIRED_VIOLATION_MESSAGE)
        AirConditionerAction action
) {

        @Override
        public String toString() {
                return String.format("{\"resource\": \"%s\" , \"action\": \"%s\"}", resource.name(), action.name());
        }

}
