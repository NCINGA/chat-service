package com.ncinga.chatservice.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ncinga.chatservice.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.http.*;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import io.jsonwebtoken.Claims;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;


@Service
@Slf4j
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final UserDetailsService userDetailsService;
    private String secretKey;
    private final OAuth2AuthorizedClientManager authorizedClientManager;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.registration.google.scope}")
    private String scope;

    /*
    @Value("${spring.security.oauth2.client.provider.google.token-uri}")
    private String tokenUri;

    @Value("${spring.security.oauth2.client.registration.google.scope}")
    private String scope;
    */
//
//    public JwtServiceImpl(UserDetailsService userDetailsService, OAuth2AuthorizedClientManager authorizedClientManager) {
//        try {
//            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
//            SecretKey sk = keyGen.generateKey();
//            secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//
//        this.authorizedClientManager = authorizedClientManager;
//        this.userDetailsService = userDetailsService;
//    }



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

    @Override
    public String generateAzureADToken() {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId("azure")
                .principal("Microsoft Graph API")
                .build();

        OAuth2AccessToken accessToken = this.authorizedClientManager
                .authorize(authorizeRequest)
                .getAccessToken();

        return accessToken.getTokenValue();
    }

    @Override
    public String generateGoogleToken() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            final String tokenUrl = "https://oauth2.googleapis.com/token";


            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("client_id", clientId);
            requestBody.add("client_secret", clientSecret);
            requestBody.add("code", "authCode");
            requestBody.add("grant_type", "authorization_code");
            requestBody.add("redirect_uri", "https://oauth.pstmn.io/v1/callback");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, String.class);

            log.info("{}", response);

            return new JSONObject(response.getBody()).getString("access_token");
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return null;
    }

//    public String generateGoogleAuthCode() {
//        try{
//            String url = "https://accounts.google.com/o/oauth2/auth?client_id=127088301323-qikkinmthk10a3aj5nvc867sbha5quoa.apps.googleusercontent.com&redirect_uri=http://localhost:8081/login/oauth2/code/google&response_type=code&scope=https://www.googleapis.com/auth/admin.directory.user&access_type=offline&prompt=consent";
//            RestTemplate restTemplate = new RestTemplate();
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            Map<String, Object> requestBody = new HashMap<>();
//
//            requestBody.put("email", "damiru@gws.nsinga.com");
//            requestBody.put("password", "Ncinga4321");
//
//            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
//
//            log.info("{}", response.getHeaders());
//
//            return response.getBody();
//
////            ObjectMapper objectMapper = new ObjectMapper();
////            JsonNode rootNode = objectMapper.readTree(response.getBody());
//
////            if (rootNode.has("scope")) {
////                return rootNode.get("scope").asText(); // Extract and return the scope
////            } else {
////                throw new RuntimeException("Scope not found in response!");
////            }
//        }catch(Exception e){
//            throw new RuntimeException(e);
//        }

    public String generateGoogleAuthCode() {
    // Encode scopes and redirect URI to handle special characters
    String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
    String encodedScopes = URLEncoder.encode(String.join(" ", scope), StandardCharsets.UTF_8);

    // Build the Google OAuth2 authorization URL
    return UriComponentsBuilder.fromHttpUrl("https://accounts.google.com/o/oauth2/v2/auth")
            .queryParam("client_id", clientId)
            .queryParam("redirect_uri", encodedRedirectUri)
            .queryParam("response_type", "code")
            .queryParam("scope", encodedScopes)
            .queryParam("access_type", "offline")  // To get a refresh token
            .queryParam("prompt", "consent")  // Force consent screen on each login
            .build()
            .toUriString();
    }
}

