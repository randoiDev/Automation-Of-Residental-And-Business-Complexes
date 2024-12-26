package com.arbc.enums;

public enum SwimmingPoolFilling {
    EMPTY("Empty"),
    FILLED("Filled"),
    IN_PROGRESS("In progress");

    private final String state;

    SwimmingPoolFilling(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
