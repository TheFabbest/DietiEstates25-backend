package com.dieti.dietiestatesbackend.exception;

public class UnauthorizedOperationException extends RuntimeException {

    public UnauthorizedOperationException() {
        super("Unauthorized operation");
    }

    public UnauthorizedOperationException(String message) {
        super(message);
    }
}