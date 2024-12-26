package com.arbc.sports_wellness_center.exceptions.mappers;

import com.arbc.sports_wellness_center.exceptions.SWCCRUDException;
import com.arbc.sports_wellness_center.models.messages.Message;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class SWCCRUDExceptionMapper implements ExceptionMapper<SWCCRUDException> {

    @Override
    public Response toResponse(SWCCRUDException exception) {
        return Response.status(exception.getStatusCode())
                .entity(new Message(exception.getMessage()))
                .build();
    }
}
