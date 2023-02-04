package com.air.drone.transport.errorHandlers;

import com.air.drone.transport.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@ControllerAdvice
public class DroneErrorHandler {

    static class ErrorMessage {
        boolean error;
        String message;

        public ErrorMessage(){}

        public boolean isError() {
            return error;
        }

        public void setError(boolean error) {
            this.error = error;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    Logger log = LoggerFactory.getLogger(DroneErrorHandler.class);

    @ResponseBody
    @ExceptionHandler(DroneNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorMessage droneNotFoundHandler(DroneNotFoundException ex) {
        log.error("Drone not found", ex);
        return buildError(ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(DroneNotAvailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorMessage droneNotAvailableHandler(DroneNotAvailableException ex) {
        log.error("Drone not available", ex);
        return buildError(ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(DroneOverweightException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorMessage droneOverweightHandler(DroneOverweightException ex) {
        log.error("Bad request", ex);
        return buildError(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    ErrorMessage constraintViolationExceptionHandler(ConstraintViolationException ex) {
        log.error("Bad request", ex);
        return buildError(ex.getConstraintViolations()
                .stream()
                .map(constraintViolation -> {
                    final String s = constraintViolation.getPropertyPath().toString() + " "
                            + constraintViolation.getMessage();
                    return s;
                })
                .collect(Collectors.joining()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(LowBatteryException.class)
    ErrorMessage lowBatteryExceptionHandler(LowBatteryException ex) {
        log.error("Bad request", ex);
        return buildError(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(DroneIsNotLoadedException.class)
    ErrorMessage droneNotLoadedExceptionHandler(DroneIsNotLoadedException ex) {
        log.error("Bad request", ex);
        return buildError(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(DroneAlreadyDispatchedException.class)
    ErrorMessage droneAlreadyDispatchedExceptionHandler(DroneAlreadyDispatchedException ex) {
        log.error("Bad request", ex);
        return buildError(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    @ExceptionHandler(org.hibernate.exception.ConstraintViolationException.class)
    ErrorMessage droneAlreadyDispatchedExceptionHandler(org.hibernate.exception.ConstraintViolationException ex) {
        log.error("Bad request", ex);
        return buildError("Conflict");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ExceptionHandler(NoItemFoundException.class)
    ErrorMessage itemNotFound(NoItemFoundException ex) {
        log.error("Bad request", ex);
        return buildError(ex.getMessage());
    }

    private ErrorMessage buildError(String message) {
        ErrorMessage error = new ErrorMessage();
        error.setError(true);
        error.setMessage(message);
        return error;
    }
}
