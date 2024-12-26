package com.arbc.entities;

import com.arbc.enums.WindowsLocation;
import com.arbc.enums.WindowsOperation;
import com.arbc.enums.WindowsState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.arbc.Constants.WINDOWS_JSON;
import static com.arbc.enums.WindowsState.*;

public class Windows {

    private final Map<WindowsLocation, WindowsState> windowsStates;

    private Windows() {
        windowsStates = new ConcurrentHashMap<>(4);
        windowsStates.put(WindowsLocation.LIVING_ROOM, WindowsState.CLOSED);
        windowsStates.put(WindowsLocation.BED_ROOM, WindowsState.CLOSED);
        windowsStates.put(WindowsLocation.KITCHEN, WindowsState.CLOSED);
        windowsStates.put(WindowsLocation.BATH_ROOM, WindowsState.CLOSED);
    }

    private static class SingletonHelper {
        private static final Windows INSTANCE = new Windows();
    }

    public static Windows getInstance() {
        return Windows.SingletonHelper.INSTANCE;
    }

    public String windowsOperation(WindowsOperation operation, WindowsLocation location) {
        switch (operation) {
            case OPEN_VERTICALLY -> {
                if(windowsStates.get(location) == OPENED_VERTICAL)
                    return String.format(WINDOWS_JSON, location.getLocation(), OPENED_VERTICAL.getState(), location.getLocation() + " windows are already in desired state.");
                windowsStates.computeIfPresent(location, (id, currentState) -> {
                    try {
                        Thread.sleep(5000); // Simulation of 5 seconds for operation to complete
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Opening windows in " + location.getLocation().toLowerCase() + " vertically.");
                    windowsStates.put(id, OPENED_VERTICAL);
                    return OPENED_VERTICAL;
                });
                return String.format(WINDOWS_JSON, location.getLocation(), OPENED_VERTICAL.getState(), location.getLocation() + " windows are now opened vertically.");
            }
            case OPEN_HORIZONTALLY -> {
                if(windowsStates.get(location) == OPENED_HORIZONTAL)
                    return String.format(WINDOWS_JSON, location.getLocation(), OPENED_HORIZONTAL.getState(), location.getLocation() + " windows are already in desired state.");
                windowsStates.computeIfPresent(location, (id, currentState) -> {
                    try {
                        Thread.sleep(5000); // Simulation of 5 seconds for operation to complete
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Opening windows in " + location.getLocation().toLowerCase() + " horizontally.");
                    windowsStates.put(id, OPENED_HORIZONTAL);
                    return OPENED_HORIZONTAL;
                });
                return String.format(WINDOWS_JSON, location.getLocation(), OPENED_HORIZONTAL.getState(), location.getLocation() +  " windows are now opened horizontally.");
            }
            case CLOSE -> {
                if(windowsStates.get(location) == CLOSED)
                    return String.format(WINDOWS_JSON, location.getLocation(), CLOSED.getState(), location.getLocation() + " windows are already in desired state.");
                windowsStates.computeIfPresent(location, (id, currentState) -> {
                    if(currentState == CLOSED)
                        return currentState;
                    try {
                        Thread.sleep(5000); // Simulation of 5 seconds for operation to complete
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Closing windows in " + location.getLocation().toLowerCase() + ".");
                    windowsStates.put(id, CLOSED);
                    return CLOSED;
                });
                return String.format(WINDOWS_JSON, location.getLocation(), CLOSED.getState(), location.getLocation() +  " windows are now closed.");
            }
            default -> {
                return String.format(WINDOWS_JSON, location.getLocation(), windowsStates.get(location).getState(), location.getLocation() +  " windows are in " + windowsStates.get(location).getState().toLowerCase() +".");

            }
        }
    }
}

