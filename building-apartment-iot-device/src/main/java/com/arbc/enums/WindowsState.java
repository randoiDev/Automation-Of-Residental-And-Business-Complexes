package com.arbc.enums;

public enum WindowsState {

    CLOSED("Closed"),
    OPENED_HORIZONTAL("Opened horizontal"),
    OPENED_VERTICAL("Opened vertical");

    private final String state;

    WindowsState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
