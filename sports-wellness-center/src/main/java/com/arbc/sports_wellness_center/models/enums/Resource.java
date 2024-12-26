package com.arbc.sports_wellness_center.models.enums;

import lombok.Getter;

@Getter
public enum Resource {

    SWIMMING_POOL("Swimming pool"),
    GYM("Gym"),
    SAUNA("Sauna");

    private final String resource;

    Resource(String resource) {
        this.resource = resource;
    }

}
