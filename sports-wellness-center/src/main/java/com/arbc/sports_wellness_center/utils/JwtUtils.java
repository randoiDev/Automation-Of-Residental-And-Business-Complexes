package com.arbc.sports_wellness_center.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

public class JwtUtils {

    private static final String SECRET_KEY = "my-most-secret-key";
    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

    // Validate JWT Token
    public static DecodedJWT validateToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm)
                .build();
        return verifier.verify(token); // Throws an exception if the token is invalid
    }

    public static String getClaim(String token, String claimKey) {
        // Validate the token first
        DecodedJWT decodedJWT = validateToken(token);

        // Retrieve the claim value by key
        return decodedJWT.getClaim(claimKey).asString();
    }
}


