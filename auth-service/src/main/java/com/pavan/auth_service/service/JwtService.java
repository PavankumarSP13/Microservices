package com.pavan.auth_service.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private String secretKey;

    public JwtService(){
        secretKey = generateSecretKey();
    }

    public String generateSecretKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey secretKey = keyGen.generateKey();
            String base64Key  = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            System.out.println("Base64 Secret Key: " + base64Key );
            return base64Key;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating secret key", e);
        }
    }


    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>(); // Claims can include custom data (e.g., roles, permissions)
        String token = createToken(claims, username); // Adding custom claim
        return token;
    }

    public String createToken(Map<String, Object> claims, String userName){
        return Jwts.builder()
                .setClaims(claims) // Add claims to the token
                .setSubject(userName) // Set the subject (e.g., the username)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Current time as issue time
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // Token expiration time (30 min's)
                .signWith(getKey(), SignatureAlgorithm.HS256) // Sign the token with the secret key
                .compact(); // Generate the token
    }

    private Key getKey() {
        String secretKey = "lWA3EDnA6JvPXPhlqyitOjOADs5ys56ewesXJSurv5k=";
//        String secretKey = generateSecretKey();
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


//    public boolean validateToken(String token, UserDetails userDetails) {
//        final String userName = extractUserName(token);
//        //username(passing through token) got from the token, need to verify in the DB & Not Expired
//        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }

    public void validateToken(String token) {
        Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
    }

    public String extractJwtToken(HttpServletRequest r){
        String headers = r.getHeader("Authorization");
        if(headers == null || !headers.startsWith("Bearer ")) throw new RuntimeException("Missing token");
        return headers.substring(7);
    }

    public String extractUsername(String token) {
        // extract the username from jwt token
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build().parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}



