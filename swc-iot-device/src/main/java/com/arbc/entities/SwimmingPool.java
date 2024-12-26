package com.arbc.entities;

import com.arbc.SWCIoTDevice;
import com.arbc.enums.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.arbc.Constants.*;
import static com.arbc.enums.Resource.SWIMMING_POOL;
import static com.arbc.enums.SwimmingPoolFilling.EMPTY;
import static com.arbc.enums.SwimmingPoolFilling.FILLED;
import static com.arbc.enums.SwimmingPoolLights.NO_COLOR;
import static com.arbc.enums.SwimmingPoolRoof.*;

public class SwimmingPool {

    private final AtomicReference<SwimmingPoolLights> changingColorLights = new AtomicReference<>(NO_COLOR);
    private final AtomicReference<SwimmingPoolRoof> roof = new AtomicReference<>(CLOSED);
    private final AtomicReference<SwimmingPoolFilling> waterPump = new AtomicReference<>(FILLED);
    private final AtomicInteger interiorHeaterTemperature = new AtomicInteger(30);
    private final AtomicInteger ordinaryLights = new AtomicInteger(0);
    private boolean interiorHeaterPower = false;
    private final int operationProcessTime = 10000; // milliseconds

    private SwimmingPool() {}

    private static class SingletonHelper {
        private static final SwimmingPool INSTANCE = new SwimmingPool();
    }

    public static SwimmingPool getInstance() {
        return SwimmingPool.SingletonHelper.INSTANCE;
    }

