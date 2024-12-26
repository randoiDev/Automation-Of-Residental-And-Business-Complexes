package com.arbc.usermanagement.models.enums;

import lombok.Getter;

@Getter
public enum Role {
    RESIDENT("Resident"),
    SPORTS_AND_WELLNESS_CENTER_WORKER("SWC worker"),
    ADMIN("Admin");

    private final String role;

    Role(String role) {
        this.role = role;
    }

}
