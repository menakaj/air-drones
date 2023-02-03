package com.air.drone.transport.entities;

public enum DroneModel {
    LIGHT_WEIGHT(200),
    MIDDLE_WEIGHT(300),
    CRUISER_WEIGHT(400),
    HEAVY_WEIGHT(500);

    public final float maxWeight;
    DroneModel(float maxWeight) {
        this.maxWeight = maxWeight;
    }
}
