package com.arbc.residencemanagement.models.dto;

import com.arbc.residencemanagement.models.enums.AirConditionerOperation;
import jakarta.validation.constraints.NotNull;

import static com.arbc.residencemanagement.models.Constants.*;

public record AirConditionerOperationDTO(
        @NotNull(message = RESIDENCE_NUMBER_FIELD_REQUIRED_VIOLATION_MESSAGE)
        String residenceNumber,
        @NotNull(message = AIR_CONDITIONER_OPERATION_FIELD_REQUIRED_VIOLATION_MESSAGE)
        AirConditionerOperation action
) {

    @Override
    public String toString() {
        return "{\"action\": \"" + action.name() + "\"}";
    }
}
