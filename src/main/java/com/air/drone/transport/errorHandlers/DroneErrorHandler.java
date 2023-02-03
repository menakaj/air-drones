package com.air.drone.transport.errorHandlers;

import com.air.drone.transport.exceptions.DroneNotAvailableException;
import com.air.drone.transport.exceptions.DroneNotFoundException;
import com.air.drone.transport.exceptions.DroneOverweightException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@ControllerAdvice
public class DroneErrorHandler {

    Logger log = LoggerFactory.getLogger(DroneErrorHandler.class);

    @ResponseBody
    @ExceptionHandler(DroneNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String droneNotFoundHandler(DroneNotFoundException ex) {
        log.error("Drone not found", ex);
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(DroneNotAvailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String droneNotAvailableHandler(DroneNotAvailableException ex) {
        log.error("Drone not available", ex);
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(DroneOverweightException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String droneOverweightHandler(DroneOverweightException ex) {
        log.error("Bad request", ex);
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    String constraintViolationExceptionHandler(ConstraintViolationException ex) {
        log.error("Bad request", ex);
        return "{\"error\": true, \"message\": \" "+ ex.getConstraintViolations().stream().map(ConstraintViolation::getMessageTemplate).collect(Collectors.joining()) + "\" }";
    }
}
