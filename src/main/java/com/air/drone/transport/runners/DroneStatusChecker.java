package com.air.drone.transport.runners;

import com.air.drone.transport.drone.Drone;
import com.air.drone.transport.drone.DroneRepository;
import com.air.drone.transport.drone.DroneState;
import com.air.drone.transport.item.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DroneStatusChecker {
    private static final Logger log = LoggerFactory.getLogger(DroneStatusChecker.class);
    private final DroneRepository droneRepository;

    private final ItemRepository itemRepository;

    public DroneStatusChecker(DroneRepository droneRepository, ItemRepository itemRepository) {
        this.droneRepository = droneRepository;
        this.itemRepository = itemRepository;
    }

    @Scheduled(fixedRate = 10000)
    public void updateBatteryCapasity() {
        List<Drone> drones = droneRepository.findAll();

        for(Drone d : drones) {
            if (d.getState() != DroneState.IDLE) {
                d.setBatteryCapacity(d.getBatteryCapacity() - 1.0);
                droneRepository.save(d);
            }
        }
    }

    @Scheduled(fixedRate = 5000)
    public void reportBatteryCapacity() {
        List<Drone> drones = droneRepository.findAll();

        for(Drone d : drones) {
            log.info(String.format("Status of drone: %s id: %d state: %s batery : %.2f", d.getSerialNumber(), d.getId(), d.getState().name(), d.getBatteryCapacity() ));
        }
    }

    @Scheduled(fixedRate = 5000)
    public void runDelivery() {
        List<Drone> drones = droneRepository.findAll();
        List<Drone> updatedDrones = new ArrayList<>();
        for(Drone d : drones) {
            Drone drone = new Drone(d.getModel(), d.getBatteryCapacity(), d.getSerialNumber());
            drone.setId(d.getId());
            switch (d.getState()) {
                case DELIVERED:
                    d.setState(DroneState.RETURNING);
                    d.deleteAllItems();
                    System.out.println(d);
                    updatedDrones.add(d);
                    break;
                case DELIVERING:
                    drone.setState(DroneState.DELIVERED);
                    updatedDrones.add(drone);
                    break;
                case RETURNING:
                    drone.setState(DroneState.IDLE);
                    droneRepository.save(drone);
                    break;
                default:
                    break;
            }
        }
        droneRepository.saveAllAndFlush(updatedDrones);
    }

}
