package com.air.drone.transport;

import com.air.drone.transport.drone.Drone;
import com.air.drone.transport.drone.DroneModelType;
import com.air.drone.transport.drone.DroneState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class DroneTest {


    private final String TEST_DRONE_SERiIAL_NUMBER = "TEST_DRONE_1";

    @Test
    public void testCreateDroneInstance() {
        Drone drone = new Drone(DroneModelType.HEAVY_WEIGHT, new BigDecimal("100.0"), TEST_DRONE_SERiIAL_NUMBER);
        Assertions.assertEquals(drone.getState(), DroneState.IDLE);
        Assertions.assertEquals(drone.getMaxWeight(), DroneModelType.HEAVY_WEIGHT.maxWeight);
    }
}
