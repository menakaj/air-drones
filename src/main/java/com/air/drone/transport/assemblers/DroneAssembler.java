package com.air.drone.transport.assemblers;

import com.air.drone.transport.controllers.DispatchController;
import com.air.drone.transport.entities.Drone;
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
                linkTo(methodOn(DispatchController.class).getDrones("available")).withRel("all"),
                linkTo(methodOn(DispatchController.class).registerDrone(drone)).withRel("update")
                );
    }
}
