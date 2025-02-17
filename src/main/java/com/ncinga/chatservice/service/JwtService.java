package com.ncinga.chatservice.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public interface JwtService {

    String generateToken(Authentication authentication, Map<String, Object> extraClaims);

    String generateRefreshToken(Authentication authentication);

    String extractUserName(String token);

    <T> T extractClaim(String token, Function<io.jsonwebtoken.Claims, T> claimResolver);

    boolean validateToken(String token, UserDetails userDetails);

    Date extractExpiration(String token);

    boolean isTokenExpired(String token);

    Key getKey();

    String generateAzureADToken();

}
