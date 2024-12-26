package com.arbc.usermanagement.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import static com.arbc.usermanagement.models.Constants.*;

public record UpdatePasswordDTO(
        @NotBlank(message = OLD_PASSWORD_FIELD_REQUIRED_VIOLATION_MESSAGE)
        String oldPassword,
        @NotNull(message = NEW_PASSWORD_FIELD_REQUIRED_VIOLATION_MESSAGE)
        @Pattern(message = PASSWORD_FIELD_CONSTRAINT_VIOLATION_MESSAGE, regexp = PASSWORD_REGEX)
        String newPassword
) {
}
