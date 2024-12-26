package com.arbc.usermanagement.models.dtos.workers;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import static com.arbc.usermanagement.models.Constants.*;

public record UpdateMobileNumberDTO(
        @NotNull(message = MOBILE_NUMBER_FIELD_REQUIRED_VIOLATION_MESSAGE)
        @Pattern(message = MOBILE_NUMBER_FIELD_REGEX_VIOLATION_MESSAGE, regexp = MOBILE_NUMBER_REGEX)
        String mobileNumber
) {
}
