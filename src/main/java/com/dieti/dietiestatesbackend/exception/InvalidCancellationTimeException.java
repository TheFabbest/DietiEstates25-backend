package com.dieti.dietiestatesbackend.exception;

public class InvalidCancellationTimeException extends RuntimeException {
    public InvalidCancellationTimeException() {
        super("Invalid cancellation time");
    }

    public InvalidCancellationTimeException(String message) {
        super(message);
    }
}