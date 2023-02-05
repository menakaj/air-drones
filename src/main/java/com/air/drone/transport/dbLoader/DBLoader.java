package com.air.drone.transport.dbLoader;

import com.air.drone.transport.drone.Drone;
import com.air.drone.transport.drone.DroneModelType;
import com.air.drone.transport.drone.DroneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DBLoader {

    private static final Logger log = LoggerFactory.getLogger(DBLoader.class);

    @Bean
    CommandLineRunner initDatabase(DroneRepository droneRepository) {
        return args -> {
            log.info("Adding drone 1" + droneRepository.save(new Drone(DroneModelType.HEAVY_WEIGHT, new BigDecimal("20.00"), "ZSSZSJFU_DHJ234_1")));
            log.info("Adding drone 2" + droneRepository.save(new Drone(DroneModelType.MIDDLE_WEIGHT, new BigDecimal("80.00"), "ZAAZSJFU_DESF4_2")));
            log.info("Adding drone 3" + droneRepository.save(new Drone(DroneModelType.LIGHT_WEIGHT, new BigDecimal("100.00"), "SSDFFFU_DHJ234_3")));
            log.info("Adding drone 4" + droneRepository.save(new Drone(DroneModelType.CRUISER_WEIGHT, new BigDecimal("50.00"), "ZSSZSJFU_DHJ234_4")));
            log.info("Adding drone 5" + droneRepository.save(new Drone(DroneModelType.MIDDLE_WEIGHT, new BigDecimal("80.00"), "ZAAZSJFU_DESF4_5")));
            log.info("Adding drone 6" + droneRepository.save(new Drone(DroneModelType.LIGHT_WEIGHT, new BigDecimal("100.00"), "SSDFFFU_DHJ234_6")));
            log.info("Adding drone 7" + droneRepository.save(new Drone(DroneModelType.HEAVY_WEIGHT, new BigDecimal("70.00"), "ZSSZSJFU_DHJ234_7")));
            log.info("Adding drone 8" + droneRepository.save(new Drone(DroneModelType.CRUISER_WEIGHT, new BigDecimal("90.00"), "ZAAZSJFU_DESF4_8")));
            log.info("Adding drone 9" + droneRepository.save(new Drone(DroneModelType.LIGHT_WEIGHT, new BigDecimal("100.00"), "SSDFFFU_DHJ234_9")));
            log.info("Adding drone 10" + droneRepository.save(new Drone(DroneModelType.LIGHT_WEIGHT, new BigDecimal("100.00"), "SSDFFFU_DHJ234_10")));
        };
    }
}
