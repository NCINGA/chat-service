package com.ncinga.chatservice.service.impl;

import com.ncinga.chatservice.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

@Service
public class JwtServiceImpl implements JwtService {

    private String secretKey = "9c208648d79a3ab82a5faa85b2d725dc3f5c7a9af4bbf506a5dfcef95b45961e0ee81fd2c2c23227014c8142b7943b08f61acbdd1b3dc9b721ab9fc2b7ef3d9ed6f90a64acac28604eb57c8ebe0306cb0f3d3d3199be930396c4060d3f118c77af7c3b0d44f651447180411fcd329bd91b038c5c110e832794f5e7c895811118e8a9371b81163253d45d169fb57dfb9a629559eee05419e49c77bff29aa57b232c05ccd6a9d1631418a5fcadd471fe026b3e9f38a3077ec143a5015b217ea28b1bf31a30477cd94fbcaa91e9084528ac47f6183ad132c35dea56e6d54630781ddf3ede6a746ce35bf87f5bb72f6cba6d7153207086b377aa5a37278a365214d4";

    public JwtServiceImpl() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGen.generateKey();
            secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String generateToken(Authentication authentication, Map<String, Object> extraClaims) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(authentication.getName())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 10)) // @TODO add time
                .signWith(getKey())
                .compact();
    }

    @Override
    public String generateRefreshToken(Authentication authentication) {
        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 10)) // @TODO add time
                .signWith(getKey())
                .compact();
    }

    @Override
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public <T> T extractClaim(String token, Function<io.jsonwebtoken.Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    @Override
    public Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
