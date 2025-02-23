package com.example.JWT_Login_with_Spring_Boot.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;



// mark the class as service provider (Business Logic layer)
// the class must contain business logic & should be managed by Spring
@Service
// JwtService: handles JWT-related logic (e.g., token generation, validation, extracting info)
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;


    
    // 🔹 Public API (Methods used by other classes)
    
    // Generate JWT token with default claims
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    // Generate JWT token with additional claims provided
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    // Extract Username (Subject) from JWT token
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    // Extract a specific claim from JWT token using a Function
    public<T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Check if the token is valid or not by verifying the username and expiration status 
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token); 
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Return the configured expiration time (configured in application.properties) for JWT tokens
    public long getExpirationTime(){
        return jwtExpiration;
    } 
    

    
    // 🔹 Private Helper Methods (Internal logic)
    
    // Return the signing key used for token generation and validation
    private SecretKey getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes); // Keys.hmacShaKeyFor() returns a SecretKey not Key
    }

    // Parses JWT token and retrieves all claims
    private Claims extractAllClaims(String token){
        return Jwts
                .parser() // Start parsing the token
                .verifyWith(getSignInKey()) // Set signing key for verification
                .build() //Build parser
                .parseSignedClaims(token) // Parse the JWT
                .getPayload(); // Return the claims inside the token
    }

    /* 
    // Parses JWT token and retrieves all claims (deprecated version)
    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder() // Start parsing the token
                .setSigningKey(getSignInKey()) // Set signing key for verification
                .build() //Build parser
                .parseClaimsJws(token) // Parse the JWT
                .getBody(); // Return the claims inside the token
    }
    */

    // Build a JWT token using claims, user details, and expiration time
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts
                .builder() // Start building the JWT
                .claims(extraClaims) // Add extra claims (if any)
                .subject(userDetails.getUsername()) // Set the username (Subject)
                .issuedAt(new Date(System.currentTimeMillis())) // Set issued time to current time
                .expiration(new Date(System.currentTimeMillis() + expiration)) // Set expiration time
                .signWith(getSignInKey()) // Sign token using secret key (HS256 (32 bytes =
                                                                    // 256 bits) and no need to manually specify it)
                .compact(); // Generate final JWT string
    }
    
    /* 
    // Build a JWT token using claims, user details, and expiration time (deprecated version)
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts
                .builder() // Start building the JWT
                .setClaims(extraClaims) // Add extra claims (if any)
                .setSubject(userDetails.getUsername()) // Set the username (Subject)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Set issued time to current time
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Set expiration time
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Sign token using secret key
                .compact(); // Generate final JWT string
    }
    */       

    // Extract the expiration date from a token
    private Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    // Check if a token is expired
    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
     
    

}
