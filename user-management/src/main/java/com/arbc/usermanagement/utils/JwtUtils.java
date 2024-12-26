package com.arbc.usermanagement.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Date;

@ApplicationScoped
public class JwtUtils {

    private static final String SECRET_KEY = "my-most-secret-key";
    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

    public static String generateWorkerToken(String email, String role, String name) {
        return JWT.create()
                .withSubject(email)
                .withClaim("role", role)
                .withClaim("name", name)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 7200000)) // 2 hours expiration
                .sign(algorithm);
    }

    public static String generateResidentToken(String email, String role, String name, String bannedForReservations) {
        return JWT.create()
                .withSubject(email)
                .withClaim("role", role)
                .withClaim("name", name)
                .withClaim("bannedForReservations", bannedForReservations)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 7200000)) // 2 hours expiration
                .sign(algorithm);
    }

    public static DecodedJWT validateToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm)
                .build();
        return verifier.verify(token);
    }
}


