package com.pavan.questionservice.util;

import com.pavan.questionservice.exceptions.UnauthorizedException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    public Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
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
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

