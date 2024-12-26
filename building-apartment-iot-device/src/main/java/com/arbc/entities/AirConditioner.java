package com.arbc.entities;

import com.arbc.enums.AirConditionerOperation;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.arbc.Constants.AIR_CONDITIONER_JSON;

public class AirConditioner {

    private final AtomicInteger temperature = new AtomicInteger(20);
    private boolean power = false;

    private AirConditioner() {}

    private static class SingletonHelper {
        private static final AirConditioner INSTANCE = new AirConditioner();
    }

    public static AirConditioner getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public String airConditionerOperation(AirConditionerOperation operation) {
        switch (operation) {
            case RAISE -> {
                AtomicReference<String> message = new AtomicReference<>("Temperature already at max (25°C).");
                int result = temperature.updateAndGet(currentTemp -> {
                    if (currentTemp < 25) {
                        System.out.println("Raising temperature to: " + (currentTemp + 1) + "°C.");
                        message.set("Raising temperature to: " + (currentTemp + 1) + "°C.");
                        currentTemp += 1;
                        return currentTemp; // For successful update
                    }
                    System.out.println("Temperature already at max (25°C).");
                    return currentTemp; // For failed update
                });
                return String.format(AIR_CONDITIONER_JSON, result, power ? "ON" : "OFF", message);
            }
            case LOWER -> {
                AtomicReference<String> message = new AtomicReference<>("Temperature already at min (15°C).");
                int result = temperature.updateAndGet(currentTemp -> {
                    if (currentTemp > 15) {
                        System.out.println("Lowering temperature to: " + (currentTemp - 1) + "°C.");
                        message.set("Lowering temperature to: " + (currentTemp - 1) + "°C.");
                        currentTemp -= 1;
                        return currentTemp; // For successful update
                    }
                    System.out.println("Temperature already at min (15°C).");
                    return currentTemp; // For failed update
                });
                return String.format(AIR_CONDITIONER_JSON, result, power ? "ON" : "OFF", message);
            }
            case POWER -> {
                power = !power;
                return String.format(AIR_CONDITIONER_JSON, temperature.get(), power ? "ON" : "OFF", "State of air conditioner changed.");
            }
            default -> {
                return String.format(AIR_CONDITIONER_JSON, temperature.get(), power ? "ON" : "OFF", "");
            }
        }
    }
}
