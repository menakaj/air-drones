package com.air.drone.transport.exceptions;

public class LowBatteryException extends RuntimeException {
    public LowBatteryException(int id) {
        super("Drone battery level is too low");
    }
}
