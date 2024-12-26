package com.arbc.enums;

public enum WindowsLocation {

    LIVING_ROOM("Living-room"),
    BED_ROOM("Bed-room"),
    KITCHEN("Kitchen"),
    BATH_ROOM("Bath-room");

    private final String location;

    WindowsLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }
}
