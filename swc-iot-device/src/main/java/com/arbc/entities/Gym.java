package com.arbc.entities;

import com.arbc.enums.GymOperations;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.arbc.Constants.*;
import static com.arbc.enums.Resource.GYM;

public class Gym {

    private final AtomicInteger interiorHeaterTemperature = new AtomicInteger(10);
    private final AtomicInteger airConditionerTemperature = new AtomicInteger(20);
    private final AtomicInteger ordinaryLights = new AtomicInteger(0);
    private boolean interiorHeaterPower = false;
    private boolean airConditionerPower = false;

    private Gym() {}

    private static class SingletonHelper {
        private static final Gym INSTANCE = new Gym();
    }

    public static Gym getInstance() {
        return Gym.SingletonHelper.INSTANCE;
    }

    public String interiorHeaterOperation(GymOperations operation) {
        switch (operation) {
            case RAISE -> {
                AtomicReference<String> message = new AtomicReference<>("Temperature already at max (30°C).");
                int result = interiorHeaterTemperature.updateAndGet(currentTemp -> {
                    if (currentTemp < 30) {
                        System.out.println("Raising temperature to: " + (currentTemp + 1) + "°C.");
                        message.set("Raising temperature to: " + (currentTemp + 1) + "°C.");
                        currentTemp += 1;
                        return currentTemp; // For successful update
                    }
                    System.out.println("Temperature already at max (30°C).");
                    return currentTemp; // For failed update
                });
                return String.format(INTERIOR_HEATER_JSON, GYM.getResource(), result, interiorHeaterPower, message);
            }
            case LOWER -> {
                AtomicReference<String> message = new AtomicReference<>("Temperature already at min (10°C).");
                int result = interiorHeaterTemperature.updateAndGet(currentTemp -> {
                    if (currentTemp > 10) {
                        System.out.println("Lowering temperature to: " + (currentTemp - 1) + "°C.");
                        message.set("Lowering temperature to: " + (currentTemp - 1) + "°C.");
                        currentTemp -= 1;
                        return currentTemp; // For successful update
                    }
                    System.out.println("Temperature already at min (10°C).");
                    return currentTemp; // For failed update
                });
                return String.format(INTERIOR_HEATER_JSON, GYM.getResource(), result, interiorHeaterPower, message);
            }
            case POWER -> {
                interiorHeaterPower = !interiorHeaterPower;
                return String.format(INTERIOR_HEATER_JSON, GYM.getResource(), interiorHeaterTemperature.get(), interiorHeaterPower, "State of heater changed.");
            }
            default -> {
                return String.format(INTERIOR_HEATER_JSON, GYM.getResource(), interiorHeaterTemperature.get(), interiorHeaterPower, "");
            }
        }
    }

    public String airConditionerOperation(GymOperations operation) {
        switch (operation) {
            case RAISE -> {
                AtomicReference<String> message = new AtomicReference<>("Temperature already at max (25°C).");
                int result = airConditionerTemperature.updateAndGet(currentTemp -> {
                    if (currentTemp < 25) {
                        System.out.println("Raising temperature to: " + (currentTemp + 1) + "°C.");
                        message.set("Raising temperature to: " + (currentTemp + 1) + "°C.");
                        currentTemp += 1;
                        return currentTemp; // For successful update
                    }
                    System.out.println("Temperature already at max (25°C).");
                    return currentTemp; // For failed update
                });
                return String.format(AIR_CONDITIONER_JSON, GYM.getResource(), result, airConditionerPower, message);
            }
            case LOWER -> {
                AtomicReference<String> message = new AtomicReference<>("Temperature already at min (15°C).");
                int result = airConditionerTemperature.updateAndGet(currentTemp -> {
                    if (currentTemp > 15) {
                        System.out.println("Lowering temperature to: " + (currentTemp - 1) + "°C.");
                        message.set("Lowering temperature to: " + (currentTemp - 1) + "°C.");
                        currentTemp -= 1;
                        return currentTemp; // For successful update
                    }
                    System.out.println("Temperature already at min (15°C).");
                    return currentTemp; // For failed update
                });
                return String.format(AIR_CONDITIONER_JSON, GYM.getResource(), result, airConditionerPower, message);
            }
            case POWER -> {
                airConditionerPower = !airConditionerPower;
                return String.format(AIR_CONDITIONER_JSON, GYM.getResource(), airConditionerTemperature.get(), airConditionerPower, "State of air conditioner changed.");
            }
            default -> {
                return String.format(AIR_CONDITIONER_JSON, GYM.getResource(), airConditionerTemperature.get(), airConditionerPower, "");
            }
        }
    }

    public String ordinaryLightsOperation(GymOperations operation) {
        switch (operation) {
            case RAISE -> {
                AtomicReference<String> message = new AtomicReference<>("Lights already at max volume: 5.");
                int result = ordinaryLights.updateAndGet(currentLightVolume -> {
                    if (currentLightVolume < 5) {
                        System.out.println("Turning up lights to volume: " + (currentLightVolume + 1) + ".");
                        message.set("Turning up lights to volume: " + (currentLightVolume + 1) + ".");
                        currentLightVolume += 1;
                        return currentLightVolume; // For successful update
                    }
                    System.out.println("Lights already at max volume: 5.");
                    return currentLightVolume; // For failed update
                });
                return String.format(ORDINARY_LIGHTS_JSON, GYM.getResource(), result, message);
            }
            case LOWER -> {
                AtomicReference<String> message = new AtomicReference<>("Lights already at min volume: 0.");
                int result = ordinaryLights.updateAndGet(currentLightVolume -> {
                    if (currentLightVolume > 0) {
                        System.out.println("Turning down lights to volume: " + (currentLightVolume - 1) + ".");
                        message.set("Turning down lights to volume: " + (currentLightVolume - 1) + ".");
                        currentLightVolume -= 1;
                        return currentLightVolume; // For successful update
                    }
                    System.out.println("Lights already at min volume: 0.");
                    return currentLightVolume; // For failed update
                });
                return String.format(ORDINARY_LIGHTS_JSON, GYM.getResource(), result, message);
            }
            default -> {
                return String.format(ORDINARY_LIGHTS_JSON, GYM.getResource(), ordinaryLights.get(), "");
            }
        }
    }
}
