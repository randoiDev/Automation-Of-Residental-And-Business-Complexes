package com.arbc.enums;

public enum Resource {

    SWIMMING_POOL("Swimming-pool"),
    GYM("Gym"),
    SAUNA("Sauna");

    private final String resource;

    Resource(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }

}
