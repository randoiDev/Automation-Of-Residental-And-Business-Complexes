package com.arbc.entities;

import com.arbc.enums.InteriorHeaterOperation;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.arbc.Constants.INTERIOR_HEATER_JSON;

public class InteriorHeater {

    private final AtomicInteger temperature = new AtomicInteger(30);
    private boolean power = false;

    private InteriorHeater() {}

    private static class SingletonHelper {
        private static final InteriorHeater INSTANCE = new InteriorHeater();
    }

    public static InteriorHeater getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public String heaterOperation(InteriorHeaterOperation operation) {
        switch (operation) {
            case RAISE -> {
                AtomicReference<String> message = new AtomicReference<>("Temperature already at max (35°C).");
                int result = temperature.updateAndGet(currentTemp -> {
                    if (currentTemp < 35) {
                        System.out.println("Raising temperature to: " + (currentTemp + 1) + "°C.");
                        message.set("Raising temperature to: " + (currentTemp + 1) + "°C.");
                        currentTemp += 1;
                        return currentTemp; // For successful update
                    }
                    System.out.println("Temperature already at max (35°C).");
                    return currentTemp; // For failed update
                });
                return String.format(INTERIOR_HEATER_JSON, result, power ? "ON" : "OFF", message);
            }
            case LOWER -> {
                AtomicReference<String> message = new AtomicReference<>("Temperature already at min (25°C).");
                int result = temperature.updateAndGet(currentTemp -> {
                    if (currentTemp > 25) {
                        System.out.println("Lowering temperature to: " + (currentTemp - 1) + "°C.");
                        message.set("Lowering temperature to: " + (currentTemp - 1) + "°C.");
                        currentTemp -= 1;
                        return currentTemp; // For successful update
                    }
                    System.out.println("Temperature already at min (25°C).");
                    return currentTemp; // For failed update
                });
                return String.format(INTERIOR_HEATER_JSON, result, power ? "ON" : "OFF", message);
            }
            case POWER -> {
                power = !power;
                return String.format(INTERIOR_HEATER_JSON, temperature.get(), power ? "ON" : "OFF", "State of interior heater changed.");
            }
            default -> {
                return String.format(INTERIOR_HEATER_JSON, temperature.get(), power ? "ON" : "OFF", "");
            }
        }
    }
}

