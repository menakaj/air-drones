package com.air.drone.transport.exceptions;

public class DroneOverweightException extends RuntimeException {

    public DroneOverweightException(int id) {
        super(String.format("The drone %d is overweight", id));
    }

}
