package com.arbc.usermanagement.exceptions.mappers;

import com.arbc.usermanagement.models.otds.Message;
import com.mongodb.MongoException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import static com.arbc.usermanagement.models.Constants.SERVER_ERROR_EXCEPTION_MESSAGE;

@Provider
public class MongoGeneralExceptionMapper implements ExceptionMapper<MongoException> {
    @Override
    public Response toResponse(MongoException exception) {
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new Message(SERVER_ERROR_EXCEPTION_MESSAGE))
                .build();
    }
}
