package com.air.drone.transport.controllers;

import com.air.drone.transport.assemblers.DroneAssembler;
import com.air.drone.transport.exceptions.DroneNotAvailableException;
import com.air.drone.transport.exceptions.DroneNotFoundException;
import com.air.drone.transport.exceptions.DroneOverweightException;
import com.air.drone.transport.entities.Drone;
import com.air.drone.transport.entities.DroneState;
import com.air.drone.transport.entities.Item;
import com.air.drone.transport.repositories.DroneRepository;
import com.air.drone.transport.repositories.ItemRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/dispatcher/v1")
public class DispatchController {

    private final DroneRepository droneRepository;
    private final ItemRepository itemRepository;

    private final DroneAssembler droneAssembler;

    public DispatchController(DroneRepository droneRepository, ItemRepository itemRepository, DroneAssembler droneAssembler) {
        this.droneRepository = droneRepository;
        this.itemRepository = itemRepository;
        this.droneAssembler = droneAssembler;
    }

    @PostMapping("/drones/register")
    public EntityModel<Drone> registerDrone(@RequestBody Drone drone) {
        return droneAssembler.toModel(droneRepository.saveAndFlush(drone));
    }

    @GetMapping("/drones")
    public CollectionModel<EntityModel<Drone>> getDrones(@RequestParam(required = false, name = "available") String available) {
        List<EntityModel<Drone>> drones = droneRepository.findAll().stream().filter(drone -> {
            if (available == null) {
                return true;
            }
            return !"true".equalsIgnoreCase(available) || drone.isAvailable();
        }).map(droneAssembler::toModel).collect(Collectors.toList());
        return CollectionModel.of(drones, linkTo(methodOn(DispatchController.class).getDrones("")).withSelfRel());
    }

    @GetMapping("/drones/{id}/battery")
    public String getDroneBatteryLevel(@PathVariable int id) {
        Drone drone = droneRepository.findById(id).orElseThrow(() -> new DroneNotFoundException(id));
        return drone.getBatteryCapacity() + "%";

    }


    @PostMapping("/drones/{id}/items")
    public List<Item> loadDrone(@PathVariable int id, @RequestBody List<Item> items) {

        Drone drone = droneRepository.findById(id).orElseThrow(() -> new DroneNotFoundException(id));

        if (!drone.isAvailable()) {
            throw new DroneNotAvailableException(id);
        }

        Drone updatedDrone = new Drone(drone.getModel(), drone.getBatteryCapacity(), drone.getSerialNumber());
        updatedDrone.setId(drone.getId());
        float allowedMaxWeight = drone.getModel().maxWeight;

        float currentWeight = drone.getItems().stream().map(Item::getWeight).reduce(0f, Float::sum);
        for (Item item : items) {
            currentWeight += item.getWeight();
        }
        if (currentWeight > allowedMaxWeight) {
            throw new DroneOverweightException(id);
        }
        updatedDrone.setState(DroneState.LOADING);
        droneRepository.saveAndFlush(updatedDrone);
        List<Item> newItems = items.stream().map(item -> new Item(item.getName(), item.getCode(), item.getWeight(), item.getImage(), id)).collect(Collectors.toList());
        itemRepository.saveAllAndFlush(newItems);
        updatedDrone.setState(DroneState.LOADED);
        Drone droneWithItems =  droneRepository.saveAndFlush(updatedDrone);
        return droneWithItems.getItems();
    }

    @GetMapping("/drones/{id}/items")
    public List<Item> getLoadedItems(@PathVariable int id) {
        Drone drone = droneRepository.findById(id).orElseThrow(() -> new DroneNotFoundException(id));
        return drone.getItems();
    }

}
