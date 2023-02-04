package com.air.drone.transport.drone;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class DroneModelSubsetValidator implements ConstraintValidator<DroneModelSubset, DroneModelType> {

    private DroneModelType[] models;

    @Override
    public void initialize(DroneModelSubset constraint) {
        this.models = constraint.anyOf();
    }

    @Override
    public boolean isValid(DroneModelType model, ConstraintValidatorContext context) {
        return model == null || Arrays.asList(models).contains(model);
    }
}
