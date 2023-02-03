package com.air.drone.transport.exceptions;

public class DroneNotFoundException extends RuntimeException {

    public DroneNotFoundException(int id) {
        super(String.format("Drone with id %d not found", id));
    }

}
