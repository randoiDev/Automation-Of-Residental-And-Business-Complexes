package com.arbc.residencemanagement.models.dto;

import com.arbc.residencemanagement.models.enums.WindowsLocation;
import com.arbc.residencemanagement.models.enums.WindowsOperation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import static com.arbc.residencemanagement.models.Constants.*;

public record WindowsOperationDTO(
        @NotBlank(message = RESIDENCE_NUMBER_FIELD_REQUIRED_VIOLATION_MESSAGE)
        String residenceNumber,
        @NotNull(message = WINDOWS_LOCATION_FIELD_REQUIRED_VIOLATION_MESSAGE)
        WindowsLocation location,
        @NotNull(message = WINDOWS_OPERATION_FIELD_REQUIRED_VIOLATION_MESSAGE)
        WindowsOperation action
) {

        @Override
        public String toString() {
                return "{\"action\": \"" + action.name() + "\", \"location\": \"" + location.name() + "\"}";
        }
}