    public String roofAction(SwimmingPoolOperations operation) {
        switch (operation) {
            case OPEN -> {
                AtomicReference<String> message = new AtomicReference<>("Swimming pool roof is now being opened.");
                SwimmingPoolRoof result = roof.updateAndGet(currentState -> {
                    if(currentState == OPENED) {
                        message.set("Swimming pool roof is already opened.");
                    } else if(currentState == SwimmingPoolRoof.IN_PROGRESS) {
                        message.set("Another swimming pool roof operation is in progress.");
                    } else {
                        currentState = SwimmingPoolRoof.IN_PROGRESS;
                        Thread worker = new Thread(() -> {
                            try {
                                Thread.sleep(operationProcessTime);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            boolean operationFlag = this.roof.compareAndSet(SwimmingPoolRoof.IN_PROGRESS, OPENED);
                            if(operationFlag) {
                                System.out.println("Swimming pool roof is now opened.");
                                SWCIoTDevice.publish(PUBLISHER_TOPIC_ROOF,
                                        String.format(ROOF_JSON, SWIMMING_POOL.getResource(), OPENED.getState(), "Swimming pool roof is now opened."));
                            }
                        });
                        worker.start();
                    }
                    return currentState;
                });
                System.out.println(message.get());
                return String.format(ROOF_JSON, SWIMMING_POOL.getResource(), result.getState(), message.get());
            }
            case CLOSE -> {
                AtomicReference<String> message = new AtomicReference<>("Swimming pool roof is now being closed.");
                SwimmingPoolRoof result = roof.updateAndGet(currentState -> {
                    if(currentState == CLOSED) {
                        message.set("Swimming pool roof is already closed.");
                    } else if(currentState == SwimmingPoolRoof.IN_PROGRESS) {
                        message.set("Another swimming pool roof operation is in progress.");
                    } else {
                        currentState = SwimmingPoolRoof.IN_PROGRESS;
                        Thread worker = new Thread(() -> {
                            try {
                                Thread.sleep(operationProcessTime);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            boolean operationFlag = this.roof.compareAndSet(SwimmingPoolRoof.IN_PROGRESS, CLOSED);
                            if(operationFlag) {
                                System.out.println("Swimming pool roof is now closed.");
                                SWCIoTDevice.publish(PUBLISHER_TOPIC_ROOF,
                                        String.format(ROOF_JSON, SWIMMING_POOL.getResource(), CLOSED.getState(), "Swimming pool roof is now closed."));
                            }
                        });
                        worker.start();
                    }
                    return currentState;
                });
                System.out.println(message.get());
                return String.format(ROOF_JSON, SWIMMING_POOL.getResource(), result.getState(), message.get());
            }
            default -> {
                return String.format(ROOF_JSON, SWIMMING_POOL.getResource(), roof.get().getState(), "");
            }
        }
    }

    public String waterPumpAction(SwimmingPoolOperations operation) {
        switch (operation) {
            case FILL -> {
                AtomicReference<String> message = new AtomicReference<>("Swimming pool is now being filled.");
                SwimmingPoolFilling result = waterPump.updateAndGet(currentState -> {
                    if(currentState == FILLED) {
                        message.set("Swimming pool is already filled.");
                    } else if(currentState == SwimmingPoolFilling.IN_PROGRESS) {
                        message.set("Another swimming pool filling operation is in progress.");
                    } else {
                        currentState = SwimmingPoolFilling.IN_PROGRESS;
                        Thread worker = new Thread(() -> {
                            try {
                                Thread.sleep(operationProcessTime);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            boolean operationFlag = this.waterPump.compareAndSet(SwimmingPoolFilling.IN_PROGRESS, FILLED);
                            if(operationFlag) {
                                System.out.println("Swimming pool is now filled.");
                                SWCIoTDevice.publish(PUBLISHER_TOPIC_WATER_PUMP,
                                        String.format(WATER_PUMP_JSON, SWIMMING_POOL.getResource(), FILLED.getState(), "Swimming pool is now filled."));
                            }
                        });
                        worker.start();
                    }
                    return currentState;
                });
                System.out.println(message.get());
                return String.format(WATER_PUMP_JSON, SWIMMING_POOL.getResource(), result.getState(), message.get());
            }
            case EMPTY -> {
                AtomicReference<String> message = new AtomicReference<>("Emptying swimming pool now.");
                SwimmingPoolFilling result = waterPump.updateAndGet(currentState -> {
                    if(currentState == EMPTY) {
                        message.set("Swimming pool is already empty.");
                    } else if(currentState == SwimmingPoolFilling.IN_PROGRESS) {
                        message.set("Another swimming pool filling operation is in progress.");
                    } else {
                        currentState = SwimmingPoolFilling.IN_PROGRESS;
                        Thread worker = new Thread(() -> {
                            try {
                                Thread.sleep(operationProcessTime);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            boolean operationFlag = this.waterPump.compareAndSet(SwimmingPoolFilling.IN_PROGRESS, EMPTY);
                            if(operationFlag) {
                                System.out.println("Swimming pool is emptied now.");
                                SWCIoTDevice.publish(PUBLISHER_TOPIC_WATER_PUMP,
                                        String.format(WATER_PUMP_JSON, SWIMMING_POOL.getResource(), EMPTY.getState(), "Swimming pool is now emptied."));
                            }
                        });
                        worker.start();
                    }
                    return currentState;
                });
                System.out.println(message.get());
                return String.format(WATER_PUMP_JSON, SWIMMING_POOL.getResource(), result.getState(), message.get());
            }
            default -> {
                return String.format(WATER_PUMP_JSON, SWIMMING_POOL.getResource(), waterPump.get().getState(), "");
            }
        }
    }

    public String changingColorLightsAction(SwimmingPoolLights lights) {
        SwimmingPoolLights light = changingColorLights.updateAndGet(currentPoolLight -> {
            currentPoolLight = lights;
            return currentPoolLight;
        });
        return String.format(CHANGING_COLOR_LIGHTS_JSON, SWIMMING_POOL.getResource(), changingColorLights.get().getColor(), "Pool lights changed.");
    }

    public String interiorHeaterOperation(SwimmingPoolOperations operation) {
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
                return String.format(INTERIOR_HEATER_JSON, SWIMMING_POOL.getResource(), result, interiorHeaterPower, message);
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
                return String.format(INTERIOR_HEATER_JSON, SWIMMING_POOL.getResource(), result, interiorHeaterPower, message);
            }
            case POWER -> {
                interiorHeaterPower = !interiorHeaterPower;
                return String.format(INTERIOR_HEATER_JSON, SWIMMING_POOL.getResource(), interiorHeaterTemperature.get(), interiorHeaterPower, "");
            }
            default -> {
                return String.format(INTERIOR_HEATER_JSON, SWIMMING_POOL.getResource(), interiorHeaterTemperature.get(), interiorHeaterPower,  "");
            }
        }
    }

    public String ordinaryLightsOperation(SwimmingPoolOperations operation) {
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
                return String.format(ORDINARY_LIGHTS_JSON, SWIMMING_POOL.getResource(), result, message);
            }
            case LOWER -> {
                AtomicReference<String> message = new AtomicReference<>("Lights already at min volume: 5.");
                int result = ordinaryLights.updateAndGet(currentLightVolume -> {
                    if (currentLightVolume > 0) {
                        System.out.println("Turning down lights to volume: " + (currentLightVolume - 1) + ".");
                        message.set("Turning down lights to volume: " + (currentLightVolume - 1) + ".");
                        currentLightVolume -= 1;
                        return currentLightVolume; // For successful update
                    }
                    System.out.println("Lights already at min volume: 5.");
                    return currentLightVolume; // For failed update
                });
                return String.format(ORDINARY_LIGHTS_JSON, SWIMMING_POOL.getResource(), result, message);
            }
            default -> {
                return String.format(ORDINARY_LIGHTS_JSON, SWIMMING_POOL.getResource(), ordinaryLights.get(), "");
            }
        }
    }
}
