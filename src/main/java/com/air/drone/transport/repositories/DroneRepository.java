package com.air.drone.transport.repositories;

import com.air.drone.transport.entities.Drone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DroneRepository extends JpaRepository<Drone, Integer> {

}
