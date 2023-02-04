package com.air.drone.transport.drone;

import com.air.drone.transport.exceptions.DroneNotFoundException;
import com.air.drone.transport.item.Item;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DroneService {

    private final DroneRepository droneRepository;

    public DroneService(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
    }

    public Drone getDroneById(int id) {
        return droneRepository.findById(id).orElseThrow(() -> new DroneNotFoundException(id));
    }

    public List<Drone> getDrones(String availability) {
        return droneRepository.findAll().stream().filter(drone -> {
            if (availability == null) {
                return true;
            }
            return !"true".equalsIgnoreCase(availability) || drone.isAvailable();
        }).collect(Collectors.toList());
    }

    public Drone addDrone(Drone drone) {
        return droneRepository.save(drone);
    }

    public void deleteDrone(int id) {
        Drone drone = this.getDroneById(id);
        droneRepository.deleteById(drone.getId());
    }

    public Drone updateDrone(Drone drone) {
        return droneRepository.saveAndFlush(drone);
    }

    public Set<Item> getItemsOfDrone(int id) {
        Drone drone = this.getDroneById(id);

        return drone.getItems();
    }

}
