package com.air.drone.transport.repositories;

import com.air.drone.transport.drone.Drone;
import com.air.drone.transport.drone.DroneModelType;
import com.air.drone.transport.drone.DroneRepository;
import com.air.drone.transport.item.Item;
import com.air.drone.transport.item.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

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
            log.info("Adding drone " + droneRepository.save(new Drone(DroneModelType.HEAVY_WEIGHT, new BigDecimal("20.00"), "ZSSZSJFU_DHJ234")));
            log.info("Adding drone " + droneRepository.save(new Drone(DroneModelType.MIDDLE_WEIGHT, new BigDecimal("80.00"), "ZAAZSJFU_DESF4")));
            log.info("Adding drone " + droneRepository.save(new Drone(DroneModelType.LIGHT_WEIGHT, new BigDecimal("100.00"), "SSDFFFU_DHJ234")));
            log.info("Adding item " + itemRepository.save(new Item("Sugar", "S123", 1500, "")));
            log.info("Adding item " + itemRepository.save(new Item("Vaccine", "V12_EDDE", 11, "")));
        };
    }
}
