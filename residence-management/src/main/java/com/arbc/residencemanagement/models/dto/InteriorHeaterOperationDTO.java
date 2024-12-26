package com.arbc.residencemanagement.models.dto;

import com.arbc.residencemanagement.models.enums.InteriorHeaterOperation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import static com.arbc.residencemanagement.models.Constants.HEATER_OPERATION_FIELD_REQUIRED_VIOLATION_MESSAGE;
import static com.arbc.residencemanagement.models.Constants.RESIDENCE_NUMBER_FIELD_REQUIRED_VIOLATION_MESSAGE;

public record InteriorHeaterOperationDTO(
        @NotBlank(message = RESIDENCE_NUMBER_FIELD_REQUIRED_VIOLATION_MESSAGE)
        String residenceNumber,
        @NotNull(message = HEATER_OPERATION_FIELD_REQUIRED_VIOLATION_MESSAGE)
        InteriorHeaterOperation action
) {

    @Override
    public String toString() {
        return "{\"action\": \"" + action.name() + "\"}";
    }
}
