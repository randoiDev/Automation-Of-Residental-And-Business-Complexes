package com.arbc.sports_wellness_center.models.enums;

import lombok.Getter;

@Getter
public enum AppointmentResource {

    GYM("Gym"),
    SAUNA("Sauna");

    private final String resource;

    AppointmentResource(String resource) {
        this.resource = resource;
    }

}
