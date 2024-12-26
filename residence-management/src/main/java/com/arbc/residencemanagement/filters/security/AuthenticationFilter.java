package com.arbc.residencemanagement.filters.security;

import com.arbc.residencemanagement.models.Message;
import com.arbc.residencemanagement.utils.JwtUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.util.Optional;

import static com.arbc.residencemanagement.models.Constants.NOT_AUTHENTICATED_MESSAGE;

@Provider
@Priority(1)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {

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
            requestContext.setSecurityContext(new ResidenceSecurityContext(decodedJWT));
        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new Message(String.format(NOT_AUTHENTICATED_MESSAGE,
                            requestContext.getUriInfo().getPath(),
                            requestContext.getMethod())))
                    .build());
        }
    }
}

