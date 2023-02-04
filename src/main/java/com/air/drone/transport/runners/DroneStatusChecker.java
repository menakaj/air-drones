package com.air.drone.transport.runners;

import com.air.drone.transport.drone.Drone;
import com.air.drone.transport.drone.DroneService;
import com.air.drone.transport.drone.DroneState;
import com.air.drone.transport.item.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DroneStatusChecker {
    private static final Logger log = LoggerFactory.getLogger(DroneStatusChecker.class);
    private final DroneService droneService;

    private final ItemService itemService;

    public DroneStatusChecker(DroneService droneService, ItemService itemService) {
        this.droneService = droneService;
        this.itemService = itemService;
    }

    @Scheduled(fixedRate = 10000)
    public void updateBatteryCapasity() {
        List<Drone> drones = droneService.getDrones(null);

        for(Drone d : drones) {
            if (d.getState() != DroneState.IDLE) {
                d.setBatteryCapacity(d.getBatteryCapacity() - 1.0);
                droneService.updateDrone(d);
            }
        }
    }

    @Scheduled(fixedRate = 5000)
    public void reportBatteryCapacity() {
        List<Drone> drones = droneService.getDrones(null);

        for(Drone d : drones) {
            log.info(String.format("Status of drone: %s id: %d state: %s batery : %.2f", d.getSerialNumber(), d.getId(), d.getState().name(), d.getBatteryCapacity() ));
        }
    }

    @Scheduled(fixedRate = 5000)
    public void runDelivery() {
        List<Drone> drones = droneService.getDrones(null);
        List<Drone> updatedDrones = new ArrayList<>();
        for(Drone d : drones) {
//            Drone drone = new Drone(d.getModel(), d.getBatteryCapacity(), d.getSerialNumber());
//            drone.setId(d.getId());
            switch (d.getState()) {
                case DELIVERED:
                    d.setState(DroneState.RETURNING);
                    d.deleteAllItems();
                    System.out.println(d);
                    droneService.updateDrone(d);
                    break;
                case DELIVERING:
                    d.setState(DroneState.DELIVERED);
                    droneService.updateDrone(d);
                    break;
                case RETURNING:
                    d.setState(DroneState.IDLE);
                    droneService.updateDrone(d);
                    break;
                default:
                    break;
            }
        }
    }

}
