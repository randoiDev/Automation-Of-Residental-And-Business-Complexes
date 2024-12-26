package com.arbc.usermanagement.models.dtos.workers;

import jakarta.validation.constraints.NotBlank;

import static com.arbc.usermanagement.models.Constants.*;

public record LoginWorkerDTO(
        @NotBlank(message = USERNAME_FIELD_REQUIRED_VIOLATION_MESSAGE) String username,
        @NotBlank(message = PASSWORD_FIELD_REQUIRED_VIOLATION_MESSAGE) String password
) {
}
