package com.air.drone.transport.assemblers;

import com.air.drone.transport.controllers.DispatchController;
import com.air.drone.transport.drone.Drone;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DroneAssembler implements RepresentationModelAssembler<Drone, EntityModel<Drone>> {
    @Override
    public EntityModel<Drone> toModel(Drone drone) {
        return EntityModel.of(
                drone,
                linkTo(methodOn(DispatchController.class).getDrone(drone.getId())).withRel("self"),
                linkTo(methodOn(DispatchController.class).getLoadedItems(drone.getId())).withRel("items"),
                linkTo(methodOn(DispatchController.class).dispatchDrone(drone.getId())).withRel("dispatch")
                );
    }
}
