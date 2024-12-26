package com.arbc.residencemanagement.models.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

import static com.arbc.residencemanagement.models.Constants.*;

public record ResidenceUpdateDTO(
        @NotNull(message = RESIDENCE_NUMBERS_FIELD_REQUIRED_VIOLATION_MESSAGE)
        Set<String> residenceNumbers,
        @NotNull(message = EMAIL_FIELD_REQUIRED_VIOLATION_MESSAGE)
        String residentsEmail
) {
}
