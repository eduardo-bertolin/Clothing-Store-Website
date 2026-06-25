package com.fhcs.clothing_store.core.domain.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException() {}
    public InvalidTokenException(String message) { super(message); }
    public InvalidTokenException(String message, Throwable cause) { super(message, cause); }
}
