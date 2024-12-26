package com.arbc.usermanagement.exceptions.mappers;

import com.arbc.usermanagement.models.otds.Message;
import com.arbc.usermanagement.utils.Utils;
import com.mongodb.MongoWriteException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import static com.arbc.usermanagement.models.Constants.FIELD_VALUE_ALREADY_IN_USER_VIOLATION_MESSAGE;

@Provider
public class MongoWriteExceptionMapper implements ExceptionMapper<MongoWriteException> {

    @Override
    public Response toResponse(MongoWriteException e) {
        System.out.println(e.getMessage());
        return Response.status(Response.Status.CONFLICT)
                .entity(new Message(String.format(FIELD_VALUE_ALREADY_IN_USER_VIOLATION_MESSAGE,
                        Utils.extractDuplicateValueFromMongoException(e.getMessage()))))
                .build();
    }
}
