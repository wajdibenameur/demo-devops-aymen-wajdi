package com.iteam.Exceptions;

public class UserAlreadyExistsExceptions extends RuntimeException {
    private String message;

    public UserAlreadyExistsExceptions() {

    }

    public UserAlreadyExistsExceptions(String message) {
        super(message);
        this.message = message;
    }
}
