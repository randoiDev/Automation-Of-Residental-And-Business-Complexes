package com.arbc.usermanagement.exceptions;

import lombok.Getter;

@Getter
public class UserCRUDException extends RuntimeException {

    private final int statusCode;

    public UserCRUDException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
