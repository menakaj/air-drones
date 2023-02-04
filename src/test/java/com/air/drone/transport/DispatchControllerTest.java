package com.air.drone.transport;

import com.air.drone.transport.assemblers.DroneAssembler;
import com.air.drone.transport.controllers.DispatchController;
import com.air.drone.transport.drone.Drone;
import com.air.drone.transport.drone.DroneModel;
import com.air.drone.transport.drone.DroneService;
import com.air.drone.transport.item.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.util.Assert;

@ExtendWith(MockitoExtension.class)
public class DispatchControllerTest {

    @Mock
    private DroneService droneService;

    @Mock
    private ItemService itemService;

    private DroneAssembler droneAssembler = new DroneAssembler();
    @InjectMocks
    private DispatchController dispatchController;

    @BeforeEach
    void initDispatchController() {
        dispatchController = new DispatchController(droneService, itemService, droneAssembler);
    }

    @Test
    public void testRegisterDrone() {
        Drone mockDrone = new Drone(DroneModel.HEAVY_WEIGHT, 100.0, "DRONE_1");
        mockDrone.setId(1);
        Mockito.when(droneService.addDrone(Mockito.any(Drone.class))).thenReturn(mockDrone);
        Drone drone = new Drone(DroneModel.HEAVY_WEIGHT, 100.0, "DRONE_1");
        EntityModel<Drone> droneEntityModel = dispatchController.registerDrone(drone);
        Assert.isTrue(droneEntityModel != null);
    }

}
