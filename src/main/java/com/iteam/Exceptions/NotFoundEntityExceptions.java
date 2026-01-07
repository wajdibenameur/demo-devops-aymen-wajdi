package com.iteam.Exceptions;

public class NotFoundEntityExceptions extends RuntimeException {

    private String message;

    public NotFoundEntityExceptions() {

    }

    public NotFoundEntityExceptions(String message) {
        super(message);
        this.message = message;
    }



}
