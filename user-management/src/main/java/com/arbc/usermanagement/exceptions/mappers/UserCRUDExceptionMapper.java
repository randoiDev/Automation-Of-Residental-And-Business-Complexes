package com.arbc.usermanagement.exceptions.mappers;

import com.arbc.usermanagement.exceptions.UserCRUDException;
import com.arbc.usermanagement.models.otds.Message;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UserCRUDExceptionMapper implements ExceptionMapper<UserCRUDException> {

    @Override
    public Response toResponse(UserCRUDException exception) {
        return Response.status(exception.getStatusCode())
                .entity(new Message(exception.getMessage()))
                .build();
    }
}
