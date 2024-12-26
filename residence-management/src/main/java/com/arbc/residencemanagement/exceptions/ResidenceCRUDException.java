package com.arbc.residencemanagement.exceptions;

import lombok.Getter;

@Getter
public class ResidenceCRUDException extends RuntimeException{

    private final int statusCode;

    public ResidenceCRUDException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

}
