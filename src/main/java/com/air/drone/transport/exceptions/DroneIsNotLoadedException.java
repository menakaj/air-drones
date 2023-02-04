package com.air.drone.transport.exceptions;

public class DroneIsNotLoadedException extends RuntimeException {
    public DroneIsNotLoadedException(int droneId) {
        super(String.format("The drone %d is not loaded", droneId));
    }
}
