package com.air.drone.transport.repositories;

import com.air.drone.transport.drone.Drone;
import com.air.drone.transport.drone.DroneModel;
import com.air.drone.transport.drone.DroneRepository;
import com.air.drone.transport.item.Item;
import com.air.drone.transport.item.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DBLoader {
    private static final Logger log = LoggerFactory.getLogger(DBLoader.class);

    private final DroneRepository droneRepository;
    private final ItemRepository itemRepository;

    public DBLoader(DroneRepository droneRepository, ItemRepository itemRepository) {
        this.droneRepository = droneRepository;
        this.itemRepository = itemRepository;
    }

    @Bean
    CommandLineRunner initDatabase(DroneRepository droneRepository, ItemRepository itemRepository) {
        return args -> {
            log.info("Adding drone " + droneRepository.save(new Drone(DroneModel.HEAVY_WEIGHT, 20.3, "ZSSZSJFU_DHJ234")));
            log.info("Adding drone " + droneRepository.save(new Drone(DroneModel.MIDDLE_WEIGHT, 100.0, "ZAAZSJFU_DESF4")));
            log.info("Adding drone " + droneRepository.save(new Drone(DroneModel.LIGHT_WEIGHT, 80.3, "SSDFFFU_DHJ234")));
            log.info("Adding item " + itemRepository.save(new Item("Sugar", "S123", 1500, "")));
            log.info("Adding item " + itemRepository.save(new Item("Vaccine", "V12_EDDE", 11, "")));
        };
    }
}
