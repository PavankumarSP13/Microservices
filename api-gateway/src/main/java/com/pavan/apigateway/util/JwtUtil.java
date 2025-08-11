package com.pavan.apigateway.util;

import com.pavan.apigateway.config.AuthenticationFilter;
import com.pavan.apigateway.exceptions.UnauthorizedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    /**
     *
     * @param token
     * @throws io.jsonwebtoken.JwtException
     */
    public void validateToken(final String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            // Token expired
            throw new UnauthorizedException("Token expired", e);
        } catch (UnsupportedJwtException e) {
            // Token is not supported (e.g., wrong format or algorithm)
            throw new UnauthorizedException("Unsupported JWT token", e);
        } catch (MalformedJwtException e) {
            // Token is malformed (e.g., corrupted string)
            throw new UnauthorizedException("Malformed JWT token", e);
        } catch (SignatureException e) {
            // Signature doesn't match
            throw new UnauthorizedException("Invalid JWT signature", e);
        } catch (IllegalArgumentException e) {
            // Token is null or empty
            throw new UnauthorizedException("Illegal JWT token", e);
        }
    }


    private Key getSignKey() {
//        String secretKey = generateSecretKey();
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateSecretKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey secretKey = keyGen.generateKey();
            String base64Key = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            return base64Key;
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error generating secret key: {}", e.getMessage());
            throw new RuntimeException("Failed to generate secret key: "+e.getMessage());
        }
    }
}
