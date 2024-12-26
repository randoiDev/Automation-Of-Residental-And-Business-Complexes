package com.arbc.enums;

public enum SwimmingPoolRoof {
    OPENED("Opened"),
    CLOSED("Closed"),
    IN_PROGRESS("In progress");

    private final String state;

    SwimmingPoolRoof(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
