package com.air.drone.transport.exceptions;

public class DroneAlreadyDispatchedException extends RuntimeException {
    public DroneAlreadyDispatchedException(int droneId) {
        super(String.format("Drone %d is already dispatched", droneId));
    }
}
