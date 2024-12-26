package com.arbc.sports_wellness_center.exceptions;

import lombok.Getter;

@Getter
public class SWCCRUDException extends RuntimeException {

    private final int statusCode;

    public SWCCRUDException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
