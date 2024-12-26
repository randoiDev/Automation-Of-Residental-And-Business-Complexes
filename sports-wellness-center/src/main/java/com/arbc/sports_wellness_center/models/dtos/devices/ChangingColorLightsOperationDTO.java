package com.arbc.sports_wellness_center.models.dtos.devices;

import com.arbc.sports_wellness_center.models.enums.Resource;
import com.arbc.sports_wellness_center.models.enums.devices.LightsColor;
import jakarta.validation.constraints.NotNull;

import static com.arbc.sports_wellness_center.models.Constants.*;

public record ChangingColorLightsOperationDTO(
        @NotNull(message = RESOURCE_FIELD_REQUIRED_VIOLATION_MESSAGE)
        Resource resource,
        @NotNull(message = LIGHTS_COLOR_FIELD_REQUIRED_VIOLATION_MESSAGE)
        LightsColor color
) {

        @Override
        public String toString() {
                return String.format("{\"resource\": \"%s\" , \"color\": \"%s\"}", resource.name(), color.name());
        }

}
