package com.arbc.residencemanagement.exceptions.mappers;

import com.arbc.residencemanagement.models.Message;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Set;
import java.util.stream.Collectors;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();

        // Collect all error messages
        String errorMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("\n"));

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new Message(errorMessages))
                .build();
    }
}
