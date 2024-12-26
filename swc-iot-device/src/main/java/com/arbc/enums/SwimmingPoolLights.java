package com.arbc.enums;

public enum SwimmingPoolLights {
    BLUE("Blue"),
    RED("Red"),
    GREEN("Green"),
    PURPLE("Purple"),
    NO_COLOR("No color");

    private final String color;

    SwimmingPoolLights(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
