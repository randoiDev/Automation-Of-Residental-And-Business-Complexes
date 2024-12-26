package com.arbc.sports_wellness_center.exceptions.mappers;

import com.arbc.sports_wellness_center.models.messages.Message;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.format.DateTimeParseException;

import static com.arbc.sports_wellness_center.models.Constants.DATE_FORMAT_CONSTRAINT_VIOLATION_EXCEPTION_MESSAGE;

@Provider
public class DateTimeParseExceptionMapper implements ExceptionMapper<DateTimeParseException> {

    @Override
    public Response toResponse(DateTimeParseException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new Message(DATE_FORMAT_CONSTRAINT_VIOLATION_EXCEPTION_MESSAGE))
                .build();
    }
}
