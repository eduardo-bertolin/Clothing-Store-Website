package com.fhcs.clothing_store.infrastructure.in.rest.handler;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(String message) {
        super(message);
    }
}
