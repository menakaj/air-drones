package com.air.drone.transport.drone;

public enum DroneModelType {
    LIGHT_WEIGHT(200),
    MIDDLE_WEIGHT(300),
    CRUISER_WEIGHT(400),
    HEAVY_WEIGHT(500);

    public final float maxWeight;
    DroneModelType(float maxWeight) {
        this.maxWeight = maxWeight;
    }
}
