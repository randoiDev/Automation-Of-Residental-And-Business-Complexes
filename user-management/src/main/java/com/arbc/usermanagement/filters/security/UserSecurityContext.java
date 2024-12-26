package com.arbc.usermanagement.filters.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;


public class UserSecurityContext implements SecurityContext {

    private final DecodedJWT jwt;

    public UserSecurityContext(DecodedJWT jwt) {
        this.jwt = jwt;
    }

    @Override
    public Principal getUserPrincipal() {
        return jwt::getSubject;
    }

    @Override
    public boolean isUserInRole(String role) {
        return jwt.getClaim("role").asString().equals(role);
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public String getAuthenticationScheme() {
        return "BEARER";
    }
}
