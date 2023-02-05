package com.air.drone.transport.controllers;

import com.air.drone.transport.assemblers.DroneAssembler;
import com.air.drone.transport.drone.Drone;
import com.air.drone.transport.drone.DroneService;
import com.air.drone.transport.drone.DroneState;
import com.air.drone.transport.exceptions.*;
import com.air.drone.transport.item.Item;
import com.air.drone.transport.item.ItemService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/dispatcher/v1")
public class DispatchController {

    private final DroneService droneService;
    private final ItemService itemService;

    private final DroneAssembler droneAssembler;

    public DispatchController(DroneService droneService, ItemService itemService,
                              DroneAssembler droneAssembler) {
        this.droneService = droneService;
        this.itemService = itemService;
        this.droneAssembler = droneAssembler;
    }

    @PostMapping("/drones/register")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Drone> registerDrone(@RequestBody Drone drone) {
        return droneAssembler.toModel(droneService.addDrone(drone));
    }

    @GetMapping("/drones")
    public CollectionModel<EntityModel<Drone>> getDrones(
            @RequestParam(required = false, name = "status") String status) {
        List<EntityModel<Drone>> drones = droneService.getDrones(status)
                .stream()
                .map(droneAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(
                drones,
                linkTo(methodOn(DispatchController.class).getDrones("true")).withSelfRel()
        );
    }

    @GetMapping("/drones/{id}/battery")
    public String getDroneBatteryLevel(@PathVariable int id) {
        Drone drone = droneService.getDroneById(id);

        return "{\"battery\": \"" + drone.getBatteryCapacity() + "%\"}" ;

    }

    @GetMapping("/drones/{id}")
    public EntityModel<Drone> getDrone(@PathVariable int id) {
        return droneAssembler.toModel(droneService.getDroneById(id));
    }

    @PostMapping("/drones/{id}/items")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Transactional
    public EntityModel<Drone> loadDrone(@PathVariable int id, @RequestBody List<Item> items) {

        Drone drone = droneService.getDroneById(id);

        if (!drone.isAvailable()) {
            throw new DroneNotAvailableException(id);
        }
        float allowedMaxWeight = drone.getMaxWeight();

        float currentWeight = drone.getItems().stream().map(Item::getWeight).reduce(0f, Float::sum);
        for (Item item : items) {
            currentWeight += item.getWeight();
        }
        if (currentWeight > allowedMaxWeight) {
            throw new DroneOverweightException(id);
        }
        drone.setState(DroneState.LOADING);
        droneService.updateDrone(drone);
        for (Item i : items) {
            drone.addItem(i);
        }

        itemService.addItems(items);
        drone.setState(DroneState.LOADED);
        Drone droneWithItems =  droneService.updateDrone(drone);
        return droneAssembler.toModel(droneWithItems);
    }

    @DeleteMapping("/drones/{id}/items/{itemId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Set<Item> removeItemFromDrone(@PathVariable int id, @PathVariable Long itemId) {

        Drone drone = droneService.getDroneById(id);
        Item item = itemService.getItem(itemId);
        drone.deleteItem(item);
        Drone updated = droneService.updateDrone(drone);

        return updated.getItems();
    }

    @PostMapping("/drones/{droneId}/dispatch")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Drone dispatchDrone(@PathVariable int droneId) {
        Drone drone = droneService.getDroneById(droneId);

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
        if (drone.getBatteryCapacity().floatValue() < 25) {
            throw new LowBatteryException(droneId);
        }
        drone.setState(DroneState.DELIVERING);
        return droneService.updateDrone(drone);
    }

    @GetMapping("/drones/{id}/items")
    public Set<Item> getLoadedItems(@PathVariable int id) {
        return droneService.getItemsOfDrone(id);
    }

}
