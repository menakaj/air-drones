package com.air.drone.transport.exceptions;

public class DroneNotAvailableException extends RuntimeException {

    public DroneNotAvailableException(int droneId) {
       super(String.format("Drone %s is not available", droneId));
    }
}
