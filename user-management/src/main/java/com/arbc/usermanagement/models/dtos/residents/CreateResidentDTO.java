package com.arbc.usermanagement.models.dtos.residents;

import jakarta.validation.constraints.*;

import static com.arbc.usermanagement.models.Constants.*;

public record CreateResidentDTO(
        @NotNull(message = NAME_FIELD_REQUIRED_VIOLATION_MESSAGE)
        @Pattern(regexp = NAME_SURNAME_REGEX, message = NAME_FIELD_REGEX_VIOLATION_MESSAGE)
        String name,
        @NotNull(message = SURNAME_FIELD_REQUIRED_VIOLATION_MESSAGE)
        @Pattern(regexp = NAME_SURNAME_REGEX, message = SURNAME_FIELD_REGEX_VIOLATION_MESSAGE)
        String surname,
        @NotNull(message = EMAIL_FIELD_REQUIRED_VIOLATION_MESSAGE)
        @Pattern(regexp = EMAIL_REGEX, message = EMAIL_FIELD_REGEX_VIOLATION_MESSAGE)
        String email
) {
}
