package com.arbc.residencemanagement.exceptions.mappers;

import com.arbc.residencemanagement.models.Message;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InternalServerErrorExceptionMapper implements ExceptionMapper<InternalServerErrorException> {

    @Override
    public Response toResponse(InternalServerErrorException e) {

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new Message(e.getMessage()))
                .build();
    }
}