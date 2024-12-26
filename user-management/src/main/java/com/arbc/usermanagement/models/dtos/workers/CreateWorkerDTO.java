package com.arbc.usermanagement.models.dtos.workers;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import static com.arbc.usermanagement.models.Constants.*;
import static com.arbc.usermanagement.models.Constants.SURNAME_FIELD_REGEX_VIOLATION_MESSAGE;

public record CreateWorkerDTO(
        @NotNull(message = NAME_FIELD_REQUIRED_VIOLATION_MESSAGE)
        @Pattern(regexp = NAME_SURNAME_REGEX, message = NAME_FIELD_REGEX_VIOLATION_MESSAGE)
        String name,
        @NotNull(message = SURNAME_FIELD_REQUIRED_VIOLATION_MESSAGE)
        @Pattern(regexp = NAME_SURNAME_REGEX, message = SURNAME_FIELD_REGEX_VIOLATION_MESSAGE)
        String surname,
        @NotNull(message = MOBILE_NUMBER_FIELD_REQUIRED_VIOLATION_MESSAGE)
        @Pattern(regexp = MOBILE_NUMBER_REGEX, message = MOBILE_NUMBER_FIELD_REGEX_VIOLATION_MESSAGE)
        String mobileNumber,
        @NotNull(message = EMAIL_FIELD_REQUIRED_VIOLATION_MESSAGE)
        @Pattern(regexp = EMAIL_REGEX, message = EMAIL_FIELD_REGEX_VIOLATION_MESSAGE)
        String email
) {
}
