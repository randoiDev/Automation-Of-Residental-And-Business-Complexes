package com.arbc.usermanagement.filters.security;

import com.arbc.usermanagement.filters.security.annotations.Login;
import com.arbc.usermanagement.models.otds.Message;
import com.arbc.usermanagement.utils.JwtUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.lang.reflect.Method;
import java.util.Optional;

import static com.arbc.usermanagement.models.Constants.NOT_AUTHENTICATED_MESSAGE;

@Provider
@Priority(1)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) {

        // Extract annotation from method that is invoked and check first if Login annotation is present
        Method resourceMethod = resourceInfo.getResourceMethod();
        Optional<Login> loginOpt = Optional.ofNullable(resourceMethod.getAnnotation(Login.class));

        if(loginOpt.isPresent())
            return;

        // Proceed with further authentication to see whether JWT is present
        Optional<String> tokenOpt = Optional.ofNullable(requestContext.getHeaderString("Authorization"));
        if (tokenOpt.isEmpty() || !tokenOpt.get().startsWith("Bearer ")) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new Message(String.format(NOT_AUTHENTICATED_MESSAGE,
                            requestContext.getUriInfo().getPath(),
                            requestContext.getMethod())))
                    .build());
            return;
        }

        String token = tokenOpt.get().substring("Bearer".length()).trim();

        try {
            DecodedJWT decodedJWT = JwtUtils.validateToken(token);
            requestContext.setSecurityContext(new UserSecurityContext(decodedJWT));
        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new Message(String.format(NOT_AUTHENTICATED_MESSAGE,
                            requestContext.getUriInfo().getPath(),
                            requestContext.getMethod())))
                    .build());
        }
    }
}

