package com.userservice.exception;

public class UserIsNotVerifiedException extends RuntimeException {

    public UserIsNotVerifiedException(String message) {
        super(message);
    }
}
