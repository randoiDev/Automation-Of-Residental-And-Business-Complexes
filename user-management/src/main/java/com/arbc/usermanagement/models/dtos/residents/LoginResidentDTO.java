package com.arbc.usermanagement.models.dtos.residents;

import jakarta.validation.constraints.NotBlank;

import static com.arbc.usermanagement.models.Constants.*;

public record LoginResidentDTO(
        @NotBlank(message = EMAIL_FIELD_REQUIRED_VIOLATION_MESSAGE) String email,
        @NotBlank(message = PASSWORD_FIELD_REQUIRED_VIOLATION_MESSAGE) String password
) {
}
