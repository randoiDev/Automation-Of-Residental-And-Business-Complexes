package com.arbc.residencemanagement.exceptions.mappers;

import com.arbc.residencemanagement.exceptions.ResidenceCRUDException;
import com.arbc.residencemanagement.models.Message;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ResidenceCRUDExceptionMapper implements ExceptionMapper<ResidenceCRUDException> {

    @Override
    public Response toResponse(ResidenceCRUDException exception) {
        return Response.status(exception.getStatusCode())
                .entity(new Message(exception.getMessage()))
                .build();
    }
}
