package com.air.drone.transport.runners;

import com.air.drone.transport.entities.Drone;
import com.air.drone.transport.entities.DroneState;
import com.air.drone.transport.repositories.DroneRepository;
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

    public DroneStatusChecker(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
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
                case LOADED:
                    drone.setState(DroneState.DELIVERING);
                    updatedDrones.add(drone);
                    break;
                case DELIVERED:
                    drone.setState(DroneState.RETURNING);
                    updatedDrones.add(drone);
                    break;
                case DELIVERING:
                    drone.setState(DroneState.DELIVERED);
                    updatedDrones.add(drone);
                    break;
                case RETURNING:
                    drone.setState(DroneState.IDLE);
                    updatedDrones.add(drone);
                    break;
                default:
                    break;
            }
        }
        droneRepository.saveAllAndFlush(updatedDrones);
    }

}
