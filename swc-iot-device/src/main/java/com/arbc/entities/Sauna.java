package com.arbc.entities;

import com.arbc.enums.SaunaOperations;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.arbc.Constants.*;
import static com.arbc.enums.Resource.SAUNA;

public class Sauna {

    private final AtomicInteger interiorHeaterTemperature = new AtomicInteger(30);
    private final AtomicInteger ordinaryLights = new AtomicInteger(0);
    private boolean interiorHeaterPower = false;

    private Sauna() {}

    private static class SingletonHelper {
        private static final Sauna INSTANCE = new Sauna();
    }

    public static Sauna getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public String interiorHeaterOperation(SaunaOperations operation) {
        switch (operation) {
            case RAISE -> {
                AtomicReference<String> message = new AtomicReference<>("Temperature already at max (35°C).");
                int result = interiorHeaterTemperature.updateAndGet(currentTemp -> {
                    if (currentTemp < 35) {
                        System.out.println("Raising temperature to: " + (currentTemp + 1) + "°C.");
                        message.set("Raising temperature to: " + (currentTemp + 1) + "°C.");
                        currentTemp += 1;
                        return currentTemp; // For successful update
                    }
                    System.out.println("Temperature already at max (35°C).");
                    return currentTemp; // For failed update
                });
                return String.format(INTERIOR_HEATER_JSON, SAUNA.getResource(), result, interiorHeaterPower, message);
            }
            case LOWER -> {
                AtomicReference<String> message = new AtomicReference<>("Temperature already at min (25°C).");
                int result = interiorHeaterTemperature.updateAndGet(currentTemp -> {
                    if (currentTemp > 25) {
                        System.out.println("Lowering temperature to: " + (currentTemp - 1) + "°C.");
                        message.set("Lowering temperature to: " + (currentTemp - 1) + "°C.");
                        currentTemp -= 1;
                        return currentTemp; // For successful update
                    }
                    System.out.println("Temperature already at min (25°C).");
                    return currentTemp; // For failed update
                });
                return String.format(INTERIOR_HEATER_JSON, SAUNA.getResource(), result, interiorHeaterPower, message);
            }
            case POWER -> {
                interiorHeaterPower = !interiorHeaterPower;
                return String.format(INTERIOR_HEATER_JSON, SAUNA.getResource(), interiorHeaterTemperature.get(), interiorHeaterPower, "");
            }
            default -> {
                return String.format(INTERIOR_HEATER_JSON, SAUNA.getResource(), interiorHeaterTemperature.get(), interiorHeaterPower,  "");
            }
        }
    }

    public String ordinaryLightsOperation(SaunaOperations operation) {
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
                return String.format(ORDINARY_LIGHTS_JSON, SAUNA.getResource(), result, message);
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
                return String.format(ORDINARY_LIGHTS_JSON, SAUNA.getResource(), result, message);
            }
            default -> {
                return String.format(ORDINARY_LIGHTS_JSON, SAUNA.getResource(), ordinaryLights.get(), "");
            }
        }
    }
}
