package com.fhcs.clothing_store.infrastructure.in.rest.handler;

public class AddressValidationException extends RuntimeException {
    public AddressValidationException(String message) {
        super(message);
    }
}
