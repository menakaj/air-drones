package com.air.drone.transport.controllers;

import com.air.drone.transport.assemblers.DroneAssembler;
import com.air.drone.transport.drone.Drone;
import com.air.drone.transport.drone.DroneState;
import com.air.drone.transport.exceptions.*;
import com.air.drone.transport.item.Item;
import com.air.drone.transport.drone.DroneRepository;
import com.air.drone.transport.item.ItemRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/dispatcher/v1")
public class DispatchController {

    private final DroneRepository droneRepository;
    private final ItemRepository itemRepository;

    private final DroneAssembler droneAssembler;

    public DispatchController(DroneRepository droneRepository, ItemRepository itemRepository,
                              DroneAssembler droneAssembler) {
        this.droneRepository = droneRepository;
        this.itemRepository = itemRepository;
        this.droneAssembler = droneAssembler;
    }

    @PostMapping("/drones/register")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Drone> registerDrone(@RequestBody Drone drone) {
        return droneAssembler.toModel(droneRepository.saveAndFlush(drone));
    }

    @GetMapping("/drones")
    public CollectionModel<EntityModel<Drone>> getDrones(
            @RequestParam(required = false, name = "available") String available) {
        List<EntityModel<Drone>> drones = droneRepository.findAll().stream().filter(drone -> {
            if (available == null) {
                return true;
            }
            return !"true".equalsIgnoreCase(available) || drone.isAvailable();
        }).map(droneAssembler::toModel).collect(Collectors.toList());
        drones.forEach(System.out::println);
        return CollectionModel.of(
                drones,
                linkTo(methodOn(DispatchController.class).getDrones("true")).withSelfRel()
        );
    }

    @GetMapping("/drones/{id}/battery")
    public String getDroneBatteryLevel(@PathVariable int id) {
        Drone drone = droneRepository.findById(id).orElseThrow(() -> new DroneNotFoundException(id));

        return "{ \"battery\": " + drone.getBatteryCapacity() + "%}" ;

    }

    @GetMapping("/drones/{id}")
    public EntityModel<Drone> getDrone(@PathVariable int id) {
        return droneAssembler.toModel(
                droneRepository
                        .findById(id)
                        .orElseThrow(() -> new DroneNotFoundException(id)));
    }

    @PostMapping("/drones/{id}/items")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Set<Item> loadDrone(@PathVariable int id, @RequestBody List<Item> items) {

        Drone drone = droneRepository.findById(id).orElseThrow(() -> new DroneNotFoundException(id));

        if (!drone.isAvailable()) {
            throw new DroneNotAvailableException(id);
        }

        drone.setId(drone.getId());
        float allowedMaxWeight = drone.getMaxWeight();

        float currentWeight = drone.getItems().stream().map(Item::getWeight).reduce(0f, Float::sum);
        for (Item item : items) {
            currentWeight += item.getWeight();
        }
        if (currentWeight > allowedMaxWeight) {
            throw new DroneOverweightException(id);
        }
        drone.setState(DroneState.LOADING);
        droneRepository.saveAndFlush(drone);
        for (Item i : items) {
            drone.addItem(i);
        }

        itemRepository.saveAllAndFlush(items);
        drone.setState(DroneState.LOADED);
        Drone droneWithItems =  droneRepository.saveAndFlush(drone);
        return droneWithItems.getItems();
    }

    @DeleteMapping("/drones/{id}/items/{itemId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Set<Item> removeItemFromDrone(@PathVariable int id, @PathVariable Long itemId) {

        Drone drone = droneRepository.findById(id).orElseThrow(() -> new DroneNotFoundException(id));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NoItemFoundException(itemId));;
        drone.deleteItem(item);
        Drone updated = droneRepository.save(drone);

        return updated.getItems();
    }

    @PostMapping("/drones/{droneId}/dispatch")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Drone dispatchDrone(@PathVariable int droneId) {
        Drone drone = droneRepository.findById(droneId).orElseThrow(() -> new DroneNotFoundException(droneId));

        switch (drone.getState()) {
            case DELIVERED:
            case DELIVERING:
            case RETURNING:
                throw new DroneAlreadyDispatchedException(droneId);
            case IDLE:
                throw new DroneIsNotLoadedException(droneId);
            default:
                break;
        }
        if (drone.getBatteryCapacity() < 25) {
            throw new LowBatteryException(droneId);
        }
        drone.setState(DroneState.DELIVERING);
        return droneRepository.save(drone);
    }

    @GetMapping("/drones/{id}/items")
    public Set<Item> getLoadedItems(@PathVariable int id) {
        Drone drone = droneRepository.findById(id).orElseThrow(() -> new DroneNotFoundException(id));
        return drone.getItems();
    }

}
