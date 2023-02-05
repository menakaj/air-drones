package com.air.drone.transport;

import com.air.drone.transport.assemblers.DroneAssembler;
import com.air.drone.transport.controllers.DispatchController;
import com.air.drone.transport.drone.Drone;
import com.air.drone.transport.drone.DroneModelType;
import com.air.drone.transport.drone.DroneRepository;
import com.air.drone.transport.drone.DroneService;
import com.air.drone.transport.exceptions.DroneNotFoundException;
import com.air.drone.transport.exceptions.DroneOverweightException;
import com.air.drone.transport.item.Item;
import com.air.drone.transport.item.ItemRepository;
import com.air.drone.transport.item.ItemService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@SpringBootTest
@ActiveProfiles("test")
public class DispatchControllerTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private DroneRepository droneRepository;

    private DroneAssembler droneAssembler = new DroneAssembler();
    private DispatchController dispatchController;

    private final String TEST_DRONE_SERIAL_NUMBER = "TEST_DRONE_1";
    private final String TEST_DRONE_SERIAL_NUMBER_2 = "TEST_DRONE_2";
    private final String TEST_DRONE_SERIAL_NUMBER_3 = "TEST_DRONE_3";
    private final String TEST_DRONE_SERIAL_NUMBER_ERROR = "ABCNDHDJMFJFYHJSD_SKSDKJSA-ASJJKDHSAKHDSAKDHSAKHDL" +
            "KSAHDLKASHDLKJSAHDKLJASHDLKSAHDLKSAHDKLASJHDLKAJSHDLKSAJHDLKASIOEYIROUEQPO";
    @BeforeEach
    public void initDispatchController() {
        DroneService droneService = new DroneService(droneRepository);
        ItemService itemService = new ItemService(itemRepository);
        dispatchController = new DispatchController(droneService, itemService, droneAssembler);
    }

    @Test
    public void testRegisterDrone() {
        Drone mockDrone = new Drone(DroneModelType.HEAVY_WEIGHT, new BigDecimal("100.0"), TEST_DRONE_SERIAL_NUMBER);
        EntityModel<Drone> droneEntityModel = dispatchController.registerDrone(mockDrone);
        Assertions.assertNotNull(droneEntityModel);
        Assertions.assertNotNull(droneEntityModel.getContent());
        Assertions.assertEquals(
                Objects.requireNonNull(droneEntityModel.getContent()).getModel(), DroneModelType.HEAVY_WEIGHT);
    }

    @Test
    public void testRegisterDroneInvalidSerialNumber() {
        Drone mockDrone = new Drone(
                DroneModelType.HEAVY_WEIGHT,
                new BigDecimal("100.0"),
                TEST_DRONE_SERIAL_NUMBER_ERROR);
        Assertions.assertThrows(ConstraintViolationException.class, ()-> dispatchController.registerDrone(mockDrone));
    }

    @Test
    public void testRegisterDroneInvalidBatteryLevel() {
        Drone mockDrone = new Drone(
                DroneModelType.HEAVY_WEIGHT,
                new BigDecimal("100.0"),
                TEST_DRONE_SERIAL_NUMBER_ERROR);
        Assertions.assertThrows(ConstraintViolationException.class, () -> dispatchController.registerDrone(mockDrone));
    }

    @Test
    public void testGetDroneBatteryLevel() {
        Drone mockDrone = new Drone(DroneModelType.HEAVY_WEIGHT, new BigDecimal("100.0"), TEST_DRONE_SERIAL_NUMBER_2);
        EntityModel<Drone> droneEntityModel = dispatchController.registerDrone(mockDrone);
        String level = dispatchController.getDroneBatteryLevel(
                Objects.requireNonNull(droneEntityModel.getContent()).getId());
        Assertions.assertTrue(level.contains("100"));
    }

    @Test
    public void testGetAllAvailableDrones() {
        CollectionModel<EntityModel<Drone>> drones = dispatchController.getDrones("available");
        Assertions.assertTrue(drones.getContent().size() > 0);
    }

    @Test
    public void testGetNonExistingDrone() {
        Assertions.assertThrows(DroneNotFoundException.class, () -> dispatchController.getDrone(23));
    }

    @Test
    public void testLoadDrone() {

        Drone mockDrone = new Drone(DroneModelType.LIGHT_WEIGHT, new BigDecimal("100.0"), TEST_DRONE_SERIAL_NUMBER_3);
        Drone drone = dispatchController.registerDrone(mockDrone).getContent();

        Assertions.assertNotNull(drone);

        Item item = new Item(TestConstants.ITEM_NAME, TestConstants.ITEM_CODE, TestConstants.ITEM_WEIGHT, "");

        Drone loadedDrone = dispatchController.loadDrone(drone.getId(), List.of(item)).getContent();

        Assertions.assertNotNull(loadedDrone);
        Assertions.assertEquals(loadedDrone.getItems().size(), 1);

        Item itemWithInvalidCode = new Item(
                TestConstants.ITEM_NAME,
                TestConstants.ITEM_CODE_INVALID,
                TestConstants.ITEM_WEIGHT,
                ""
        );
        Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> dispatchController.loadDrone(drone.getId(), List.of(itemWithInvalidCode)));

        Item itemWithInvalidName = new Item(
                TestConstants.ITEM_NAME_INVALID,
                TestConstants.ITEM_CODE,
                TestConstants.ITEM_WEIGHT,
                ""
        );
        Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> dispatchController.loadDrone(drone.getId(), List.of(itemWithInvalidName)));

        Item overweightItem = new Item(
                TestConstants.ITEM_NAME_INVALID,
                TestConstants.ITEM_CODE,
                TestConstants.ITEM_WEIGHT_OVERWEIGHT,
                ""
        );
        Assertions.assertThrows(
                DroneOverweightException.class,
                () -> dispatchController.loadDrone(drone.getId(), List.of(overweightItem)));
    }
}
