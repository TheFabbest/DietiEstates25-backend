package com.dieti.dietiestatesbackend.exception;

/**
 * Eccezione dedicata per errori durante le operazioni di hashing.
 */
public class HashingException extends RuntimeException {
    public HashingException(String message) {
        super(message);
    }

    public HashingException(String message, Throwable cause) {
        super(message, cause);
    }
}