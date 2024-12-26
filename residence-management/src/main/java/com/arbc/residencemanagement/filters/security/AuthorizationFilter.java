package com.arbc.residencemanagement.filters.security;

import com.arbc.residencemanagement.filters.security.annotations.RequiresRole;
import com.arbc.residencemanagement.models.Message;
import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

import java.lang.reflect.Method;
import java.util.Optional;

import static com.arbc.residencemanagement.models.Constants.NOT_AUTHORIZED_MESSAGE;

@Provider
@Priority(2)
public class AuthorizationFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) {

        // Get the method being called and first check if it contains Login annotation
        Method resourceMethod = resourceInfo.getResourceMethod();

        // Check for the RequiresRole annotation on the method
        Optional<RequiresRole> annotationOpt = Optional.ofNullable(resourceMethod.getAnnotation(RequiresRole.class));

        if (annotationOpt.isPresent()) {
            String[] requiredRole = annotationOpt.get().value();
            SecurityContext securityContext = requestContext.getSecurityContext();
            boolean isAuthorized = false;

            for (String role: requiredRole) {
                if(securityContext.isUserInRole(role))
                    isAuthorized = true;
            }

            if (!isAuthorized) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                        .entity(new Message(String.format(NOT_AUTHORIZED_MESSAGE,
                                requestContext.getUriInfo().getPath(),
                                requestContext.getMethod())))
                        .build());
            }
        }
    }
}
