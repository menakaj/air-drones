package com.air.drone.transport.exceptions;

public class NoItemFoundException extends RuntimeException {
    public NoItemFoundException(Long itemId) {
        super(String.format("Item with %d not found", itemId));
    }
}
